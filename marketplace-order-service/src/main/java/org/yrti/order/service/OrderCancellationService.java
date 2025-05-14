package org.yrti.order.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.events.OrderCancelledEvent;
import org.yrti.order.exception.OrderNotFoundException;
import org.yrti.order.handler.ClientResponseHandle;
import org.yrti.order.kafka.OrderCancelledEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCancellationService {

  private final OrderRepository orderRepository;
  private final InventoryClient inventoryClient;
  private final UserClient userClient;
  private final OrderCancelledEventPublisher orderCancelledEventPublisher;
  private final ClientResponseHandle clientResponseHandle;

  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = findOrderOrThrow(orderId);
    validateOrderCanBeCancelled(order);

    cancelProductReservations(order);
    updateOrderStatusToCancelled(order);
    publishCancellationEvent(order);
  }

  private Order findOrderOrThrow(Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
  }

  private void validateOrderCanBeCancelled(Order order) {
    if (order.getStatus() != OrderStatus.NEW) {
      throw new IllegalStateException("Заказ можно отменить только до оплаты");
    }
  }

  private void cancelProductReservations(Order order) {
    List<ProductReserveRequest> cancelRequests = order.getItems().stream()
        .map(item -> new ProductReserveRequest(item.getProductId(), item.getQuantity()))
        .toList();

    ResponseEntity<String> inventoryResponse = inventoryClient.decreaseProductsForOrder(
        cancelRequests);
    clientResponseHandle.handleResponse(inventoryResponse, "Inventory");
  }

  private void updateOrderStatusToCancelled(Order order) {
    order.setStatus(OrderStatus.CANCELLED);
    orderRepository.save(order);
    log.debug("Заказ #{} отменён", order.getId());
  }

  private void publishCancellationEvent(Order order) {
    UserResponse user = getUserInfo(order.getUserId());
    OrderCancelledEvent event = buildCancellationEvent(order, user);

    orderCancelledEventPublisher.publish(event);
    log.info("Отправлено Kafka-событие об отмене заказа: {}", event);
  }

  private UserResponse getUserInfo(Long userId) {
    return userClient.getUserById(userId);
  }

  private OrderCancelledEvent buildCancellationEvent(Order order, UserResponse user) {
    return OrderCancelledEvent.builder()
        .orderId(order.getId())
        .userId(order.getUserId())
        .email(user.getEmail())
        .message("Ваш заказ #" + order.getId() + " был отменён.")
        .build();
  }
}
