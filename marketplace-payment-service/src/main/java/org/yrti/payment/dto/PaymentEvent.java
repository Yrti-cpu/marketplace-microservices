package org.yrti.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentEvent {
    private Long orderId;
    private Long userId;
    private boolean success;
    private String message;
}