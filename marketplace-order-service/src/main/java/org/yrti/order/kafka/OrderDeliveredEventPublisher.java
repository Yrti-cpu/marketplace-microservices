package org.yrti.order.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.yrti.events.event.OrderDeliveredEvent;

@Component
@RequiredArgsConstructor
public class OrderDeliveredEventPublisher {
    private final KafkaTemplate<String, OrderDeliveredEvent> kafkaTemplate;

    public void publish(OrderDeliveredEvent event) {
        kafkaTemplate.send("order-delivered", event);
    }
}
