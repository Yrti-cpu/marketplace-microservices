package org.yrti.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.PricingClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.dto.PricingResponse;
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.model.Order;

class OrderCreationServiceTest {

  private final OrderRepository orderRepository = mock(OrderRepository.class);
  private final InventoryClient inventoryClient = mock(InventoryClient.class);
  private final PricingClient pricingClient = mock(PricingClient.class);
  private final UserClient userClient = mock(UserClient.class);
  private final OrderEventPublisher orderEventPublisher = mock(OrderEventPublisher.class);

  private final OrderCreationService orderCreateService = new OrderCreationService(
      orderRepository, userClient, pricingClient, inventoryClient, orderEventPublisher
  );

  @Test
  @DisplayName("Успешное создание заказа")
  void testCreateOrder_success() {
    // Подготовка
    CreateOrderRequest request = new CreateOrderRequest(1L,
        List.of(new CreateOrderRequest.OrderItemRequest(1L, 1),
            new CreateOrderRequest.OrderItemRequest(2L, 2)), "test address");

    when(pricingClient.getProductPriceBatch(List.of(1L, 2L))).thenReturn(
        new ResponseEntity<> (List.of( new PricingResponse(1L, new BigDecimal("100.00"), new BigDecimal("80.00"),
           new BigDecimal("0.2")),  new PricingResponse(2L, new BigDecimal("150.00"), new BigDecimal("145.00"),
           new BigDecimal("0.2"))), HttpStatus.OK
   ));

    when(inventoryClient.decreaseProductsForOrder(
        List.of(new ProductReserveRequest(1L, 1), new ProductReserveRequest(2L, 2)))).thenReturn(
        new ResponseEntity<>(HttpStatus.OK));

    when(orderRepository.save(any(Order.class))).thenAnswer(
        invocation -> invocation.getArgument(0));
    when(userClient.getUserById(1L)).thenReturn(new UserResponse(1L, "danila@mail.com", "Danila"));

    // Выполнение
    Order order = orderCreateService.createOrder(request);

    // Проверка
    assertThat(order).isNotNull();
    assertThat(order.getItems()).hasSize(2);
    assertThat(order.getTotalAmount()).isEqualByComparingTo("370.00");

    // Проверка вызовов
    verify(inventoryClient, times(1)).reserveProductsForOrder(any());
    verify(orderRepository).save(any());
    verify(orderEventPublisher).publish(any());
  }

}