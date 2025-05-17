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
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

/**
 * Сервис для обработки отправки заказов. Отвечает за подготовку заказа к отправке: - Проверка
 * статуса - Освобождение товаров со склада - Изменение статуса
 */
@Service
@Slf4j
public class OrderDispatchService extends BaseOrderService {

  private final InventoryClient inventoryClient;

  @Autowired
  public OrderDispatchService(
      InventoryClient inventoryClient,
      OrderRepository orderRepository,
      UserClient userClient,
      OrderEventPublisher orderEventPublisher) {
    super(orderRepository, userClient, orderEventPublisher);
    this.inventoryClient = inventoryClient;
  }

  @Transactional
  public String dispatchOrder(Long orderId) {
    Order order = findOrderOrThrow(orderId);

    validateStatus(order, OrderStatus.PAID);

    releaseProductsInInventory(order);

    markOrderAsState(order, OrderStatus.DISPATCHED);

    return order.getAddress();
  }


  private void releaseProductsInInventory(Order order) {
    List<ProductReserveRequest> dispatchRequests = order.getItems().stream()
        .map(item -> new ProductReserveRequest(item.getProductId(), item.getQuantity()))
        .toList();

    inventoryClient.releaseProductsForOrder(dispatchRequests);
  }
}
