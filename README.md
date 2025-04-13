# Marketplace Microservices

## Микросервисы
- `marketplace-inventory-service` — управление остатками товаров, резервирование
- `marketplace-order-service` — оформление заказов и отправка сообещний в `marketplace-notification-service`
- `marketplace-notification-service` - нотификация через gmail о созданном заказе (в процессе)
## Запуск
(будет добавлен Docker)

## Технологии
- Java 17
- Spring Boot
- PostgreSQL, Spring JPA
- JUnit, Mockito
- Kafka
- Redis (позже)