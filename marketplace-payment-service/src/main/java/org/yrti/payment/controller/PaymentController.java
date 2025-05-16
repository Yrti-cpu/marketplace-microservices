package org.yrti.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yrti.payment.dto.PaymentRequest;
import org.yrti.payment.dto.PaymentResponse;
import org.yrti.payment.service.PaymentService;

@Tag(name = "Оплата", description = "Управляет оплатой заказа")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @Operation(
      summary = "Оплата заказа",
      description = "Позволяет оплатить заказ"
  )
  @PostMapping("/{id}")
  public ResponseEntity<PaymentResponse> pay(@PathVariable Long id,
      @RequestBody PaymentRequest request) {
    return ResponseEntity.ok(paymentService.processPayment(request, id));
  }
}