package org.yrti.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.exception.OrderCreationException;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentService {

  private final OrderRepository orderRepository;

  @Transactional
  public void markOrderAsPaid(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderCreationException("Заказ не найден"));

    order.setStatus(OrderStatus.PAID);
    orderRepository.save(order);
    log.debug("Заказ #{} отмечен как оплаченный", orderId);
  }
}
