package org.yrti.pricing.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PricingResponse {

  private Long productId;
  private BigDecimal originalPrice;
  private BigDecimal discountedPrice;
}
