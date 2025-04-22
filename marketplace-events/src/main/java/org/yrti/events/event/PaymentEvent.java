package org.yrti.events.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentEvent(Long orderId, Long userId, boolean success, BigDecimal amount, String message) {}