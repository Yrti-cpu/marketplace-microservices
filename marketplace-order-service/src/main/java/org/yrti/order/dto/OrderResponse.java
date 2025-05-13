package org.yrti.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
  @Pattern(regexp = "NEW|CANCELLED|PAID|PAYMENT_FAILED|DISPATCHED|DELIVERED", message = "Invalid order status")
  private String status;
  @Min(value = 1, message = "Price must be at least 1")
  private BigDecimal totalPrice;

}
