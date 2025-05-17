package org.yrti.notification.strategy;

import org.yrti.notification.events.OrderEvent;
import org.yrti.notification.events.OrderEventType;
import org.yrti.notification.events.SellerEvent;

/**
 * Интерфейс стратегии для отправки email-уведомлений.
 * Определяет контракт для различных типов email-уведомлений.
 */
public interface EmailStrategy{

  void sendEmail(OrderEvent event); // Для событий заказа
  void sendSellerEmail(SellerEvent event); // Для событий продавца
  boolean supports(OrderEventType eventType); // Поддержка типа события
}
