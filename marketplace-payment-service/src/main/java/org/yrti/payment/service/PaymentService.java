package org.yrti.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yrti.payment.dto.PaymentEvent;
import org.yrti.payment.dto.PaymentRequest;
import org.yrti.payment.kafka.PaymentEventPublisher;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentEventPublisher eventPublisher;

    public boolean processPayment(PaymentRequest request) {
        boolean isSuccess = Math.random() > 0.05;

        PaymentEvent event = PaymentEvent.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .success(isSuccess)
                .amount(request.getAmount())
                .message(isSuccess ? "Оплата прошла успешно" : "Оплата не прошла")
                .build();

        eventPublisher.publish(event);
        return isSuccess;
    }
}