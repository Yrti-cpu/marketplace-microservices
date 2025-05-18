package org.yrti.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yrti.notification.events.OrderEvent;
import org.yrti.notification.strategy.EmailEventDispatcher;

/**
 * Kafka-слушатель для обработки событий доставки заказов. Получает события из топика
 * "order-delivered" и запускает процесс уведомления клиента.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeliveredListener {

  private final EmailEventDispatcher dispatcher;

  @KafkaListener(topics = "order-delivered", groupId = "notification-order-delivered")
  public void listen(OrderEvent event) {
    log.debug("[order-delivered] Получено событие: {}", event);
    dispatcher.dispatchEmail(event);
  }
}
