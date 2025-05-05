FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY settings.gradle .
COPY marketplace-eureka-service/build.gradle ./marketplace-eureka-service/
COPY marketplace-eureka-service/src ./marketplace-eureka-service/src

RUN ./gradlew :marketplace-eureka-service:build -x test

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/marketplace-eureka-service/build/libs/*.jar ./app.jar

EXPOSE 8761

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]