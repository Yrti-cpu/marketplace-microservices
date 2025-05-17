package org.yrti.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yrti.notification.events.OrderEvent;
import org.yrti.notification.strategy.EmailEventDispatcher;

/**
 * Kafka-слушатель для обработки событий оплаты заказов. Получает события из топика "order-paid" и
 * запускает процесс уведомления.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidListener {

  private final EmailEventDispatcher dispatcher;

  @KafkaListener(topics = "order-paid", groupId = "notification-order-paid")
  public void listen(OrderEvent event) {
    log.debug("[order-paid] Получено событие: {}", event);
    dispatcher.dispatchEmail(event);
  }
}
