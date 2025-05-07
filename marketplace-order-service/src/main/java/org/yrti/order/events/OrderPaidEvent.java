package org.yrti.order.events;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record OrderPaidEvent(Long orderId, Long userId, String email, BigDecimal amount) implements
    Serializable {

}
