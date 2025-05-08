package org.yrti.order.service;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.PricingClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.dto.PricingResponse;
import org.yrti.order.dto.ProductReserveRequest;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.events.OrderCreatedEvent;
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

    List<OrderItem> items = request.getItems().stream().map(i -> {
      inventoryClient.reserveProduct(new ProductReserveRequest(i.getProductId(), i.getQuantity()));
      PricingResponse priceInfo = pricingClient.getProductPrice(i.getProductId());

      BigDecimal originalPrice = priceInfo.getOriginalPrice();
      BigDecimal discountedPrice = priceInfo.getDiscountedPrice();
      BigDecimal discount = priceInfo.getDiscountPercent();

      BigDecimal totalPrice = discountedPrice.multiply(BigDecimal.valueOf(i.getQuantity()));

      return OrderItem.builder()
          .productId(i.getProductId())
          .quantity(i.getQuantity())
          .originalPrice(originalPrice)
          .price(discountedPrice)
          .totalPrice(totalPrice)
          .discountPercentage(discount)
          .order(order)
          .build();
    }).toList();

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
    log.info("Заказ #{} создан", order.getId());

    return savedOrder;
  }
}
