package org.yrti.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.events.OrderDeliveredEvent;
import org.yrti.order.exception.OrderNotFoundException;
import org.yrti.order.kafka.OrderDeliveredEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDeliveryService {

  private final OrderRepository orderRepository;
  private final UserClient userClient;
  private final OrderDeliveredEventPublisher orderDeliveredEventPublisher;

  @Transactional
  public void markOrderAsDelivered(Long orderId) {
    Order order = findOrderById(orderId);
    ensureOrderCanBeDelivered(order);
    updateOrderStatusToDelivered(order);
    UserResponse user = fetchUser(order);
    publishOrderDeliveredEvent(order, user);
    logDelivery(order);
  }

  private Order findOrderById(Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
  }

  private void ensureOrderCanBeDelivered(Order order) {
    if (order.getStatus() != OrderStatus.DISPATCHED) {
      throw new IllegalStateException("Невозможно доставить заказ, который ещё не отправлен.");
    }
  }

  private void updateOrderStatusToDelivered(Order order) {
    order.setStatus(OrderStatus.DELIVERED);
    orderRepository.save(order);
  }

  private UserResponse fetchUser(Order order) {
    return userClient.getUserById(order.getUserId());
  }

  private void publishOrderDeliveredEvent(Order order, UserResponse user) {
    OrderDeliveredEvent event = OrderDeliveredEvent.builder()
        .orderId(order.getId())
        .userId(order.getUserId())
        .email(user.getEmail())
        .build();

    orderDeliveredEventPublisher.publish(event);
  }

  private void logDelivery(Order order) {
    log.debug("Заказ #{} доставлен. Событие отправлено в Kafka", order.getId());
  }

}
