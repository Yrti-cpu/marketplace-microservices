package org.yrti.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PricingResponse {

  @NotNull(message = "ID товара обязательно")
  private Long productId;
  @Min(value = 1, message = "Цена не меньше 1")
  private BigDecimal originalPrice;
  @Min(value = 1, message = "Цена со скидкой не меньше 1")
  private BigDecimal discountedPrice;
  private BigDecimal discount;
}
