package org.yrti.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.events.event.OrderPaidEvent;
import org.yrti.events.event.PaymentEvent;
import org.yrti.order.client.UserClient;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.kafka.OrderPaidEventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {
    private final OrderService orderService;
    private final UserClient userClient;
    private final OrderPaidEventPublisher orderPaidEventPublisher;

    public void processPaymentEvent(PaymentEvent event) {
        if (!event.success()) {
            log.warn("Оплата заказа #{} не прошла", event.orderId());
            return;
        }

        try {
            orderService.markOrderAsPaid(event.orderId());

            UserResponse user = userClient.getUserById(event.userId());

            OrderPaidEvent paidEvent = OrderPaidEvent.builder()
                    .orderId(event.orderId())
                    .userId(event.userId())
                    .email(user.getEmail())
                    .amount(event.amount())
                    .build();

            orderPaidEventPublisher.publish(paidEvent);
            log.info("Событие оплаты заказа отправлено в Kafka: {}", paidEvent);

        } catch (Exception e) {
            log.error("Ошибка при обработке события оплаты: {}", e.getMessage(), e);
        }
    }
}
