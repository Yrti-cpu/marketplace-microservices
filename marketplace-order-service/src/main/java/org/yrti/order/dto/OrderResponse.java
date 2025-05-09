package org.yrti.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.yrti.order.model.OrderStatus;

@Data
@AllArgsConstructor
public class OrderResponse {

  @NotNull
  private Long orderId;
  @NotNull
  private Long customerId;
  @NotBlank
  private OrderStatus status;
  @Min(value = 1, message = "Price must be at least 1")
  private BigDecimal totalPrice;

}
