package org.yrti.pricing.service;

import org.springframework.stereotype.Service;
import org.yrti.pricing.dto.PricingResponse;

import java.util.List;
import java.util.Map;

@Service
public class PricingService {

    // Заглушка на скидочные товары
    private static final List<Long> DISCOUNTED_PRODUCTS = List.of(1L, 3L, 5L);

    // Заглушка на цены товаров
    private static final Map<Long, Double> PRODUCT_PRICES = Map.of(
            1L, 100.0,
            2L, 150.0,
            3L, 200.0,
            4L, 300.0,
            5L, 500.0
    );

    public PricingResponse getPrice(Long productId) {
        Double price = PRODUCT_PRICES.getOrDefault(productId, 999.0); // если нет цены — фиктивная
        Double finalPrice = DISCOUNTED_PRODUCTS.contains(productId)
                ? price * 0.8  // 20%
                : price;
        return new PricingResponse(productId, price, finalPrice);
    }
}
