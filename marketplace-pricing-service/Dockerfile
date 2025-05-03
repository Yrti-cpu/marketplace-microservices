FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY settings.gradle .
COPY marketplace-pricing-service/build.gradle ./marketplace-pricing-service/
COPY marketplace-pricing-service/src ./marketplace-pricing-service/src

RUN ./gradlew :marketplace-pricing-service:build -x test -Pcheckstyle.skip -Ppmd.skip

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/marketplace-pricing-service/build/libs/*.jar ./app.jar

EXPOSE 8084

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]