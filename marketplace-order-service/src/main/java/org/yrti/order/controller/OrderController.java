package org.yrti.order.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.dto.OrderResponse;
import org.yrti.order.model.Order;
import org.yrti.order.service.OrderService;

@Tag(name = "Заказы", description = "Управляет заказами пользователей")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @Operation(
      summary = "Создание заказа",
      description = "Позволяет создать заказ"
  )
  @PostMapping
  public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    return ResponseEntity.ok(orderService.createOrder(request));
  }

  @Operation(
      summary = "Отправка заказа",
      description = "Позволяет отправить заказ со склада"
  )
  @PutMapping("/{id}/dispatch")
  public ResponseEntity<String> dispatchOrder(@PathVariable Long id) {
    return ResponseEntity.ok("Адрес доставки: " + orderService.dispatchOrder(id));
  }

  @Operation(
      summary = "Доставить заказ",
      description = "Позволяет доставить заказ до пользователя"
  )
  @PutMapping("/{id}/deliver")
  public ResponseEntity<String> deliverOrder(@PathVariable Long id) {
    orderService.markOrderAsDelivered(id);
    return ResponseEntity.ok(
        "Дата доставки: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @Operation(
      summary = "Отмена заказа",
      description = "Позволяет отменить заказ"
  )
  @DeleteMapping("/{id}/cancel")
  public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
    orderService.cancelOrder(id);
    return ResponseEntity.ok(
        "Дата отмены заказа: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }


  @Hidden
  @Operation(
      summary = "Получить заказ",
      description = "Позволяет получить информацию о заказе"
  )
  @GetMapping("/{id}")
  public OrderResponse getOrder(@PathVariable Long id) {
    return orderService.getOrder(id);
  }
}
