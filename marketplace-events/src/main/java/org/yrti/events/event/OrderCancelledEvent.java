package org.yrti.events.event;

import lombok.Builder;

@Builder
public record OrderCancelledEvent(Long orderId, Long userId, String email, String message) {

}
