package org.yrti.notification.strategy.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yrti.events.event.OrderCancelledEvent;
import org.yrti.notification.service.EmailService;
import org.yrti.notification.strategy.EmailStrategy;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelledEmailStrategy implements EmailStrategy<OrderCancelledEvent> {

    private final EmailService emailService;

    @Override
    public boolean supports(Class<?> eventType) {
        return OrderCancelledEvent.class.equals(eventType);
    }

    @Override
    public void sendEmail(OrderCancelledEvent event) {
        String to = event.email();
        String subject = "Заказ отменен";
        String body = "Ваш заказ №" + event.orderId() + " успешно отменен. Ждем Вас снова!";
        emailService.send(to, subject, body);
        log.debug("Отправлено уведомление об отмене заказа на {}", to);
    }
}
