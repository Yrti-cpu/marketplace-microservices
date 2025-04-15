package org.yrti.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.yrti.notification.event.OrderCreatedEvent;
import org.yrti.notification.service.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventListener {

    private final EmailService emailService;

    @KafkaListener(topics = "order-created", groupId = "notification-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("📩 Получено событие: {}", event);

        String to = event.getEmail();
        String subject = "Заказ оформлен";
        String body = "Ваш заказ №" + event.getOrderId() + " успешно оформлен. Спасибо за покупку!";

        try {
            log.info("📨 Попытка отправки email на {}", to);
            emailService.send(to, subject, body);
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке email: {}", e.getMessage(), e);
        }



    }
}
