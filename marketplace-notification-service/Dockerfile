FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY settings.gradle .
COPY marketplace-notification-service/build.gradle ./marketplace-notification-service/
COPY marketplace-notification-service/src ./marketplace-notification-service/src

RUN ./gradlew :marketplace-notification-service:build -x test -Pcheckstyle.skip -Ppmd.skip

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/marketplace-notification-service/build/libs/*.jar ./app.jar

EXPOSE 8082

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]