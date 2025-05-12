package org.yrti.order.service;

import jakarta.transaction.Transactional;
import java.util.List;
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
import org.yrti.order.model.OrderItem;

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

      Set<Long> sellerIds = inventoryClient.getSellersId(order.getItems().stream()
          .map(OrderItem::getProductId).collect(Collectors.toList()));

      List<String> sellersEmails = userClient.getUsersBatch(sellerIds.stream().toList());

      if (sellersEmails.size() != sellerIds.size()) {
        log.warn("Не все пользователи найдены! Запрошено: {}, найдено: {}",
            sellerIds.size(), sellersEmails.size());
      }
      sellersEmails.forEach(email -> {
        sellerEventPublisher.publish(
            SellerEvent.builder()
                .email(email)
                .build()
        );
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
