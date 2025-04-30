FROM eclipse-temurin:17-jdk
LABEL authors="daniladotsenko"

WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]

EXPOSE 8083