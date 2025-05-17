package org.yrti.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.yrti.order.events.OrderEventType;
import org.yrti.order.exception.OrderNotFoundException;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;

/**
 * Сервис для создания новых заказов.
 * Обрабатывает полный цикл создания заказа:
 * - Создание заказа из запроса
 * - Резервирование товаров
 * - Получение цен
 * - Расчет итоговой суммы
 * - Публикация события
 */
@Service
@Slf4j
public class OrderCreationService extends BaseOrderService {

  private final PricingClient pricingClient;
  private final InventoryClient inventoryClient;

  @Autowired
  public OrderCreationService(
      OrderRepository orderRepository,
      UserClient userClient,
      OrderEventPublisher eventPublisher,
      InventoryClient inventoryClient,
      PricingClient pricingClient) {
    super(orderRepository, userClient, eventPublisher);
    this.inventoryClient = inventoryClient;
    this.pricingClient = pricingClient;
  }

  @Transactional
  public Order createOrder(CreateOrderRequest request) {
    Order order = createOrderFromRequest(request);

    reserveProductsInInventory(request);
    List<PricingResponse> prices = getProductPrices(request);

    List<OrderItem> items = createOrderItems(request, prices, order);
    order.setItems(items);
    order.setTotalAmount(calculateTotalAmount(items));

    Order savedOrder = orderRepository.save(order);

    publishEvent(savedOrder, OrderEventType.CREATED);

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

    inventoryClient.reserveProductsForOrder(reserveRequests);
  }

  private List<PricingResponse> getProductPrices(CreateOrderRequest request) {
    List<Long> productIds = request.getItems().stream()
        .map(OrderItemRequest::getProductId)
        .toList();

    return pricingClient.getProductPriceBatch(productIds);
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

  public OrderResponse getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
    return new OrderResponse(order.getId(), order.getUserId(), order.getStatus().toString(),
        order.getTotalAmount());
  }
}
