package org.yrti.notification.kafka.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.yrti.notification.events.OrderCancelledEvent;
import org.yrti.notification.events.OrderCreatedEvent;
import org.yrti.notification.events.OrderDeliveredEvent;
import org.yrti.notification.events.OrderPaidEvent;
import org.yrti.notification.events.SellerEvent;

public class KafkaTopicDeserializer implements Deserializer<Object> {

  private final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private final Map<String, Class<?>> topicToTypeMap = Map.of(
      "order-cancelled", OrderCancelledEvent.class,
      "order-delivered", OrderDeliveredEvent.class,
      "order-created", OrderCreatedEvent.class,
      "order-paid", OrderPaidEvent.class,
      "product-sold", SellerEvent.class
  );

  @Override
  public Object deserialize(String topic, byte[] data) {
    try {
      Class<?> targetType = topicToTypeMap.get(topic);
      if (targetType == null) {
        throw new IllegalStateException("Нет сопоставления типов для топика: " + topic);
      }
      return objectMapper.readValue(data, targetType);
    } catch (IOException e) {
      throw new SerializationException("Ошибка десериализации сообщения", e);
    }
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public void close() {
  }
}
