package org.yrti.order.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
    public OrderNotFoundException(Long orderId) {
        super("Заказ с ID " + orderId + " не найден");
    }
}
