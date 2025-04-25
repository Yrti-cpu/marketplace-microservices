package org.yrti.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

import static org.assertj.core.api.Assertions.*;

class OrderRepositoryIntegrationTest extends IntegrationTestBase {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Сохраняем заказ и читаем его обратно")
    void saveAndFind() {
        Order order = Order.builder()
                .userId(123L)
                .status(OrderStatus.NEW)
                .build();

        Order saved = orderRepository.save(order);
        Order found = orderRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getUserId()).isEqualTo(123L);
        assertThat(found.getStatus()).isEqualTo(OrderStatus.NEW);
    }
}