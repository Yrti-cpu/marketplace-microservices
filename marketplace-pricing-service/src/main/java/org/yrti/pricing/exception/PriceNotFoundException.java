package org.yrti.pricing.exception;

public class PriceNotFoundException extends RuntimeException {

  public PriceNotFoundException(Long id) {

    super("Цена для товара с ID " + id + " не найдена.");
  }
}
