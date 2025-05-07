FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY settings.gradle .
COPY marketplace-payment-service/build.gradle ./marketplace-payment-service/
COPY marketplace-payment-service/src ./marketplace-payment-service/src

RUN ./gradlew :marketplace-payment-service:build -x test -Pcheckstyle.skip -Ppmd.skip

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/marketplace-payment-service/build/libs/*.jar ./app.jar

EXPOSE 8086

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]