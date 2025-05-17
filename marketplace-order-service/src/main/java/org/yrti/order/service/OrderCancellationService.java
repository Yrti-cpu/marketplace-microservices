package org.yrti.order.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.events.OrderEventType;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

/**
 * Сервис для отмены заказов.
 * Обрабатывает логику отмены заказов:
 * - Проверяет возможность отмены
 * - Освобождает зарезервированные товары
 * - Изменяет статус заказа
 * - Публикует событие об отмене
 */
@Service
@Slf4j
public class OrderCancellationService extends BaseOrderService {

  private final InventoryClient inventoryClient;

  @Autowired
  public OrderCancellationService(OrderRepository orderRepository,
      UserClient userClient,
      OrderEventPublisher eventPublisher,
      InventoryClient inventoryClient) {
    super(orderRepository, userClient, eventPublisher);
    this.inventoryClient = inventoryClient;
  }

  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = findOrderOrThrow(orderId);

    validateStatus(order, OrderStatus.NEW);

    cancelProductReservations(order);

    markOrderAsState(order, OrderStatus.CANCELLED);

    publishEvent(order, OrderEventType.CANCELLED);
  }

  private void cancelProductReservations(Order order) {
    List<ProductReserveRequest> cancelRequests = order.getItems().stream()
        .map(item -> new ProductReserveRequest(item.getProductId(), item.getQuantity()))
        .toList();

    inventoryClient.decreaseProductsForOrder(cancelRequests);
  }
}
