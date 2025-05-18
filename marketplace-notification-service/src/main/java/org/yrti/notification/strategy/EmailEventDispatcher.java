package org.yrti.notification.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yrti.notification.events.OrderEvent;
import org.yrti.notification.events.SellerEvent;

import java.util.List;
import org.yrti.notification.strategy.impl.SellerEmailStrategy;

/**
 * Диспетчер для обработки и маршрутизации email-уведомлений.
 * Выбирает подходящую стратегию для обработки каждого типа события.
 */
@Component
@RequiredArgsConstructor
public class EmailEventDispatcher {

  private final List<EmailStrategy> strategies;
  private final SellerEmailStrategy sellerEmailStrategy;

  public void dispatchEmail(OrderEvent event) {
    strategies.stream()
        .filter(s -> s.supports(event.eventType()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип"))
        .sendEmail(event);
  }

  public void dispatchSellerEmail(SellerEvent event) {
    sellerEmailStrategy.sendSellerEmail(event);
  }
}