package org.yrti.order.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OrderPaymentServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);

    private final OrderPaymentService orderPaymentService = new OrderPaymentService(orderRepository);


    @Test
    @DisplayName("Отметить заказ как оплаченный - успешно")
    void testMarkOrderAsPaid_success() {
        //Подготовка
        Order order = Order.builder().id(1L).status(OrderStatus.NEW).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        //Выполнение
        orderPaymentService.markOrderAsPaid(1L);
        //Проверка
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Ошибка, если заказ не найден")
    void testMarkOrderAsPaid_notFound() {
        //Подготовка
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        //Проверка
        assertThatThrownBy(() -> orderPaymentService.markOrderAsPaid(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("не найден");
    }
}