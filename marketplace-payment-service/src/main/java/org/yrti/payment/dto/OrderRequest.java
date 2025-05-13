package org.yrti.payment.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRequest {

  private Long orderId;
  private Long customerId;
  private String status;
  private BigDecimal totalPrice;

}
