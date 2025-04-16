package org.yrti.order.event;

import lombok.Data;

@Data
public class PaymentEvent {
    private Long orderId;
    private Long userId;
    private boolean success;
    private double amount;
    private String message;
}