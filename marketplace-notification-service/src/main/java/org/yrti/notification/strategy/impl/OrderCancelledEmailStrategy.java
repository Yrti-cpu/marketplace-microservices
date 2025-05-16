package org.yrti.notification.strategy.impl;

import org.springframework.stereotype.Component;
import org.yrti.notification.events.OrderEvent;
import org.yrti.notification.events.OrderEventType;
import org.yrti.notification.service.EmailService;

/**
 * Стратегия для отправки email-уведомлений об отмене заказов.
 * Генерирует и отправляет письма клиентам при отмене их заказов.
 */
@Component
public class OrderCancelledEmailStrategy extends BaseEmailStrategy {

  public OrderCancelledEmailStrategy(EmailService emailService) {
    super(emailService, OrderEventType.CANCELLED);
  }

  @Override
  public void sendEmail(OrderEvent event) {
    String body = String.format(
        "Ваш заказ №%d был отменен. Если это ошибка, свяжитесь с поддержкой.",
        event.orderId()
    );
    emailService.send(event.email(), "Заказ отменен", body);
  }
}