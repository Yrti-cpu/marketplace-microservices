package org.yrti.order.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.yrti.order.events.SellerEvent;

/**
 * Публикатор событий, связанных с продавцами, в Kafka.
 */
@Component
@RequiredArgsConstructor
public class SellerEventPublisher {

  private final KafkaTemplate<String, SellerEvent> kafkaTemplate;

  public void publish(SellerEvent event) {
    kafkaTemplate.send("product-sold", event);
  }

}
