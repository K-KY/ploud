ARG BASE_IMAGE=eclipse-temurin:21-jre
FROM ${BASE_IMAGE}

WORKDIR /app

COPY build/libs/ploud-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
