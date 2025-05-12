package org.yrti.notification.events;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record OrderCancelledEvent(Long orderId, Long userId, String email,
                                  String message) implements
    Serializable {

}
