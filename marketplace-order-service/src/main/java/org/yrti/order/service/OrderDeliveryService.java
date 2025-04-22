package org.yrti.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.events.event.OrderDeliveredEvent;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.exception.OrderNotFoundException;
import org.yrti.order.kafka.OrderDeliveredEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDeliveryService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final OrderDeliveredEventPublisher orderDeliveredEventPublisher;

    @Transactional
    public void markOrderAsDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.DISPATCHED) {
            throw new IllegalStateException("Невозможно доставить заказ, который ещё не отправлен.");
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        UserResponse user = userClient.getUserById(order.getUserId());

        OrderDeliveredEvent event = OrderDeliveredEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .email(user.getEmail())
                .build();

        orderDeliveredEventPublisher.publish(event);
        log.info("Заказ #{} доставлен. Событие отправлено в Kafka", order.getId());
    }
}
