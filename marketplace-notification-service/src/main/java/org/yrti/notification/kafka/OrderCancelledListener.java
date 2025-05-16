package org.yrti.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yrti.notification.events.OrderEvent;
import org.yrti.notification.strategy.EmailEventDispatcher;

/**
 * Kafka-слушатель для обработки событий отмены заказов.
 * Получает события из топика "order-cancelled" и делегирует обработку EmailEventDispatcher.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelledListener {

  private final EmailEventDispatcher dispatcher;

  @KafkaListener(topics = "order-cancelled", groupId = "notification-order-cancelled")
  public void listen(OrderEvent event) {
    log.debug("[order-cancelled] Получено событие: {}", event);
    dispatcher.dispatchEmail(event);
  }
}
