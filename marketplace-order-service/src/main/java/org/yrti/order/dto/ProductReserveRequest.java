package org.yrti.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReserveRequest {

  @NotNull(message = "Product ID is required")
  private Long productId;
  @Min(value = 1, message = "Quantity must be at least 1")
  private Integer quantity;
}
