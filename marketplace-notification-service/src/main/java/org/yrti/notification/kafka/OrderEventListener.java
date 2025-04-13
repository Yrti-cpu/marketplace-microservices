package org.yrti.notification.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yrti.notification.event.OrderCreatedEvent;

@Slf4j
@Service
public class OrderEventListener {
    @KafkaListener(topics = "order-created", groupId = "notification-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("📩 Получено событие: {}", event);

    }
}
