package org.yrti.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PricingResponse {
    private Long productId;
    private Double originalPrice;
    private Double discountedPrice;
}
