package org.yrti.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PricingResponse {
    @NotNull(message = "Product ID is required")
    private Long productId;
    @Min(value = 1, message = "Price must be at least 1")
    private Double originalPrice;
    @Min(value = 1, message = "Price must be at least 1")
    private Double discountedPrice;
}
