package org.yrti.order.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.event.OrderCreatedEvent;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.request.CreateOrderRequest;
import org.yrti.order.exception.InventoryServiceException;
import org.yrti.order.exception.OrderCreationException;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;
import org.yrti.order.request.ProductReserveRequest;
import org.yrti.order.request.UserResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderEventPublisher orderEventPublisher;
    private final UserClient userClient;


    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = Order.builder()
                .userId(request.getUserId())
                .build();

        List<OrderItem> items = request.getItems().stream().map(i -> {
            try {
                ProductReserveRequest reserveRequest = new ProductReserveRequest();
                reserveRequest.setProductId(i.getProductId());
                reserveRequest.setQuantity(i.getQuantity());
                inventoryClient.reserveProduct(reserveRequest);
            } catch (Exception e) {
                throw new InventoryServiceException("Failed to reserve product: " + i.getProductId());
            }

            return OrderItem.builder()
                    .productId(i.getProductId())
                    .quantity(i.getQuantity())
                    .order(order)
                    .build();
        }).toList();

        order.setItems(items);

        try {
            UserResponse user = userClient.getUserById(request.getUserId());
            Order saved = orderRepository.save(order);

            publishOrderEvent(saved.getId(), saved.getUserId(), user.getEmail());
            return saved;
        } catch (Exception e) {
            throw new OrderCreationException("Failed to create order: " + e.getMessage());
        }
    }
    public void publishOrderEvent(Long orderId, Long userId, String gmail) {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .email(gmail)
                .message("Заказ #" + orderId + " успешно оформлен")
                .build();

        orderEventPublisher.publish(event);
    }
}
