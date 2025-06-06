package org.yrti.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
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
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;
import org.yrti.order.model.OrderStatus;

class OrderDispatchServiceTest {

  private final OrderRepository orderRepository = mock(OrderRepository.class);
  private final InventoryClient inventoryClient = mock(InventoryClient.class);
  private final UserClient userClient = mock(UserClient.class);
  private final OrderEventPublisher orderEventPublisher = mock(OrderEventPublisher.class);

  private final OrderDispatchService service = new OrderDispatchService(inventoryClient,
      orderRepository, userClient, orderEventPublisher);

  @Test
  @DisplayName("Успешная отправка заказа")
  void testDispatch_success() {
    Order order = Order.builder()
        .id(1L)
        .status(OrderStatus.PAID)
        .items(List.of(OrderItem.builder().productId(1L).quantity(2).build()))
        .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    when(inventoryClient.releaseProductsForOrder(
        List.of(new ProductReserveRequest(1L, 2)))).thenReturn(
        "Дата выгрузки со склада: " + LocalDateTime.now().truncatedTo(
            ChronoUnit.SECONDS)
    );

    service.dispatchOrder(1L);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.DISPATCHED);
    verify(inventoryClient).releaseProductsForOrder(any());
    verify(orderRepository).save(order);
  }

  @Test
  @DisplayName("Ошибка если заказ не оплачен")
  void testDispatch_invalidStatus() {
    Order order = Order.builder()
        .id(1L)
        .status(OrderStatus.NEW)
        .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    assertThatThrownBy(() -> service.dispatchOrder(1L))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining(String.format("Заказ должен быть %s", OrderStatus.PAID));
  }
}
