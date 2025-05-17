package org.yrti.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

class OrderDeliveryServiceTest {

  private final OrderRepository orderRepository = mock(OrderRepository.class);
  private final UserClient userClient = mock(UserClient.class);
  private final OrderEventPublisher publisher = mock(OrderEventPublisher.class);

  private final OrderDeliveryService service = new OrderDeliveryService(orderRepository, userClient,
      publisher);

  @Test
  @DisplayName("Успешная доставка заказа")
  void testMarkDelivered_success() {
    Order order = Order.builder()
        .id(1L)
        .userId(1L)
        .status(OrderStatus.DISPATCHED)
        .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(userClient.getUserById(1L)).thenReturn(new UserResponse(1L, "email@mail.com", "User"));

    service.markOrderAsDelivered(1L);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    verify(orderRepository).save(order);
    verify(publisher).publish(any());
  }

  @Test
  @DisplayName("Ошибка доставки, если заказ не отправлен")
  void testMarkDelivered_invalidStatus() {
    Order order = Order.builder()
        .id(1L)
        .userId(1L)
        .status(OrderStatus.NEW)
        .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    assertThatThrownBy(() -> service.markOrderAsDelivered(1L))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(String.format("Заказ должен быть %s", OrderStatus.DISPATCHED));
  }
}