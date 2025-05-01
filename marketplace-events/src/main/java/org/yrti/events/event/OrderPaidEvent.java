package org.yrti.events.event;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record OrderPaidEvent(Long orderId, Long userId, String email, BigDecimal amount) {

}
