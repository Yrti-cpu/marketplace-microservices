package org.yrti.notification.strategy.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yrti.events.event.OrderPaidEvent;
import org.yrti.notification.service.EmailService;
import org.yrti.notification.strategy.EmailStrategy;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidEmailStrategy implements EmailStrategy<OrderPaidEvent> {

  private final EmailService emailService;

  @Override
  public boolean supports(Class<?> eventType) {
    return OrderPaidEvent.class.equals(eventType);
  }

  @Override
  public void sendEmail(OrderPaidEvent event) {
    String to = event.email();
    String subject = "Заказ оплачен";
    String body = "Ваш заказ №" + event.orderId() + " успешно оплачен на сумму: " + event.amount()
        + ". Спасибо за покупку!";
    emailService.send(to, subject, body);
    log.debug("Отправлено уведомление об оплате заказа на {}", to);

  }
}
