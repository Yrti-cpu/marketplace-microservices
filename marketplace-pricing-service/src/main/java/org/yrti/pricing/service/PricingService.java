package org.yrti.pricing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

  public PricingResponse getPrice(Long productId) {
    log.debug("Запрос цены: productId={}", productId);
    Price price = priceRepository.findByProductId(productId)
        .orElseThrow(() -> new PriceNotFoundException(productId));

    Optional<Discount> activeDiscount = discountRepository.findByProductId(productId)
        .stream()
        .filter(Discount::isCurrentlyActive)
        .findFirst();

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
