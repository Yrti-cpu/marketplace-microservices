package org.yrti.order.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;
import org.yrti.order.model.OrderStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderDispatchServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final InventoryClient inventoryClient = mock(InventoryClient.class);

    private final OrderDispatchService service = new OrderDispatchService(orderRepository, inventoryClient);

    @Test
    @DisplayName("Успешная отправка заказа")
    void testDispatch_success() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.PAID)
                .items(List.of(OrderItem.builder().productId(1L).quantity(2).build()))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        service.dispatchOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DISPATCHED);
        verify(inventoryClient).releaseProduct(any(ProductReserveRequest.class));
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
                .hasMessageContaining("не может быть отгружен");
    }
}
