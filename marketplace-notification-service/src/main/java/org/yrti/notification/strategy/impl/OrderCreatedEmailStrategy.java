package org.yrti.notification.strategy.impl;

import org.springframework.stereotype.Component;
import org.yrti.notification.events.OrderEvent;
import org.yrti.notification.events.OrderEventType;
import org.yrti.notification.service.EmailService;

/**
 * Стратегия для отправки email-уведомлений о создании заказов.
 * Отправляет клиентам письма с подтверждением оформления заказа.
 */
@Component
public class OrderCreatedEmailStrategy extends BaseEmailStrategy {

  public OrderCreatedEmailStrategy(EmailService emailService) {
    super(emailService, OrderEventType.CREATED);
  }

  @Override
  public void sendEmail(OrderEvent event) {
    String body = String.format(
        "Ваш заказ №%d успешно оформлен. Сумма для заказа: %s. Спасибо за покупку!",
        event.orderId(),
        event.amount()
    );
    emailService.send(event.email(), "Заказ оформлен", body);
  }
}