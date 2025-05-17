package org.yrti.order.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yrti.order.events.PaymentEvent;
import org.yrti.order.service.PaymentProcessingService;

/**
 * Слушатель событий оплаты из Kafka.
 * Обрабатывает платежи и делегирует их обработку в PaymentProcessingService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

  private final PaymentProcessingService paymentProcessingService;

  @KafkaListener(topics = "payment-events", groupId = "order-payment-group")
  public void handlePaymentCreated(PaymentEvent event) {
    log.debug("Получено событие оплаты: {}", event);
    paymentProcessingService.processPaymentEvent(event);
  }
}