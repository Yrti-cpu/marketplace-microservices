package org.yrti.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yrti.events.event.OrderDeliveredEvent;
import org.yrti.notification.strategy.EmailEventDispatcher;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeliveredListener {

  private final EmailEventDispatcher dispatcher;

  @KafkaListener(topics = "order-delivered", groupId = "notification-order-delivered")
  public void listen(OrderDeliveredEvent event) {
    log.debug("[order-delivered] Получено событие: {}", event);
    dispatcher.dispatchEmail(event);
  }
}
