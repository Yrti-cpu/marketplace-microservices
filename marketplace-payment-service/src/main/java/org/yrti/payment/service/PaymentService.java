package org.yrti.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.payment.dto.PaymentRequest;
import org.yrti.payment.events.PaymentEvent;
import org.yrti.payment.kafka.PaymentEventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentEventPublisher eventPublisher;

  public boolean processPayment(PaymentRequest request) {
    log.debug("Оплата от клиента userId={} заказа orderId={} на сумму {}", request.getUserId(),
        request.getOrderId(), request.getAmount());
    boolean isSuccess = Math.random() > 0.05;

    PaymentEvent event = PaymentEvent.builder()
        .orderId(request.getOrderId())
        .userId(request.getUserId())
        .success(isSuccess)
        .amount(request.getAmount())
        .message(isSuccess ? "Оплата прошла успешно" : "Оплата не прошла")
        .build();

    eventPublisher.publish(event);
    return isSuccess;
  }
}