package org.yrti.order.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.yrti.order.event.OrderPaidEvent;

@Component
@RequiredArgsConstructor
public class OrderPaidEventPublisher {

    private final KafkaTemplate<String, OrderPaidEvent> kafkaTemplate;

    public void publish(OrderPaidEvent event) {
        kafkaTemplate.send("order-paid", event);
    }
}