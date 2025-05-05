package org.yrti.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yrti.events.event.OrderCreatedEvent;
import org.yrti.notification.strategy.EmailEventDispatcher;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

  private final EmailEventDispatcher dispatcher;

  @KafkaListener(topics = "order-created", groupId = "notification-order-created")
  public void listen(OrderCreatedEvent event) {
    log.debug("[order-created] Получено событие: {}", event);
    dispatcher.dispatchEmail(event);
  }
}
