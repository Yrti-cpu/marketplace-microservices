package org.yrti.notification.events;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record OrderCreatedEvent(Long orderId, Long userId, String email, String message) implements
    Serializable {

}
