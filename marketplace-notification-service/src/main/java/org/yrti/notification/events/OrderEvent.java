package org.yrti.notification.events;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record OrderEvent(
    Long orderId,
    String email,
    OrderEventType eventType,
    BigDecimal amount
) implements Serializable {}
