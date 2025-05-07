package org.yrti.order.kafka.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.yrti.order.events.PaymentEvent;

public class KafkaTopicDeserializer implements Deserializer<Object> {

  private final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private final Map<String, Class<?>> topicToTypeMap = Map.of(
      "payment-events", PaymentEvent.class
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
