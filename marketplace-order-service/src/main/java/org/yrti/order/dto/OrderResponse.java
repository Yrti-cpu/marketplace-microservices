package org.yrti.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponse {

  @NotNull
  private Long orderId;
  @NotNull
  private Long customerId;
  @NotBlank
  private String status;
  @Min(value = 1, message = "Цена должна быть не меньше 1")
  private BigDecimal totalPrice;

}
