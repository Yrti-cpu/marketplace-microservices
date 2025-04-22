package org.yrti.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.events.event.OrderCancelledEvent;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.exception.OrderNotFoundException;
import org.yrti.order.kafka.OrderCancelledEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;
import org.yrti.order.model.OrderStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCancellationService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final UserClient userClient;
    private final OrderCancelledEventPublisher orderCancelledEventPublisher;

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalStateException("Заказ можно отменить только до оплаты");
        }

        for (OrderItem item : order.getItems()) {
            inventoryClient.decreaseProduct(new ProductReserveRequest(item.getProductId(), item.getQuantity()));
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Заказ #{} отменён", order.getId());

        UserResponse user = userClient.getUserById(order.getUserId());

        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .email(user.getEmail())
                .message("Ваш заказ #" + order.getId() + " был отменён.")
                .build();

        orderCancelledEventPublisher.publish(event);
        log.info("Отправлено Kafka-событие об отмене заказа: {}", event);
    }
}
