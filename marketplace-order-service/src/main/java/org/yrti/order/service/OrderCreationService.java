package org.yrti.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.PricingClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.dto.CreateOrderRequest.OrderItemRequest;
import org.yrti.order.dto.OrderResponse;
import org.yrti.order.dto.PricingResponse;
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.events.OrderCreatedEvent;
import org.yrti.order.exception.ClientRequestException;
import org.yrti.order.exception.OrderNotFoundException;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreationService {

  private final OrderRepository orderRepository;
  private final UserClient userClient;
  private final PricingClient pricingClient;
  private final InventoryClient inventoryClient;
  private final OrderEventPublisher orderEventPublisher;

  @Transactional
  public Order createOrder(CreateOrderRequest request) {
    Order order = Order.builder().userId(request.getUserId()).address(request.getAddress()).build();

    List<ProductReserveRequest> reserveRequests = new ArrayList<>();
    List<Long> productIds = new ArrayList<>();

    for (OrderItemRequest item : request.getItems()) {
      reserveRequests.add(new ProductReserveRequest(item.getProductId(), item.getQuantity()));
      productIds.add(item.getProductId());
    }

    ResponseEntity<String> inventoryResponse = inventoryClient.reserveProductsForOrder(reserveRequests);
    log.debug("Получили ответ со статусом: " + inventoryResponse.getStatusCode());
    if (!inventoryResponse.getStatusCode().is2xxSuccessful()) {
      assert inventoryResponse.getBody() != null;
      throw new ClientRequestException("Inventory", inventoryResponse.getBody());
    }

    ResponseEntity<List<PricingResponse>> pricingResponse = pricingClient.getProductPriceBatch(
        productIds);
    log.debug("Получили ответ со статусом: " + pricingResponse.getStatusCode());
    assert pricingResponse.getBody() != null;
    if (!pricingResponse.getStatusCode().is2xxSuccessful()) {
      throw new ClientRequestException("Pricing", pricingResponse.getBody().toString());
    }

    List<PricingResponse> prices = pricingResponse.getBody();

    List<OrderItem> items = new ArrayList<>();
    for (int i = 0; i < prices.size(); i++) {
      BigDecimal originalPrice = prices.get(i).getOriginalPrice();
      BigDecimal discountedPrice = prices.get(i).getDiscountedPrice();
      BigDecimal discount = prices.get(i).getDiscount();
      BigDecimal totalPrice = discountedPrice.multiply(
          BigDecimal.valueOf(request.getItems().get(i).getQuantity()));

      items.add(OrderItem.builder()
          .productId(productIds.get(i))
          .quantity(request.getItems().get(i).getQuantity())
          .originalPrice(originalPrice)
          .price(discountedPrice)
          .totalPrice(totalPrice)
          .discount(discount)
          .order(order)
          .build());

    }

    order.setItems(items);
    order.setTotalAmount(
        items.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
    Order savedOrder = orderRepository.save(order);

    UserResponse user = userClient.getUserById(order.getUserId());

    OrderCreatedEvent event = OrderCreatedEvent.builder()
        .orderId(order.getId())
        .userId(order.getUserId())
        .email(user.getEmail())
        .message("Заказ #" + order.getId() + " успешно оформлен")
        .build();

    orderEventPublisher.publish(event);
    log.debug("Заказ #{} создан", order.getId());

    return savedOrder;
  }

  public OrderResponse getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
    return new OrderResponse(order.getId(), order.getUserId(), order.getStatus().toString(),
        order.getTotalAmount());
  }
}
