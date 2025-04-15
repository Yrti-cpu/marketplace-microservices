package org.yrti.notification.event;

import lombok.Data;

@Data
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private String email;
    private String message;
}
