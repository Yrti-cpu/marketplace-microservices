package org.yrti.order.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.event.OrderCreatedEvent;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.request.CreateOrderRequest;
import org.yrti.order.exception.InventoryServiceException;
import org.yrti.order.exception.OrderCreationException;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;
import org.yrti.order.request.ProductReserveRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderEventPublisher orderEventPublisher;


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
            Order saved = orderRepository.save(order);
            OrderCreatedEvent event = new OrderCreatedEvent();
            event.setOrderId(saved.getId());
            event.setUserId(saved.getUserId());
            event.setMessage("Заказ #" + saved.getId() + " успешно оформлен");

            orderEventPublisher.publish(event);

            return saved;
        } catch (Exception e) {
            throw new OrderCreationException("Failed to create order: " + e.getMessage());
        }
    }
}
