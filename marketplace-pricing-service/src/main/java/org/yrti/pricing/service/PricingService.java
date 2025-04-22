package org.yrti.pricing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.pricing.dto.PricingResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PricingService {

    // Заглушка на скидочные товары
    private static final List<Long> DISCOUNTED_PRODUCTS = List.of(1L, 2L, 3L, 4L, 5L);

    // Заглушка на цены товаров
    private static final Map<Long, BigDecimal> PRODUCT_PRICES = Map.of(
            1L, BigDecimal.valueOf(100.0),
            2L, BigDecimal.valueOf(150.0),
            3L, BigDecimal.valueOf(200.0),
            4L, BigDecimal.valueOf(300.0),
            5L, BigDecimal.valueOf(500.0)
    );

    public PricingResponse getPrice(Long productId) {
        log.debug("Запрос цены: productId={}", productId);

        BigDecimal defaultPrice = BigDecimal.valueOf(999.0);
        BigDecimal price = PRODUCT_PRICES.getOrDefault(productId, defaultPrice);

        BigDecimal discountMultiplier = BigDecimal.valueOf(0.8);
        BigDecimal finalPrice = DISCOUNTED_PRODUCTS.contains(productId)
                ? price.multiply(discountMultiplier)
                : price;

        return new PricingResponse(productId, price, finalPrice);
    }
}
