package org.yrti.notification.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yrti.events.event.OrderCreatedEvent;
import org.yrti.notification.service.EmailService;
import org.yrti.notification.strategy.EmailStrategy;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEmailStrategy implements EmailStrategy<OrderCreatedEvent> {

    private final EmailService emailService;

    @Override
    public boolean supports(Class<?> eventType) {
        return OrderCreatedEvent.class.equals(eventType);
    }

    @Override
    public void sendEmail(OrderCreatedEvent event) {
        String to = event.email();
        String subject = "Заказ оформлен";
        String body = "Ваш заказ №" + event.orderId() + " успешно оформлен. Спасибо за покупку!";
        emailService.send(to, subject, body);
        log.debug("Отправлено уведомление о создании заказа на {}", to);
    }
}
