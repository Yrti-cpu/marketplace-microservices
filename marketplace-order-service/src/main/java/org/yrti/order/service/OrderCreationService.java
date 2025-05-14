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
import org.yrti.order.exception.OrderNotFoundException;
import org.yrti.order.handler.ClientResponseHandle;
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
  private final ClientResponseHandle clientResponseHandle;

  @Transactional
  public Order createOrder(CreateOrderRequest request) {
    Order order = createOrderFromRequest(request);

    reserveProductsInInventory(request);
    List<PricingResponse> prices = getProductPrices(request);

    List<OrderItem> items = createOrderItems(request, prices, order);
    order.setItems(items);
    order.setTotalAmount(calculateTotalAmount(items));

    Order savedOrder = orderRepository.save(order);
    publishOrderCreatedEvent(savedOrder);

    return savedOrder;
  }

  private Order createOrderFromRequest(CreateOrderRequest request) {
    return Order.builder()
        .userId(request.getUserId())
        .address(request.getAddress())
        .build();
  }

  private void reserveProductsInInventory(CreateOrderRequest request) {
    List<ProductReserveRequest> reserveRequests = request.getItems().stream()
        .map(item -> new ProductReserveRequest(item.getProductId(), item.getQuantity()))
        .toList();

    ResponseEntity<String> inventoryResponse = inventoryClient.reserveProductsForOrder(
        reserveRequests);
    clientResponseHandle.handleResponse(inventoryResponse, "Inventory");
  }

  private List<PricingResponse> getProductPrices(CreateOrderRequest request) {
    List<Long> productIds = request.getItems().stream()
        .map(OrderItemRequest::getProductId)
        .toList();

    ResponseEntity<List<PricingResponse>> pricingResponse = pricingClient.getProductPriceBatch(
        productIds);
    return clientResponseHandle.handleResponse(pricingResponse, "Pricing");
  }

  private List<OrderItem> createOrderItems(CreateOrderRequest request,
      List<PricingResponse> prices,
      Order order) {
    List<OrderItem> items = new ArrayList<>();

    for (int i = 0; i < prices.size(); i++) {
      PricingResponse price = prices.get(i);
      OrderItemRequest itemRequest = request.getItems().get(i);

      BigDecimal totalPrice = price.getDiscountedPrice()
          .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

      items.add(OrderItem.builder()
          .productId(itemRequest.getProductId())
          .quantity(itemRequest.getQuantity())
          .originalPrice(price.getOriginalPrice())
          .price(price.getDiscountedPrice())
          .totalPrice(totalPrice)
          .discount(price.getDiscount())
          .order(order)
          .build());
    }

    return items;
  }

  private BigDecimal calculateTotalAmount(List<OrderItem> items) {
    return items.stream()
        .map(OrderItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private void publishOrderCreatedEvent(Order order) {
    UserResponse user = userClient.getUserById(order.getUserId());

    OrderCreatedEvent event = OrderCreatedEvent.builder()
        .orderId(order.getId())
        .userId(order.getUserId())
        .email(user.getEmail())
        .message("Заказ #" + order.getId() + " успешно оформлен")
        .build();

    orderEventPublisher.publish(event);
    log.debug("Заказ #{} создан", order.getId());
  }

  public OrderResponse getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
    return new OrderResponse(order.getId(), order.getUserId(), order.getStatus().toString(),
        order.getTotalAmount());
  }
}
