package org.yrti.notification.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yrti.notification.events.OrderDeliveredEvent;
import org.yrti.notification.service.EmailService;
import org.yrti.notification.strategy.EmailStrategy;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeliveredEmailStrategy implements EmailStrategy<OrderDeliveredEvent> {

  private final EmailService emailService;

  @Override
  public boolean supports(Class<?> eventType) {
    return OrderDeliveredEvent.class.equals(eventType);
  }

  @Override
  public void sendEmail(OrderDeliveredEvent event) {
    String to = event.email();
    String subject = "Заказ доставлен";
    String body = "Ваш заказ №" + event.orderId() + " успешно доставлен. Спасибо за покупку!";
    emailService.send(to, subject, body);
    log.debug("Отправлено уведомление о доставке заказа на {}", to);
  }
}
