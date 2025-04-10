package org.yrti.inventory.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
  public ProductNotFoundException(Long id) {
    super("Продукт с ID " + id + " не найден.");
  }

}
