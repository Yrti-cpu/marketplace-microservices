package org.yrti.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.exception.OrderNotFoundException;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDispatchService {

  private final OrderRepository orderRepository;
  private final InventoryClient inventoryClient;

  @Transactional
  public String dispatchOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));

    if (order.getStatus() != OrderStatus.PAID) {
      throw new IllegalStateException("Заказ не может быть отгружен. Он не оплачен.");
    }

    order.getItems().forEach(item ->
        inventoryClient.releaseProduct(
            new ProductReserveRequest(item.getProductId(), item.getQuantity()))
    );

    order.setStatus(OrderStatus.DISPATCHED);
    orderRepository.save(order);
    log.info("Заказ #{} отправлен клиенту", orderId);
    return order.getAddress();
  }
}
