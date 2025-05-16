package org.yrti.order.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.client.UserClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.events.OrderEventType;
import org.yrti.order.events.PaymentEvent;
import org.yrti.order.events.SellerEvent;
import org.yrti.order.kafka.OrderEventPublisher;
import org.yrti.order.kafka.SellerEventPublisher;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;
import org.yrti.order.model.OrderStatus;

/**
 * Сервис обработки платежей.
 * Обрабатывает события об оплате заказов:
 * - Успешные платежи
 * - Неудачные платежи
 * - Уведомления продавцов
 */
@Slf4j
@Service
public class PaymentProcessingService extends BaseOrderService {

  private final InventoryClient inventoryClient;
  private final SellerEventPublisher sellerEventPublisher;

  @Autowired
  public PaymentProcessingService(
      InventoryClient inventoryClient,
      SellerEventPublisher sellerEventPublisher,
      OrderRepository orderRepository,
      UserClient userClient,
      OrderEventPublisher orderEventPublisher) {
    super(orderRepository, userClient, orderEventPublisher);
    this.inventoryClient = inventoryClient;
    this.sellerEventPublisher = sellerEventPublisher;
  }

  @Transactional
  public void processPaymentEvent(PaymentEvent event) {
    if (!event.success()) {
      handleFailedPayment(event);
      return;
    }

    try {
      Order order = processSuccessfulPayment(event);
      notifySellers(order);
      publishEvent(order, OrderEventType.PAID);
    } catch (Exception e) {
      log.error("Ошибка при обработке события оплаты: {}", e.getMessage(), e);
    }
  }

  private void handleFailedPayment(PaymentEvent event) {
    log.warn("Оплата заказа #{} не прошла", event.orderId());
  }

  private Order processSuccessfulPayment(PaymentEvent event) {
    Order order = findOrderOrThrow(event.orderId());
    markOrderAsState(order, OrderStatus.PAID);
    order.setStatus(OrderStatus.PAID);
    return order;
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
}
