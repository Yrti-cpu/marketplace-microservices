package org.yrti.order.dto;

import lombok.Data;

@Data
public class PricingResponse {
    private Long productId;
    private Double originalPrice;
    private Double discountedPrice;
}
