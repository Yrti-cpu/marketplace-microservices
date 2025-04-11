package org.yrti.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yrti.order.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
