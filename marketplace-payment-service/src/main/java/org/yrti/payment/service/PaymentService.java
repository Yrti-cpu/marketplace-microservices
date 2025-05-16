package org.yrti.payment.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.payment.client.OrderClient;
import org.yrti.payment.dto.OrderRequest;
import org.yrti.payment.dto.PaymentRequest;
import org.yrti.payment.dto.PaymentResponse;
import org.yrti.payment.events.PaymentEvent;
import org.yrti.payment.exception.PaymentException;
import org.yrti.payment.kafka.PaymentEventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentEventPublisher eventPublisher;
  private final OrderClient orderClient;

  /**
   * Обрабатывает платеж для указанного заказа.
   *
   * @param request данные платежа (сумма, пользователь и др.)
   * @param orderId идентификатор заказа
   * @return PaymentResponse
   * @see OrderClient#getOrderById(Long)
   * @see PaymentEventPublisher#publish(PaymentEvent)
   */
  public PaymentResponse processPayment(PaymentRequest request, Long orderId) {
    try {

      OrderRequest order = orderClient.getOrderById(orderId); // Получаем данные заказа

      validatePayment(request, order); // Валидация платежа

      // Имитация обработки платежа (в реальной системе - вызов платежного шлюза)
      boolean isSuccess = processPaymentWithProvider(request, orderId);

      // Публикация события о платеже
      publishPaymentEvent(request, orderId, isSuccess);

      return PaymentResponse.builder()
          .orderId(orderId)
          .customerId(request.getUserId())
          .payStatus(isSuccess ? "SUCCESS" : "FAIL")
          .totalPrice(request.getAmount())
          .build();

    } catch (FeignException.NotFound e) {
      throw new PaymentException("Заказ не найден");
    }
  }

  /**
   * Валидирует платеж перед обработкой.
   *
   * @param request данные платежа
   * @param order   данные заказа
   * @throws PaymentException если валидация не пройдена
   */
  private void validatePayment(PaymentRequest request, OrderRequest order) {
    if (!order.getCustomerId().equals(request.getUserId())) {
      throw new PaymentException("Заказ принадлежит другому пользователю");
    }

    if (!order.getStatus().equals("NEW")) {
      throw new PaymentException("Заказ уже оплачен или отменен");
    }

    if (!order.getTotalPrice().equals(request.getAmount())) {
      throw new PaymentException("Сумма оплаты не соответствует стоимости заказа");
    }
  }

  /**
   * Публикует событие о результате платежа.
   *
   * @param request   данные платежа
   * @param orderId   ID заказа
   * @param isSuccess результат обработки платежа
   */
  private void publishPaymentEvent(PaymentRequest request, Long orderId, boolean isSuccess) {
    PaymentEvent event = PaymentEvent.builder()
        .orderId(orderId)
        .userId(request.getUserId())
        .success(isSuccess)
        .amount(request.getAmount())
        .message(isSuccess ? "Оплата прошла успешно" : "Оплата не прошла")
        .build();

    eventPublisher.publish(event);
  }

  /**
   * Имитирует обработку платежа через платежного провайдера. В реальной системе здесь будет вызов
   * API платежного шлюза.
   *
   * @param request данные платежа
   * @param orderId ID заказа
   * @return true если платеж успешен, false если отклонен
   */
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