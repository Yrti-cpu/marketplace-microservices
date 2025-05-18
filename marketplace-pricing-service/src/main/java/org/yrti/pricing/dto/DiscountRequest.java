package org.yrti.pricing.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DiscountRequest {

  @NotNull
  private Long productId;

  @NotNull
  @DecimalMin("0.01")
  @DecimalMax("0.99")
  private BigDecimal discount;

  @NotNull
  @Future
  private LocalDateTime startDate;

  @Future
  private LocalDateTime endDate;
}
