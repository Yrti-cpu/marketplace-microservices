package org.yrti.order.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yrti.order.client.UserClient;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.event.OrderPaidEvent;
import org.yrti.order.event.PaymentEvent;
import org.yrti.order.service.OrderService;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final UserClient userClient;
    private final OrderPaidEventPublisher orderPaidEventPublisher;
    private final OrderService orderService;

    @KafkaListener(topics = "payment-created", groupId = "order-group")
    public void handlePaymentCreated(PaymentEvent event) {
        log.info("💳 Получено событие оплаты: {}", event);

        if (!event.isSuccess()) {
            log.warn("⚠️ Оплата заказа #{} не прошла", event.getOrderId());
            return;
        }

        try {
            // Обновляем статус заказа
            orderService.markOrderAsPaid(event.getOrderId());

            // Получаем email
            UserResponse user = userClient.getUserById(event.getUserId());

            // Отправляем новое событие
            OrderPaidEvent paidEvent = OrderPaidEvent.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .email(user.getEmail())
                    .amount(event.getAmount())
                    .build();

            orderPaidEventPublisher.publish(paidEvent);
            log.info("📨 Событие оплаты заказа отправлено в Kafka: {}", paidEvent);

        } catch (Exception e) {
            log.error("❌ Ошибка при обработке события оплаты: {}", e.getMessage(), e);
        }
    }
}