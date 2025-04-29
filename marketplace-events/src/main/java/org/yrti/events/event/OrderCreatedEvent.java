package org.yrti.events.event;

import lombok.Builder;

@Builder
public record OrderCreatedEvent(Long orderId, Long userId, String email, String message) {
}
