package org.yrti.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PricingResponse {
    private Long productId;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
}
