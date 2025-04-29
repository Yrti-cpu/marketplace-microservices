package org.yrti.events.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderPaidEvent(Long orderId, Long userId, String email, BigDecimal amount) {
}
