package org.yrti.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yrti.events.event.OrderCancelledEvent;
import org.yrti.events.event.OrderCreatedEvent;
import org.yrti.events.event.OrderDeliveredEvent;
import org.yrti.events.event.OrderPaidEvent;
import org.yrti.notification.service.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventListener {
    //TODO-Крит ты прям в этом классе ловишь сообщения, лепишь новые и отправляешь сообщения на почту. У тебя класс слишком перегружен ответственностью
    // Во-первых - на один топик один класс.
    // Во-вторых - метод с аннотацией @KafkaListener обычно хватает сообщение и далее передает его на обработку другому сервису. Тут прослеживается потенциал для паттерна Стратегия. В зависимости от класса сообщения будет вызываться тот или иной сервис, которые зашлет сообщение на почту.
    private final EmailService emailService;

    // TODO-Минор лучше для разных топиков иметь уникальный groupId, что бы можно было удобно отслеживать лаг консьюмера в рамках одного топика на всю группу, а не в рамках нескольких топиков на всю группу.
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

    @KafkaListener(topics = "order-cancelled", groupId = "notification-group")
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("Получено событие отмены заказа: {}", event);

        String to = event.getEmail();
        String subject = "Отмена заказа";
        String body = event.getMessage();

        emailService.send(to, subject, body);
    }
}
