# Multi-stage build to produce a small runtime image
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy build descriptors first to leverage Docker layer caching
COPY pom.xml /app/pom.xml
COPY mvnw /app/mvnw
COPY .mvn /app/.mvn

# Copy source
COPY src /app/src

# Build the application (skips tests for faster container builds)
RUN chmod +x /app/mvnw && ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/newsAPI-0.0.1-SNAPSHOT.jar app.jar

# Allow runtime JVM tuning via JAVA_OPTS if needed
ENV JAVA_OPTS=""

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
