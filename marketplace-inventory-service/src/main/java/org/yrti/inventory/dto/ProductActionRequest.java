package org.yrti.inventory.dto;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Hidden
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductActionRequest {

  @NotNull(message = "ID товара обязателен")
  private Long productId;

  @Min(value = 1, message = "Количество должно быть не меньше 1")
  private Integer quantity;
}
