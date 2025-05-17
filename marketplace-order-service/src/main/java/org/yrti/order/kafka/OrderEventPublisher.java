package org.yrti.order.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.yrti.order.events.OrderEvent;

/**
 * Публикатор событий, связанных с заказами, в Kafka. Отправляет события в соответствующие топики на
 * основе типа события. Формат топиков: order-{тип_события} (например, order-created, order-paid)
 */
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

  private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

  public void publish(OrderEvent event) {
    String topic = "order-" + event.eventType().name().toLowerCase();
    kafkaTemplate.send(topic, event);
  }
}
