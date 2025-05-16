package org.yrti.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrderRequest {

  @NotNull
  private Long userId;

  @NotNull
  private List<OrderItemRequest> items;

  @NotBlank
  private String address;

  @Data
  @AllArgsConstructor
  public static class OrderItemRequest {

    @NotNull(message = "ID продукта обязателен")
    private Long productId;

    @Min(value = 1, message = "Количество не меньше 1")
    private Integer quantity;
  }

}
