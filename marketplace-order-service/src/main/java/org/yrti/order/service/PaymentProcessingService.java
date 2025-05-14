package org.yrti.order.service;

import jakarta.transaction.Transactional;
import java.util.List;
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
      handleFailedPayment(event);
      return;
    }

    try {
      Order order = processSuccessfulPayment(event);
      notifySellers(order);
      publishOrderPaidEvent(event);
    } catch (Exception e) {
      log.error("Ошибка при обработке события оплаты: {}", e.getMessage(), e);
    }
  }

  private void handleFailedPayment(PaymentEvent event) {
    log.warn("Оплата заказа #{} не прошла", event.orderId());
  }

  private Order processSuccessfulPayment(PaymentEvent event) {
    return orderService.markOrderAsPaid(event.orderId());
  }

  private void notifySellers(Order order) {
    List<Long> sellerIds = getSellerIds(order);
    List<String> sellerEmails = getSellerEmails(sellerIds);

    validateSellerEmails(sellerIds, sellerEmails);
    publishSellerNotifications(sellerEmails);
  }

  private List<Long> getSellerIds(Order order) {
    return inventoryClient.getSellersId(
        order.getItems().stream()
            .map(OrderItem::getProductId)
            .toList()
    );
  }

  private List<String> getSellerEmails(List<Long> sellerIds) {
    return userClient.getUsersBatch(sellerIds);
  }

  private void validateSellerEmails(List<Long> sellerIds, List<String> sellerEmails) {
    if (sellerEmails.size() != sellerIds.size()) {
      log.warn("Не все пользователи найдены! Запрошено: {}, найдено: {}",
          sellerIds.size(), sellerEmails.size());
    }
  }

  private void publishSellerNotifications(List<String> sellerEmails) {
    sellerEmails.forEach(email ->
        sellerEventPublisher.publish(
            SellerEvent.builder()
                .email(email)
                .build()
        )
    );
  }

  private void publishOrderPaidEvent(PaymentEvent event) {
    UserResponse user = getUserInfo(event.userId());

    OrderPaidEvent paidEvent = buildOrderPaidEvent(event, user);
    orderPaidEventPublisher.publish(paidEvent);

    log.info("Событие оплаты заказа отправлено в Kafka: {}", paidEvent);
  }

  private UserResponse getUserInfo(Long userId) {
    return userClient.getUserById(userId);
  }

  private OrderPaidEvent buildOrderPaidEvent(PaymentEvent event, UserResponse user) {
    return OrderPaidEvent.builder()
        .orderId(event.orderId())
        .userId(event.userId())
        .email(user.getEmail())
        .amount(event.amount())
        .build();
  }
}
