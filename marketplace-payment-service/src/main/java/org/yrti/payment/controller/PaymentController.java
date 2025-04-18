package org.yrti.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yrti.payment.dto.PaymentRequest;
import org.yrti.payment.dto.PaymentResponse;
import org.yrti.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> pay(@RequestBody PaymentRequest request) {
        boolean success = paymentService.processPayment(request);
        return ResponseEntity.ok(new PaymentResponse(success ? "SUCCESS" : "FAILED"));
    }
}