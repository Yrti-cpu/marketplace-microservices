package org.yrti.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

@Transactional
class OrderRepositoryIntegrationTest extends IntegrationTestBase {

  @Autowired
  private OrderRepository orderRepository;

  @Test
  void saveAndFind() {
    Order order = Order.builder()
        .userId(123L)
        .status(OrderStatus.NEW)
        .build();

    Order saved = orderRepository.save(order);
    Order found = orderRepository.findById(saved.getId()).orElseThrow();

    assertThat(found.getId()).isNotNull();
    assertThat(found.getUserId()).isEqualTo(123L);
    assertThat(found.getStatus()).isEqualTo(OrderStatus.NEW);
  }
}