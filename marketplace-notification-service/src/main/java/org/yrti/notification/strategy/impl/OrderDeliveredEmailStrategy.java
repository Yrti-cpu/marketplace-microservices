package org.yrti.notification.strategy.impl;

import org.springframework.stereotype.Component;
import org.yrti.notification.events.OrderEvent;
import org.yrti.notification.events.OrderEventType;
import org.yrti.notification.service.EmailService;

/**
 * Стратегия для отправки email-уведомлений о доставке заказов. Информирует клиентов об успешной
 * доставке их заказов.
 */
@Component
public class OrderDeliveredEmailStrategy extends BaseEmailStrategy {

  public OrderDeliveredEmailStrategy(EmailService emailService) {
    super(emailService, OrderEventType.DELIVERED);
  }

  @Override
  public void sendEmail(OrderEvent event) {
    String body = String.format(
        "Ваш заказ №%d успешно доставлен!",
        event.orderId()
    );
    emailService.send(event.email(), "Заказ доставлен", body);
  }
}