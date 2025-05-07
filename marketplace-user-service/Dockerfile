FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper gradle/wrapper
COPY settings.gradle .
COPY marketplace-user-service/build.gradle ./marketplace-user-service/
COPY marketplace-user-service/src ./marketplace-user-service/src

RUN ./gradlew :marketplace-user-service:build -x test -Pcheckstyle.skip -Ppmd.skip

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/marketplace-user-service/build/libs/*.jar ./app.jar

EXPOSE 8083

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]