package org.yrti.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
}