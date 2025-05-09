package org.yrti.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yrti.notification.events.SellerEvent;
import org.yrti.notification.strategy.EmailEventDispatcher;

@Slf4j
@Component
@RequiredArgsConstructor
public class SellerEventListener {

  private final EmailEventDispatcher dispatcher;

  @KafkaListener(topics = "product-sold", groupId = "notification-product-sold")
  public void listen(SellerEvent event) {
    log.debug("[product-sold] Получено событие: {}", event);
    dispatcher.dispatchEmail(event);
  }

}
