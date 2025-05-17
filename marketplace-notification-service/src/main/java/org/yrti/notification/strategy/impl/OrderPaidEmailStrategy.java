package org.yrti.notification.strategy.impl;

import org.springframework.stereotype.Component;
import org.yrti.notification.events.OrderEvent;
import org.yrti.notification.events.OrderEventType;
import org.yrti.notification.service.EmailService;

/**
 * Стратегия для отправки email-уведомлений об оплате заказов.
 * Отправляет клиентам подтверждение успешной оплаты заказа.
 */
@Component
public class OrderPaidEmailStrategy extends BaseEmailStrategy {

  public OrderPaidEmailStrategy(EmailService emailService) {
    super(emailService, OrderEventType.PAID);
  }

  @Override
  public void sendEmail(OrderEvent event) {
    String body = String.format(
        "Ваш заказ №%d успешно оплачен на сумму: %s. Спасибо за покупку!",
        event.orderId(),
        event.amount()
    );
    emailService.send(event.email(), "Заказ оплачен", body);
  }
}