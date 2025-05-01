package org.yrti.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yrti.events.event.OrderPaidEvent;
import org.yrti.notification.strategy.EmailEventDispatcher;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidListener {

  private final EmailEventDispatcher dispatcher;

  @KafkaListener(topics = "order-paid", groupId = "notification-order-paid")
  public void listen(OrderPaidEvent event) {
    log.debug("[order-paid] Получено событие: {}", event);
    dispatcher.dispatchEmail(event);
  }
}
