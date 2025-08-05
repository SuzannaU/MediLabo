# Needs the artifact to be ready (use medilabo builder)
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=medilabo-builder /app/registry-service/target/registry-service-0.0.1-SNAPSHOT.jar registry-service.jar
RUN apk add --no-cache curl  # used for healthcheck
ENTRYPOINT ["java","-jar","/app/registry-service.jar"]