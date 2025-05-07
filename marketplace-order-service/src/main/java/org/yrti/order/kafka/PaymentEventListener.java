package org.yrti.order.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yrti.order.events.PaymentEvent;
import org.yrti.order.service.PaymentProcessingService;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

  private final PaymentProcessingService paymentProcessingService;

  @KafkaListener(topics = "payment-events", groupId = "order-payment-group")
  public void handlePaymentCreated(PaymentEvent event) {
    log.info("Получено событие оплаты: {}", event);
    paymentProcessingService.processPaymentEvent(event);
  }
}