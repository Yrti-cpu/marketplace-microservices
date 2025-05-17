package org.yrti.payment.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

  private Long orderId;
  private Long customerId;
  private String payStatus;
  private BigDecimal totalPrice;
}