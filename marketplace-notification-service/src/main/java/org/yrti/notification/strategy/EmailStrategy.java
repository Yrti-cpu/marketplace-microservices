package org.yrti.notification.strategy;

public interface EmailStrategy<T> {
    boolean supports(Class<?> eventType);
    void sendEmail(T event);
}
