package org.yrti.order.handler;

import feign.FeignException;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yrti.order.exception.ClientRequestException;
import org.yrti.order.exception.InventoryServiceException;
import org.yrti.order.exception.OrderCreationException;
import org.yrti.order.exception.OrderNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<?> handleFeignException(FeignException ex) {
    HttpStatus status = switch (ex.status()) {
      case 400 -> HttpStatus.BAD_REQUEST;
      case 404 -> HttpStatus.NOT_FOUND;
      case 409 -> HttpStatus.CONFLICT;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };

    return ResponseEntity.status(status)
        .body(error(
            "Ошибка при взаимодействии с внешним сервисом: " + ex.getMessage(),
            status
        ));
  }

  @ExceptionHandler(InventoryServiceException.class)
  public ResponseEntity<?> handleInventoryError(InventoryServiceException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(error(ex.getMessage(), HttpStatus.CONFLICT));
  }

  @ExceptionHandler(OrderCreationException.class)
  public ResponseEntity<?> handleOrderError(OrderCreationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(error(ex.getMessage(), HttpStatus.BAD_REQUEST));
  }

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<?> handleOrderError(OrderNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(error(ex.getMessage(), HttpStatus.BAD_REQUEST));
  }

  @ExceptionHandler(ClientRequestException.class)
  public ResponseEntity<?> handleClientRequest(ClientRequestException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(error(ex.getMessage(), HttpStatus.BAD_REQUEST));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGeneric() {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(error("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR));
  }

  private Map<String, Object> error(String message, HttpStatus status) {
    return Map.of(
        "timestamp", LocalDateTime.now(),
        "status", status.value(),
        "error", status.getReasonPhrase(),
        "message", message
    );
  }
}
