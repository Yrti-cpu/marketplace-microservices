package org.yrti.pricing.exception;

public class DiscountNotFoundException extends RuntimeException {

  public DiscountNotFoundException(Long id) {

    super("Скидка для товара с ID " + id + " не найдена.");
  }
}
