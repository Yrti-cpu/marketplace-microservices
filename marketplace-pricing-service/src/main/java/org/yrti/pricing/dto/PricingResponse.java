package org.yrti.pricing.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PricingResponse {

  private Long productId;
  private BigDecimal originalPrice;
  private BigDecimal discountedPrice;
  private BigDecimal discountPercent;
}
