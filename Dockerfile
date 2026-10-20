# Stage 1: Build the Spring Boot application using Maven
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy pom.xml and source code from the backend directory
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B || true

COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal JRE runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create a non-root system user for security
RUN groupadd -r sweetdelight && useradd -r -g sweetdelight appuser

# Copy built application from builder stage
COPY --from=builder /workspace/target/*.jar app.jar
RUN chown -R appuser:sweetdelight /app

USER appuser

# Render sets PORT dynamically (e.g. 10000 or 8080)
ENV PORT=8080
EXPOSE ${PORT}

# Launch Spring Boot with container-aware JVM flags and explicit PORT binding
ENTRYPOINT ["sh", "-c", "exec java -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT} -jar app.jar"]
