package org.yrti.events.event;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record PaymentEvent(Long orderId, Long userId, boolean success, BigDecimal amount,
                           String message) {

}