# 🛒 Marketplace Microservices

![Java](https://img.shields.io/badge/Java-17-red)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-green)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2024.0-blue)
![Kafka](https://img.shields.io/badge/Kafka-3.8-purple)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)

---

## 🛠️ Технологический стек

| Категория          | Технологии                                                                                                   |
|--------------------|--------------------------------------------------------------------------------------------------------------|
| **Backend**        | Java 17, Spring Boot 3, Spring Cloud (Gateway, Eureka, OpenFeign), Spring Data (JPA, Redis), ORM (Hibernate) |
| **Базы данных**    | PostgreSQL, Liquibase                                                                                        |
| **Тестирование**   | JUnit 5, Mockito, H2                                                                                         |
| **Инфраструктура** | Docker,  Kafka, Zookeeper, Service Discovery (Eureka)                                                        |
| **Инструменты**    | Lombok, SLF4J, PMD, Checkstyle, JaCoCo                                                                       |

---

## 📦 Микросервисы

| Сервис                   | Описание                                       |
|--------------------------|------------------------------------------------|
| **order-service**        | Управление заказами (создание, отмена, статус) |
| **inventory-service**    | Резервация и списание товаров со склада        |
| **user-service**         | Управление пользователями                      |
| **pricing-service**      | Расчет цен и скидок на товары                  |
| **payment-service**      | Обработка платежей                             |
| **notification-service** | Отправка email                                 |
| **gateway-service**      | Единая точка входа (API Gateway)               |
| **eureka-service**       | Service Discovery (централизованный реестр)    |

---

## 🏗️ Архитектура

---

## 📅 Планы развития

- JWT аутентификация
- Swagger документация API
- Покрытие unit и integration тестами
- Redis для кеширования популярных товаров и токенов

---

## 🚀 Полный запуск

```bash
docker-compose up -d --build
```
## 🚪 Доступ к сервисам

`API Gateway` → http://localhost:8085/api/...  
`Eureka Dashboard` → http://localhost:8761
