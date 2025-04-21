package org.yrti.events.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaidEvent {
    private Long orderId;
    private Long userId;
    private String email;
    private Double amount; // TODO-Крит при работе с баблом надо юзать BigDecimal!!! Это в банках любят спрашивать
}

// TODO-Минор почему не используешь record классы ?
