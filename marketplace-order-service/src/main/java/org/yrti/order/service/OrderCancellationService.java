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
import org.yrti.order.exception.ClientRequestException;
import org.yrti.order.exception.OrderNotFoundException;
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

  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));

    if (order.getStatus() != OrderStatus.NEW) {
      throw new IllegalStateException("Заказ можно отменить только до оплаты");
    }

    List<ProductReserveRequest> cancellRequests = order.getItems().stream()
        .map(item -> new ProductReserveRequest(item.getProductId(), item.getQuantity()))
        .toList();

    ResponseEntity<String> inventoryResponse = inventoryClient.decreaseProductsForOrder(cancellRequests);

    if (!inventoryResponse.getStatusCode().is2xxSuccessful()) {
      assert inventoryResponse.getBody() != null;
      throw new ClientRequestException("Inventory", inventoryResponse.getBody());
    }

    order.setStatus(OrderStatus.CANCELLED);
    orderRepository.save(order);
    log.debug("Заказ #{} отменён", order.getId());

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
