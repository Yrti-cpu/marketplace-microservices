package org.yrti.payment.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.payment.client.OrderClient;
import org.yrti.payment.dto.OrderRequest;
import org.yrti.payment.dto.OrderStatus;
import org.yrti.payment.dto.PaymentRequest;
import org.yrti.payment.events.PaymentEvent;
import org.yrti.payment.exception.PaymentException;
import org.yrti.payment.kafka.PaymentEventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentEventPublisher eventPublisher;
  private final OrderClient orderClient;

  public boolean processPayment(PaymentRequest request, Long orderId) {
    try {
      OrderRequest order = orderClient.getOrderById(orderId);

      if (!order.getCustomerId().equals(request.getUserId())) {
        throw new PaymentException("Заказ принадлежит другому пользователю");
      }

      if (order.getStatus() != OrderStatus.NEW) {
        throw new PaymentException("Заказ уже оплачен или отменен");
      }

      if (!order.getTotalPrice().equals(request.getAmount())) {
        throw new PaymentException("Сумма оплаты не соответствует стоимости заказа");
      }

      boolean isSuccess = processPaymentWithProvider(request, orderId);

      PaymentEvent event = PaymentEvent.builder()
          .orderId(orderId)
          .userId(request.getUserId())
          .success(isSuccess)
          .amount(request.getAmount())
          .message(isSuccess ? "Оплата прошла успешно" : "Оплата не прошла")
          .build();

      eventPublisher.publish(event);
      return isSuccess;

    } catch (FeignException.NotFound e) {
      throw new PaymentException("Заказ не найден");
    }
  }

  private boolean processPaymentWithProvider(PaymentRequest request, Long orderId) {
    boolean isSuccess = Math.random() > 0.1;
    log.info("Платеж {} для заказа {}. Сумма: {}. Статус: {}",
        isSuccess ? "принят" : "отклонен",
        orderId,
        request.getAmount(),
        isSuccess ? "success" : "failed");
    return isSuccess;
  }
}