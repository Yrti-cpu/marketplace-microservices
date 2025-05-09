package org.yrti.notification.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yrti.notification.events.SellerEvent;
import org.yrti.notification.service.EmailService;
import org.yrti.notification.strategy.EmailStrategy;

@Slf4j
@Component
@RequiredArgsConstructor
public class SellerEmailStrategy implements EmailStrategy<SellerEvent> {

  private final EmailService emailService;

  @Override
  public boolean supports(Class<?> eventType) {
    return SellerEvent.class.equals(eventType);
  }

  @Override
  public void sendEmail(SellerEvent event) {
    String to = event.email();
    String subject = "Товар куплен";
    String body = "Ваш товар преобрели!";
    emailService.send(to, subject, body);
    log.debug("Отправлено уведомление продавцу о покупке товара на {}", to);

  }
}
