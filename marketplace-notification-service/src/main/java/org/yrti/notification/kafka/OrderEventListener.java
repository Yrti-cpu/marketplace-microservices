package org.yrti.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yrti.events.event.OrderCreatedEvent;
import org.yrti.events.event.OrderDeliveredEvent;
import org.yrti.events.event.OrderPaidEvent;
import org.yrti.notification.service.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventListener {

    private final EmailService emailService;

    @KafkaListener(topics = "order-created", groupId = "notification-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Получено событие: {}", event);

        String to = event.getEmail();
        String subject = "Заказ оформлен";
        String body = "Ваш заказ №" + event.getOrderId() + " успешно оформлен. Спасибо за покупку!";

        try {
            log.info("📨 Попытка отправки email на {}", to);
            emailService.send(to, subject, body);
        } catch (Exception e) {
            log.error("Ошибка при отправке email: {}", e.getMessage(), e);
        }

    }
    @KafkaListener(topics = "order-paid", groupId = "notification-group")
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("Получено событие об оплате заказа: {}", event);

        String to = event.getEmail();
        String subject = "Оплата подтверждена";
        String body = "Ваш заказ №" + event.getOrderId() + " успешно оплачен на сумму " +
                event.getAmount() + "₽. Спасибо за покупку!";

        try {
            log.info("📨 Попытка отправки письма с чеком на {}", to);
            emailService.send(to, subject, body);
        } catch (Exception e) {
            log.error("Ошибка при отправке письма с чеком: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "order-delivered", groupId = "notification-group")
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        log.info("Получено событие доставки заказа: {}", event);

        String to = event.getEmail();
        String subject = "Ваш заказ доставлен!";
        String body = "Ваш заказ №" + event.getOrderId() + " успешно доставлен. Приятного дня!";

        try {
            emailService.send(to, subject, body);
            log.info("Уведомление о доставке отправлено на {}", to);
        } catch (Exception e) {
            log.error("Ошибка отправки письма о доставке: {}", e.getMessage(), e);
        }
    }
}
