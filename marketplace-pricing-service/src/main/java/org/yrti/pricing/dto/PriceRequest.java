package org.yrti.pricing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PriceRequest {

  @NotNull
  private Long productId;

  @NotNull
  @DecimalMin("0.01")
  private BigDecimal amount;
}
