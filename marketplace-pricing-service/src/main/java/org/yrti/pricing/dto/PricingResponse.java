package org.yrti.pricing.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class PricingResponse {

  private Long productId;
  private BigDecimal originalPrice;
  private BigDecimal discountedPrice;
  private BigDecimal discount;
}
