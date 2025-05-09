package org.yrti.order.service;

import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dto.UserResponse;
import org.yrti.order.events.OrderPaidEvent;
import org.yrti.order.events.PaymentEvent;
import org.yrti.order.events.SellerEvent;
import org.yrti.order.kafka.OrderPaidEventPublisher;
import org.yrti.order.kafka.SellerEventPublisher;
import org.yrti.order.model.Order;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

  private final OrderService orderService;
  private final UserClient userClient;
  private final OrderPaidEventPublisher orderPaidEventPublisher;
  private final InventoryClient inventoryClient;
  private final SellerEventPublisher sellerEventPublisher;

  @Transactional
  public void processPaymentEvent(PaymentEvent event) {
    if (!event.success()) {
      log.warn("Оплата заказа #{} не прошла", event.orderId());
      return;
    }

    try {
      Order order = orderService.markOrderAsPaid(event.orderId());
      Set<Long> sellerIds = order.getItems().stream()
          .map(item -> inventoryClient.getSellerId(item.getProductId()))
          .collect(Collectors.toSet());
      sellerIds.forEach(sellerId -> {
        UserResponse userResponse = userClient.getUserById(sellerId);

        SellerEvent sellerEvent = SellerEvent.builder()
            .email(userResponse.getEmail())
            .build();
        sellerEventPublisher.publish(sellerEvent);
      });
      UserResponse user = userClient.getUserById(event.userId());

      OrderPaidEvent paidEvent = OrderPaidEvent.builder()
          .orderId(event.orderId())
          .userId(event.userId())
          .email(user.getEmail())
          .amount(event.amount())
          .build();

      orderPaidEventPublisher.publish(paidEvent);
      log.info("Событие оплаты заказа отправлено в Kafka: {}", paidEvent);

    } catch (Exception e) {
      log.error("Ошибка при обработке события оплаты: {}", e.getMessage(), e);
    }
  }
}
