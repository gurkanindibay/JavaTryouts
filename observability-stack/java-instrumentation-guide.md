# Java Application OpenTelemetry Configuration

This directory contains example configurations for instrumenting your Java applications with OpenTelemetry.

## 🔧 Spring Boot Configuration

### Maven Dependencies

Add these dependencies to your `pom.xml`:

```xml
<dependencies>
    <!-- Spring Boot Actuator for metrics endpoint -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Micrometer Prometheus registry -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
    
    <!-- OpenTelemetry Spring Boot Starter -->
    <dependency>
        <groupId>io.opentelemetry.instrumentation</groupId>
        <artifactId>opentelemetry-spring-boot-starter</artifactId>
        <version>1.29.0-alpha</version>
    </dependency>
    
    <!-- OpenTelemetry Exporter OTLP -->
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-exporter-otlp</artifactId>
    </dependency>
    
    <!-- Logback OTLP Appender for logs -->
    <dependency>
        <groupId>io.opentelemetry.instrumentation</groupId>
        <artifactId>opentelemetry-logback-appender-1.0</artifactId>
        <version>1.29.0-alpha</version>
    </dependency>
</dependencies>
```

### Application Properties

Add to your `application.properties`:

```properties
# Application Info
spring.application.name=your-service-name
info.app.name=${spring.application.name}
info.app.version=@project.version@

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus,env
management.endpoint.health.show-details=always
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true
management.metrics.distribution.percentiles-histogram.http.server.requests=true
management.metrics.distribution.percentiles.http.server.requests=0.5,0.9,0.95,0.99

# OpenTelemetry Configuration
otel.service.name=${spring.application.name}
otel.service.version=@project.version@
otel.service.instance.id=${spring.application.name}-${random.uuid}
otel.resource.attributes=environment=development,team=backend

# OTLP Exporter Configuration
otel.exporter.otlp.endpoint=http://localhost:4317
otel.exporter.otlp.protocol=grpc
otel.exporter.otlp.compression=gzip

# Traces Configuration
otel.traces.exporter=otlp
otel.traces.sampler=parentbased_traceidratio
otel.traces.sampler.arg=1.0

# Metrics Configuration
otel.metrics.exporter=otlp,prometheus
otel.metric.export.interval=30s

# Logs Configuration  
otel.logs.exporter=otlp
otel.instrumentation.logback-appender.experimental-log-attributes=true

# Instrumentation Configuration
otel.instrumentation.spring-webmvc.enabled=true
otel.instrumentation.spring-webflux.enabled=true
otel.instrumentation.jdbc.enabled=true
otel.instrumentation.hikaricp.enabled=true
otel.instrumentation.lettuce.enabled=true
otel.instrumentation.redis.enabled=true
otel.instrumentation.kafka.enabled=true

# Propagation
otel.propagators=tracecontext,baggage,b3
```

### Logback Configuration

Create or update `src/main/resources/logback-spring.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    
    <!-- Console Appender with colored output -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- File Appender -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>3GB</totalSizeCap>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - [%X{traceId},%X{spanId}] %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- OpenTelemetry OTLP Appender -->
    <appender name="OTLP" class="io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender">
        <resource>
            <attribute>
                <key>service.name</key>
                <value>${spring.application.name}</value>
            </attribute>
        </resource>
        <includeInstrumentationScopeInfo>true</includeInstrumentationScopeInfo>
        <captureExperimentalAttributes>true</captureExperimentalAttributes>
        <captureCodeAttributes>true</captureCodeAttributes>
        <captureMarkerAttribute>true</captureMarkerAttribute>
        <captureKeyValuePairAttributes>true</captureKeyValuePairAttributes>
        <captureLoggerContext>true</captureLoggerContext>
        <captureMdcAttributes>*</captureMdcAttributes>
    </appender>
    
    <!-- Root Logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="OTLP"/>
    </root>
    
    <!-- Application-specific loggers -->
    <logger name="com.example" level="DEBUG"/>
    <logger name="org.springframework.web" level="DEBUG"/>
    <logger name="org.springframework.transaction" level="DEBUG"/>
    
    <!-- Reduce noise from libraries -->
    <logger name="org.springframework.boot.autoconfigure" level="WARN"/>
    <logger name="org.hibernate" level="WARN"/>
    <logger name="com.zaxxer.hikari" level="WARN"/>
    
    <springProfile name="production">
        <root level="WARN">
            <appender-ref ref="FILE"/>
            <appender-ref ref="OTLP"/>
        </root>
    </springProfile>
</configuration>
```

## 🚀 Manual Instrumentation Examples

### Custom Metrics

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {
    
    private final Counter orderCounter;
    private final Timer orderProcessingTimer;
    
    public BusinessService(MeterRegistry meterRegistry) {
        this.orderCounter = Counter.builder("orders.processed")
            .description("Number of orders processed")
            .tag("service", "business")
            .register(meterRegistry);
            
        this.orderProcessingTimer = Timer.builder("order.processing.time")
            .description("Time taken to process an order")
            .register(meterRegistry);
    }
    
    public void processOrder(Order order) {
        Timer.Sample sample = Timer.start();
        try {
            // Business logic here
            Thread.sleep(100); // Simulate processing
            orderCounter.increment();
        } catch (Exception e) {
            // Handle exception
        } finally {
            sample.stop(orderProcessingTimer);
        }
    }
}
```

### Custom Traces

```java
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("payment-service");
    
    public PaymentResult processPayment(PaymentRequest request) {
        Span span = tracer.spanBuilder("payment.process")
            .setAttribute("payment.amount", request.getAmount())
            .setAttribute("payment.currency", request.getCurrency())
            .setAttribute("customer.id", request.getCustomerId())
            .startSpan();
            
        try (Scope scope = span.makeCurrent()) {
            // Add events
            span.addEvent("payment.validation.start");
            validatePayment(request);
            span.addEvent("payment.validation.complete");
            
            span.addEvent("payment.processing.start");
            PaymentResult result = executePayment(request);
            span.addEvent("payment.processing.complete");
            
            // Add result attributes
            span.setAttribute("payment.status", result.getStatus());
            span.setAttribute("payment.transaction.id", result.getTransactionId());
            
            return result;
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
```

### Structured Logging with Trace Context

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    public void createOrder(CreateOrderRequest request) {
        // MDC will automatically include trace context
        MDC.put("user.id", request.getUserId());
        MDC.put("order.type", request.getOrderType());
        
        try {
            logger.info("Creating order for user: {}", request.getUserId());
            
            // Business logic
            Order order = new Order(request);
            
            logger.info("Order created successfully: orderId={}, amount={}", 
                       order.getId(), order.getAmount());
            
        } catch (Exception e) {
            logger.error("Failed to create order for user: {}", request.getUserId(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
```

## 🔍 Testing Your Instrumentation

### Health Check Endpoint

Create a custom health indicator:

```java
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ObservabilityHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // Check if telemetry is working
        boolean telemetryWorking = checkTelemetryConnection();
        
        if (telemetryWorking) {
            return Health.up()
                .withDetail("telemetry", "connected")
                .withDetail("traces", "enabled")
                .withDetail("metrics", "enabled")
                .build();
        } else {
            return Health.down()
                .withDetail("telemetry", "disconnected")
                .build();
        }
    }
    
    private boolean checkTelemetryConnection() {
        // Implement connection check logic
        return true;
    }
}
```

### Testing Commands

```bash
# Test metrics endpoint
curl http://localhost:8080/actuator/prometheus

# Test health endpoint
curl http://localhost:8080/actuator/health

# Generate test data
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":"123","amount":99.99,"currency":"USD"}'
```

## 📊 Docker Integration

If you want to run your Java app in Docker alongside the observability stack:

```dockerfile
FROM openjdk:17-jre-slim

# Add OpenTelemetry Java agent
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/
COPY target/your-app.jar /app/app.jar

WORKDIR /app

# Run with OpenTelemetry agent
ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "app.jar"]
```

Then add to your docker-compose.yml:

```yaml
your-app:
  build: ../your-app
  ports:
    - "8080:8080"
  environment:
    - OTEL_SERVICE_NAME=your-app
    - OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
  networks:
    - observability
  depends_on:
    - otel-collector
```
