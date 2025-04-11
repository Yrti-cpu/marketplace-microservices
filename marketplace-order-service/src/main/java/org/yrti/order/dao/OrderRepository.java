package org.yrti.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yrti.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
