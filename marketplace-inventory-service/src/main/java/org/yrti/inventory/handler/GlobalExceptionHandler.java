package org.yrti.inventory.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yrti.inventory.exception.InvalidArgumentException;
import org.yrti.inventory.exception.NotEnoughStockException;
import org.yrti.inventory.exception.ProductNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<String> handleProductNotFound(NotEnoughStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<String> handleProductNotFound(InvalidArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }




}
