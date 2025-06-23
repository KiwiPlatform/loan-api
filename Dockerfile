# ===================================================================
# DOCKERFILE PARA KIWIPAY LOAN API - RENDER DEPLOYMENT
# ===================================================================

# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Set execution permission for Gradle wrapper
RUN chmod +x ./gradlew

# Copy source code
COPY src src

# Build the application (skip tests for faster deployment)
RUN ./gradlew clean build -x test

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp

# Create a non-root user for security
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership of the app
RUN chown spring:spring app.jar
USER spring:spring

# Expose port (Render will automatically bind to $PORT)
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
           "-Djava.security.egd=file:/dev/./urandom", \
           "-Dserver.port=${PORT:-8081}", \
           "-jar", \
           "/app.jar"] 