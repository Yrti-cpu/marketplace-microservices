package org.yrti.payment.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yrti.payment.exception.PaymentException;

@RestControllerAdvice
public class PaymentExceptionHandler {

  @ExceptionHandler(PaymentException.class)
  public ResponseEntity<?> handlePaymentError(PaymentException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

}
