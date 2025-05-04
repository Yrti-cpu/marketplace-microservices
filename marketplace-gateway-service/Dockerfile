FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY settings.gradle .
COPY marketplace-gateway-service/build.gradle ./marketplace-gateway-service/
COPY marketplace-gateway-service/src ./marketplace-gateway-service/src

RUN ./gradlew :marketplace-gateway-service:build -x test -Pcheckstyle.skip -Ppmd.skip

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/marketplace-gateway-service/build/libs/*.jar ./app.jar

EXPOSE 8085

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]