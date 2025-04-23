package org.yrti.inventory.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Продукт с ID " + id + " не найден.");
    }
}
