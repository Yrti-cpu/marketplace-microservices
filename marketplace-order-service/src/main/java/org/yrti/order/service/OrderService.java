package org.yrti.order.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.dto.OrderResponse;
import org.yrti.order.model.Order;

/**
 * Фасадный сервис для работы с заказами.
 * Объединяет все операции с заказами в единый API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderCreationService orderCreationService;
  private final OrderDispatchService orderDispatchService;
  private final OrderDeliveryService orderDeliveryService;
  private final OrderCancellationService orderCancellationService;

  public Order createOrder(CreateOrderRequest request) {
    return orderCreationService.createOrder(request);
  }

  public OrderResponse getOrder(Long orderId) {
    return orderCreationService.getOrderById(orderId);
  }

  public String dispatchOrder(Long orderId) {
    return orderDispatchService.dispatchOrder(orderId);
  }

  public void markOrderAsDelivered(Long orderId) {
    orderDeliveryService.markOrderAsDelivered(orderId);
  }

  public void cancelOrder(Long orderId) {
    orderCancellationService.cancelOrder(orderId);
  }

}
