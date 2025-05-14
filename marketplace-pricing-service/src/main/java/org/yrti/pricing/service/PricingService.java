package org.yrti.pricing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.pricing.dao.DiscountRepository;
import org.yrti.pricing.dao.PriceRepository;
import org.yrti.pricing.dto.PricingResponse;
import org.yrti.pricing.exception.PriceNotFoundException;
import org.yrti.pricing.model.Discount;
import org.yrti.pricing.model.Price;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingService {

  private final PriceRepository priceRepository;
  private final DiscountRepository discountRepository;

  private static final String PRICE_CACHE = "price";
  private static final String DISCOUNT_CACHE = "discount";
  private static final String FINAL_PRICE_CACHE = "final_price";

  @Cacheable(value = PRICE_CACHE, key = "#productId")
  public Price getRawPrice(Long productId) {
    return priceRepository.findByProductId(productId)
        .orElseThrow(() -> new PriceNotFoundException(productId));
  }

  @Cacheable(value = DISCOUNT_CACHE, key = "#productId")
  public Optional<Discount> getActiveDiscount(Long productId) {
    return discountRepository.findByProductId(productId)
        .stream()
        .filter(Discount::isCurrentlyActive)
        .findFirst();
  }

  @Transactional
  @Cacheable(value = FINAL_PRICE_CACHE, key = "#productId")
  public PricingResponse getPrice(Long productId) {
    log.info("Запрос цены: productId={}", productId);
    Price price = getRawPrice(productId);
    Optional<Discount> activeDiscount = getActiveDiscount(productId);

    BigDecimal finalPrice = activeDiscount
        .map(discount -> applyDiscount(price.getAmount(), discount.getDiscount()))
        .orElse(price.getAmount());
    return PricingResponse.builder()
        .productId(productId)
        .originalPrice(price.getAmount())
        .discountedPrice(finalPrice)
        .discount(activeDiscount.map(Discount::getDiscount).orElse(null))
        .build();

  }

  @Transactional(readOnly = true)
  @Cacheable(
      value = "pricing:priceCache",
      key = "{#root.methodName, #productIds.hashCode()}",
      unless = "#result.isEmpty()"
  )
  public List<PricingResponse> getPriceBatch(List<Long> productIds) {
    log.debug("Запрос цены: productIds={}", productIds);
    if (productIds.isEmpty()) {
      return List.of();
    }

    List<Price> prices = priceRepository.findByProductIdIn(productIds);
    LocalDateTime now = LocalDateTime.now();
    List<Discount> activeDiscounts = discountRepository
        .findByProductIdInAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(
            productIds, now, now
        );

    Map<Long, Discount> discountMap = activeDiscounts.stream()
        .collect(Collectors.toMap(Discount::getProductId, Function.identity()));

    return productIds.stream()
        .map(productId -> {
          Price price = prices.stream()
              .filter(p -> p.getProductId().equals(productId))
              .findFirst()
              .orElseThrow(() -> new PriceNotFoundException(productId));

          Discount discount = discountMap.get(productId);
          BigDecimal finalPrice = discount != null
              ? applyDiscount(price.getAmount(), discount.getDiscount())
              : price.getAmount();

          return PricingResponse.builder()
              .productId(productId)
              .originalPrice(price.getAmount())
              .discountedPrice(finalPrice)
              .discount(discount != null ? discount.getDiscount() : null)
              .build();
        })
        .toList();
  }

  private BigDecimal applyDiscount(BigDecimal price, BigDecimal discountPercent) {
    return price.multiply(BigDecimal.ONE.subtract(discountPercent))
        .setScale(2, RoundingMode.HALF_UP);
  }
}
