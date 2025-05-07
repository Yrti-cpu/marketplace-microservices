package org.yrti.payment.events;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record PaymentEvent(Long orderId, Long userId, boolean success, BigDecimal amount,
                           String message) implements
    Serializable {

}