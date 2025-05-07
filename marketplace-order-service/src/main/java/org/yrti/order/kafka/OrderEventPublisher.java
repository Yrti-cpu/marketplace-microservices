package org.yrti.order.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.yrti.order.events.OrderCreatedEvent;


@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

  private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

  public void publish(OrderCreatedEvent event) {
    kafkaTemplate.send("order-created", event);
  }
}
