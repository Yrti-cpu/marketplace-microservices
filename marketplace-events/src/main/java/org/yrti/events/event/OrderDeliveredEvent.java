package org.yrti.events.event;

import lombok.Builder;

@Builder
public record OrderDeliveredEvent(Long orderId, Long userId, String email) {

}
