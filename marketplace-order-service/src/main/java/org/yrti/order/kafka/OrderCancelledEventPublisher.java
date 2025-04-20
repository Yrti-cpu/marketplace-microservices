package org.yrti.order.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.yrti.events.event.OrderCancelledEvent;

@Component
@RequiredArgsConstructor
public class OrderCancelledEventPublisher {
    private final KafkaTemplate<String, OrderCancelledEvent> kafkaTemplate;

    public void publish(OrderCancelledEvent event) {
        kafkaTemplate.send("order-cancelled", event);
    }
}