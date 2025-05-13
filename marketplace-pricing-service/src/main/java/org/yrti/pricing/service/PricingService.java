package org.yrti.pricing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
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

  public List<PricingResponse> getPriceBatch(List<Long> productIds) {
    if (productIds.isEmpty()) {
      return List.of();
    }

    return productIds.stream().map(this::getPrice).collect(Collectors.toList());
  }

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

  private BigDecimal applyDiscount(BigDecimal price, BigDecimal discountPercent) {
    return price.multiply(BigDecimal.ONE.subtract(discountPercent))
        .setScale(2, RoundingMode.HALF_UP);
  }
}
