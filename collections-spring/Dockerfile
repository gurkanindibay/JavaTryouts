# Multi-stage build: Use Maven with Eclipse Temurin JDK 21
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Set Maven options for better performance and reliability (Java 21 compatible)
ENV MAVEN_OPTS="-Dmaven.repo.local=/app/.m2/repository -Xmx1024m -XX:MetaspaceSize=256m"

# Copy pom.xml first for better Docker layer caching
COPY pom.xml ./

# Download dependencies with retry and timeout settings (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=240 \
    -Dmaven.wagon.http.ssl.insecure=false \
    -Dmaven.wagon.http.ssl.allowall=false \
    || mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application with optimized settings
RUN mvn clean package -DskipTests -B \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=240

# Runtime stage: Use smaller JRE for running
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/collections-challenge-0.0.1-SNAPSHOT.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
