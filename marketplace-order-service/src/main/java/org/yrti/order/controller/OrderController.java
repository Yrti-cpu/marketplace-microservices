package org.yrti.order.controller;


import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.dto.OrderResponse;
import org.yrti.order.model.Order;
import org.yrti.order.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    return ResponseEntity.ok(orderService.createOrder(request));
  }

  @PostMapping("/{id}/dispatch")
  public ResponseEntity<?> dispatchOrder(@PathVariable Long id) {
    return ResponseEntity.ok("Адрес доставки: " + orderService.dispatchOrder(id));
  }

  @PostMapping("/{id}/deliver")
  public ResponseEntity<?> deliverOrder(@PathVariable Long id) {
    orderService.markOrderAsDelivered(id);
    return ResponseEntity.ok(
        "Дата доставки: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
    orderService.cancelOrder(id);
    return ResponseEntity.ok(
        "Дата отмены заказа: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @GetMapping("/{id}")
  public OrderResponse getOrder(@PathVariable Long id) {
    Order order = orderService.getOrder(id);
    return new OrderResponse(order.getId(), order.getUserId(), order.getStatus(),order.getTotalAmount());
  }
}
