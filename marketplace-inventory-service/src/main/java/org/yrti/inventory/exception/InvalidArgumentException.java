package org.yrti.inventory.exception;

public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String message) {
        super(message);
    }
}

// TODO-крит чем джавовский IllegalArgumentException не угодил ?