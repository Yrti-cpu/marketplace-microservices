package org.yrti.order.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private String email;
    private String message;
}
