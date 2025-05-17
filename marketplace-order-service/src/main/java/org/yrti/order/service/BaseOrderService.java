package org.yrti.order.service;

import lombok.extern.slf4j.Slf4j;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.events.OrderEvent;
import org.yrti.order.events.OrderEventType;
import org.yrti.order.exception.OrderNotFoundException;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

/**
 * Абстрактный базовый сервис для работы с заказами.
 * Содержит общую логику для всех сервисов работы с заказами:
 * - Поиск заказа
 * - Валидация статуса
 * - Публикация событий
 * - Изменение статуса заказа
 */
@Slf4j
public abstract class BaseOrderService {

  protected final OrderRepository orderRepository;
  protected final UserClient userClient;
  protected final OrderEventPublisher eventPublisher;

  protected BaseOrderService(OrderRepository orderRepository,
      UserClient userClient,
      OrderEventPublisher eventPublisher) {
    this.orderRepository = orderRepository;
    this.userClient = userClient;
    this.eventPublisher = eventPublisher;
  }

  protected Order findOrderOrThrow(Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
  }

  protected void validateStatus(Order order, OrderStatus expected) {
    if (order.getStatus() != expected) {
      throw new IllegalStateException(
          String.format("Заказ должен быть %s", expected));
    }
  }

  protected void publishEvent(Order order, OrderEventType eventType) {
    UserResponse user = userClient.getUserById(order.getUserId());
    OrderEvent event = OrderEvent.builder()
        .orderId(order.getId())
        .email(user.getEmail())
        .eventType(eventType)
        .amount(order.getTotalAmount())
        .build();
    eventPublisher.publish(event);
    log.debug("Событие order-{} для заказа #{} отправлено в Kafka", eventType, order.getId());
  }

  protected void markOrderAsState(Order order, OrderStatus orderStatus) {
    order.setStatus(orderStatus);
    orderRepository.save(order);
    log.debug("Заказ #{} статус:{}", order.getId(), orderStatus);
  }

}
