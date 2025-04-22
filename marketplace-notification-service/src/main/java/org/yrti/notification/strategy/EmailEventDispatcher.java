package org.yrti.notification.strategy;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventDispatcher {


    private final List<EmailStrategy<?>> strategies;
    private Map<Class<?>, EmailStrategy<?>> strategyMap;

    @PostConstruct
    public void init() {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getClass().getGenericInterfaces()[0]
                                .getClass(),
                        strategy -> strategy,
                        (s1, s2) -> s1 // в случае дубликатов
                ));
    }

    @SuppressWarnings("unchecked")
    public <T> void dispatchEmail(T event) {
        EmailStrategy<T> strategy = (EmailStrategy<T>) strategies.stream()
                .filter(s -> s.supports(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Стратегия не найдена для события: " + event.getClass()));

        strategy.sendEmail(event);
    }
}
