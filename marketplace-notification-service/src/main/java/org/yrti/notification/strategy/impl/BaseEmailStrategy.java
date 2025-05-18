package org.yrti.notification.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.yrti.notification.events.OrderEventType;
import org.yrti.notification.events.SellerEvent;
import org.yrti.notification.service.EmailService;
import org.yrti.notification.strategy.EmailStrategy;

/**
 * Абстрактный базовый класс для стратегий email-уведомлений. Реализует общую логику для всех
 * стратегий: - Проверку поддержки типа события - Базовую реализацию уведомлений для продавцов
 */
@RequiredArgsConstructor
public abstract class BaseEmailStrategy implements EmailStrategy {

  protected final EmailService emailService;
  protected final OrderEventType supportedType;

  @Override
  public boolean supports(OrderEventType eventType) {
    return supportedType.equals(eventType);
  }

  @Override
  public void sendSellerEmail(SellerEvent event) {
    // Базовая реализация для продавцов
    emailService.send(event.email(), "Товар куплен", "Ваш товар приобрели!");
  }
}