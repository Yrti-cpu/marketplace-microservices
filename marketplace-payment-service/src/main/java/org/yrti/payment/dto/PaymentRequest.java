package org.yrti.payment.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentRequest {
  private Long userId;
  private BigDecimal amount;
}