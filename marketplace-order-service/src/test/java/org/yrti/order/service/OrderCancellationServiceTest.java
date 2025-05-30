package org.yrti.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;
import org.yrti.order.model.OrderStatus;

class OrderCancellationServiceTest {

  private final OrderRepository orderRepository = mock(OrderRepository.class);
  private final InventoryClient inventoryClient = mock(InventoryClient.class);
  private final UserClient userClient = mock(UserClient.class);
  private final OrderEventPublisher eventPublisher = mock(OrderEventPublisher.class);

  private final OrderCancellationService service = new OrderCancellationService(
      orderRepository, userClient, eventPublisher, inventoryClient
  );

  @Test
  @DisplayName("Успешная отмена заказа")
  void testCancelOrder_success() {
    // Подготовка
    Order order = Order.builder()
        .id(1L)
        .userId(1L)
        .status(OrderStatus.NEW)
        .items(List.of(
            OrderItem.builder().productId(1L).quantity(2).build(),
            OrderItem.builder().productId(2L).quantity(1).build()
        ))
        .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(userClient.getUserById(1L)).thenReturn(new UserResponse(1L, "mail@mail.com", "Daniil"));
    when(inventoryClient.decreaseProductsForOrder(any()))
        .thenReturn("Дата отмены брони: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    // Выполнение
    service.cancelOrder(1L);

    // Проверка
    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    verify(inventoryClient, times(1)).decreaseProductsForOrder(any());
    verify(orderRepository).save(order);
    verify(eventPublisher).publish(any());
  }

  @Test
  @DisplayName("Ошибка при отмене оплаченного заказа")
  void testCancelOrder_paidOrder_throwsException() {
    //Подготовка
    Order order = Order.builder().id(1L).status(OrderStatus.PAID).build();
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    //Проверка
    assertThatThrownBy(() -> service.cancelOrder(1L))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(String.format("Заказ должен быть %s", OrderStatus.NEW));
  }
}