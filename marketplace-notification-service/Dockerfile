FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /app

COPY ../gradlew .
COPY ../gradle/wrapper gradle/wrapper

COPY build.gradle .
COPY src src

RUN ./gradlew :marketplace-notification-service:build -x test

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/marketplace-notification-service-*.jar ./app.jar

EXPOSE 8082

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]