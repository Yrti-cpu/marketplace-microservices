# 🛒 Marketplace Microservices

![Java](https://img.shields.io/badge/Java-17-red)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green)
![Kafka](https://img.shields.io/badge/Kafka-3.8-purple)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)

---

## 🛠️ Технологический стек

| Категория          | Технологии                                                   |
|--------------------|--------------------------------------------------------------|
| **Backend**        | Java 17, Spring Boot 3, Spring Cloud OpenFeign, Spring Kafka |
| **Базы данных**    | PostgreSQL, Liquibase                                        |
| **Инфраструктура** | Docker, docker-compose, Kafka, Zookeeper                     |
| **Мониторинг**     | Kafka UI                                                     |
| **Инструменты**    | Lombok, SLF4J, PMD, Checkstyle, JaCoCo                       |

---

## 📦 Микросервисы

| Сервис                   | Порт | Описание                                       |
|--------------------------|------|------------------------------------------------|
| **order-service**        | 8081 | Управление заказами (создание, отмена, статус) |
| **inventory-service**    | 8080 | Резервация и списание товаров со склада        |
| **user-service**         | 8083 | Управление пользователями                      |
| **pricing-service**      | 8084 | Расчет цен и скидок на товары                  |
| **payment-service**      | 8086 | Обработка платежей через внешние системы       |
| **notification-service** | 8082 | Отправка email                                 |
| **Kafka UI**             | 8085 | Визуализация топиков и сообщений Kafka         |

---

## 🏗️ Архитектура

---

## 📅 Планы развития

- JWT аутентификация
- Swagger документация API
- Генерация PDF чеков для email
- Покрытие unit и integration тестами
- Redis для кеширования популярных товаров

---

## 🚀 Полный запуск

```bash
docker-compose -f docker-compose.full.yml up -d