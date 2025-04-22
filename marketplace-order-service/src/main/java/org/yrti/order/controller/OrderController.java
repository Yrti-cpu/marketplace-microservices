package org.yrti.order.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.model.Order;
import org.yrti.order.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }
    @PostMapping("/{id}/dispatch")
    public ResponseEntity<Void> dispatchOrder(@PathVariable Long id) {
        orderService.dispatchOrder(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<Void> deliverOrder(@PathVariable Long id) {
        orderService.markOrderAsDelivered(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

}
