package org.yrti.order.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.events.OrderEventType;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

/**
 * Сервис для обработки доставки заказов.
 * Отвечает за отметку заказов как доставленных.
 */
@Service
@Slf4j
public class OrderDeliveryService extends BaseOrderService {

  @Autowired
  public OrderDeliveryService(
      OrderRepository orderRepository,
      UserClient userClient,
      OrderEventPublisher orderEventPublisher) {
    super(orderRepository, userClient, orderEventPublisher);
  }

  @Transactional
  public void markOrderAsDelivered(Long orderId) {
    Order order = findOrderOrThrow(orderId);

    validateStatus(order, OrderStatus.DISPATCHED);

    markOrderAsState(order, OrderStatus.DELIVERED);

    publishEvent(order, OrderEventType.DELIVERED);
  }
}
