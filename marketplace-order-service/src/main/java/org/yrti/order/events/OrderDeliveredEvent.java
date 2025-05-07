package org.yrti.order.events;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record OrderDeliveredEvent(Long orderId, Long userId, String email) implements
    Serializable {

}
