package org.yrti.notification.strategy.impl;

import org.springframework.stereotype.Component;
import org.yrti.notification.events.OrderEvent;
import org.yrti.notification.events.OrderEventType;
import org.yrti.notification.service.EmailService;

/**
 * Специальная стратегия для отправки email-уведомлений продавцам. Не поддерживает стандартные
 * события заказов, только специализированные уведомления для продавцов.
 */
@Component
public class SellerEmailStrategy extends BaseEmailStrategy {

  public SellerEmailStrategy(EmailService emailService) {
    super(emailService, null);
  }

  @Override
  public void sendEmail(OrderEvent event) {
    throw new IllegalStateException("Не поддерживает тип событий +" + event.toString());
  }


  @Override
  public boolean supports(OrderEventType eventType) {
    return false;
  }
}