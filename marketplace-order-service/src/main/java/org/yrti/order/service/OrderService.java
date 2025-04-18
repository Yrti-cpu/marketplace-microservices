package org.yrti.order.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.events.event.OrderCreatedEvent;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.PricingClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.PricingResponse;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.exception.InventoryServiceException;
import org.yrti.order.exception.OrderCreationException;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.model.OrderStatus;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final UserClient userClient;
    private final PricingClient pricingClient;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = initializeOrder(request);

        List<OrderItem> items = createAndReserveOrderItems(request, order);
        order.setItems(items);

        double totalAmount = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
        order.setTotalAmount(totalAmount);

        try {
            Order savedOrder = orderRepository.save(order);
            UserResponse user = userClient.getUserById(order.getUserId());

            publishOrderEvent(savedOrder.getId(), order.getUserId(), user.getEmail());
            return savedOrder;
        } catch (Exception e) {
            throw new OrderCreationException("Failed to create order: " + e.getMessage());
        }
    }

    private Order initializeOrder(CreateOrderRequest request) {
        return Order.builder()
                .userId(request.getUserId())
                .build();
    }

    private List<OrderItem> createAndReserveOrderItems(CreateOrderRequest request, Order order) {
        return request.getItems().stream().map(i -> {
            try {
                reserveProduct(i.getProductId(), i.getQuantity());

                PricingResponse priceInfo = pricingClient.getProductPrice(i.getProductId());

                double originalPrice = priceInfo.getOriginalPrice();
                double discountedPrice = priceInfo.getDiscountedPrice();
                double discount = originalPrice > 0
                        ? ((originalPrice - discountedPrice) / originalPrice) * 100
                        : 0.0;
                double totalPrice = discountedPrice * i.getQuantity();

                return OrderItem.builder()
                        .productId(i.getProductId())
                        .quantity(i.getQuantity())
                        .originalPrice(originalPrice)
                        .price(discountedPrice)
                        .totalPrice(totalPrice)
                        .discountPercentage(discount)
                        .order(order)
                        .build();

            } catch (Exception e) {
                throw new InventoryServiceException("Failed to reserve product: " + i.getProductId());
            }
        }).toList();
    }

    private void reserveProduct(Long productId, int quantity) {
        ProductReserveRequest reserveRequest = new ProductReserveRequest();
        reserveRequest.setProductId(productId);
        reserveRequest.setQuantity(quantity);
        inventoryClient.reserveProduct(reserveRequest);
    }

    private Double fetchDiscountedPrice(Long productId) {
        PricingResponse response = pricingClient.getProductPrice(productId);
        return response.getDiscountedPrice();
    }

    private void publishOrderEvent(Long orderId, Long userId, String email) {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .email(email)
                .message("Заказ #" + orderId + " успешно оформлен")
                .build();

        orderEventPublisher.publish(event);
    }

    public void markOrderAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderCreationException("Order not found"));

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        log.info("✅ Заказ #{} отмечен как оплаченный", orderId);
    }
}
