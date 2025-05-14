package org.yrti.order.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.exception.OrderNotFoundException;
import org.yrti.order.handler.ClientResponseHandle;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDispatchService {

  private final OrderRepository orderRepository;
  private final InventoryClient inventoryClient;
  private final ClientResponseHandle clientResponseHandle;

  @Transactional
  public String dispatchOrder(Long orderId) {
    Order order = findOrderOrThrow(orderId);
    validateOrderCanBeDispatched(order);

    releaseProductsInInventory(order);
    updateOrderStatusToDispatched(order);

    log.debug("Заказ #{} отправлен клиенту", orderId);
    return order.getAddress();
  }

  private Order findOrderOrThrow(Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
  }

  private void validateOrderCanBeDispatched(Order order) {
    if (order.getStatus() != OrderStatus.PAID) {
      throw new IllegalStateException("Заказ не может быть отгружен. Он не оплачен.");
    }
  }

  private void releaseProductsInInventory(Order order) {
    List<ProductReserveRequest> dispatchRequests = order.getItems().stream()
        .map(item -> new ProductReserveRequest(item.getProductId(), item.getQuantity()))
        .toList();

    ResponseEntity<String> inventoryResponse = inventoryClient.releaseProductsForOrder(
        dispatchRequests);
    clientResponseHandle.handleResponse(inventoryResponse, "Inventory");
  }

  private void updateOrderStatusToDispatched(Order order) {
    order.setStatus(OrderStatus.DISPATCHED);
    orderRepository.save(order);
  }
}
