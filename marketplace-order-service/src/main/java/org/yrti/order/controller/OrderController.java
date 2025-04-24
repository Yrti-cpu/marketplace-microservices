package org.yrti.order.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.model.Order;
import org.yrti.order.service.OrderService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
        orderService.dispatchOrder(id);
        return ResponseEntity.ok("Дата выгрузки со склада: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<?> deliverOrder(@PathVariable Long id) {
        orderService.markOrderAsDelivered(id);
        return ResponseEntity.ok("Дата доставки: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Дата отмены заказа: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

}
