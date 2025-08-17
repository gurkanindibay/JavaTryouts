# Java Application OpenTelemetry Configuration

This document shows recommended OpenTelemetry configuration for Spring Boot services and also documents the concrete settings used by the `collections-spring` example in this repository.

I'll keep general examples, but the bottom section titled "Project-specific settings" lists the exact values used in this repo so you can copy/paste them.

## 🔧 Spring Boot Configuration (example)

### Maven dependencies (example)

The project in this repository uses the OpenTelemetry Spring Boot starter and OTLP exporter. Example dependencies (use the versions declared in your project's `pom.xml` or the `Project-specific settings` section below):

```xml
<dependencies>
    <!-- Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Micrometer Prometheus registry -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>

    <!-- OpenTelemetry Spring Boot Starter (project uses the starter) -->
    <dependency>
        <groupId>io.opentelemetry.instrumentation</groupId>
        <artifactId>opentelemetry-spring-boot-starter</artifactId>
        <!-- version is managed in the project's pom properties -->
    </dependency>

    <!-- OTLP exporter -->
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-exporter-otlp</artifactId>
    </dependency>

    <!-- Logback / JSON helpers (optional) -->
    <dependency>
        <groupId>net.logstash.logback</groupId>
        <artifactId>logstash-logback-encoder</artifactId>
    </dependency>
</dependencies>
```

### Application properties (recommended examples)

Below are common properties. This repo's concrete settings are listed in the "Project-specific settings" section.

```properties
# Application
spring.application.name=your-service-name

# Actuator
management.endpoints.web.exposure.include=health,info,metrics,prometheus

# OpenTelemetry resource attributes & service name
otel.service.name=${spring.application.name}
otel.resource.attributes=environment=development,team=backend

# OTLP exporter - note: 4317 is OTLP/gRPC, 4318 is OTLP/HTTP
# Use the endpoint your collector exposes. In docker-compose we usually point to the collector service.
otel.exporter.otlp.endpoint=http://otel-collector:4318
otel.exporter.otlp.compression=gzip

# Traces
otel.traces.exporter=otlp
# Optional sampler example (parent-based, sample all):
# otel.traces.sampler=parentbased_traceidratio
# otel.traces.sampler.arg=1.0

# Metrics: choose how you export metrics. Two common approaches:
#  - Micrometer Prometheus (recommended when using Prometheus): enable the Prometheus endpoint
#  - OTLP metrics: send metrics to the collector via OTLP

# Example (Micrometer/Prometheus):
management.metrics.export.prometheus.enabled=true
otel.metrics.exporter=none

# Example (OTLP metrics):
# otel.metrics.exporter=otlp

# Logs
otel.logs.exporter=otlp

# Propagators (project uses tracecontext,baggage; add b3 if you need B3 compatibility)
otel.propagators=tracecontext,baggage

# Instrumentation toggles (enable only what you need)
otel.instrumentation.spring-web.enabled=true
otel.instrumentation.spring-webmvc.enabled=true
otel.instrumentation.jdbc.enabled=true
otel.instrumentation.kafka.enabled=true
otel.instrumentation.logback-appender.enabled=true
```

### Logback (short guidance)

This repository includes a canonical `logback-spring.xml` for the application. The file enables an OpenTelemetry log appender and also keeps console/file appenders for local debugging. Prefer the canonical file over duplicating a large example in this guide.

- Canonical file: `collections-spring/src/main/resources/logback-spring.xml`
- The logger pattern includes MDC keys for trace/span ids (for local correlation): `%X{traceId}`, `%X{spanId}`

If you need a minimal OTLP appender snippet, use the project file as reference; the starter and appender versions are managed in the project's `pom.xml`.

## 🚀 Instrumentation approaches: agent vs starter

Two common ways to instrument Java apps with OpenTelemetry:

- Java agent (auto-instrumentation): attach `-javaagent:opentelemetry-javaagent.jar` to the JVM. This works without changing application code and is convenient for containers and third-party JARs.
- Starter/library (in-process): add `opentelemetry-spring-boot-starter` and exporter dependencies to your `pom.xml`. This is the approach used by the `collections-spring` project in this repo.

Both approaches can be used together (agent + starter), but pick one to avoid duplicate instrumentation.

## � Testing your instrumentation (examples)

Quick health & metrics checks (adjust port if your app uses a different server.port):

```bash
# Example when server.port=8081 (this project uses 8081)
curl http://localhost:8081/actuator/prometheus
curl http://localhost:8081/actuator/health
```

Generate test traffic by calling an application endpoint (adjust as needed):

```bash
curl -X POST http://localhost:8081/api/orders \
    -H "Content-Type: application/json" \
    -d '{"userId":"123","amount":99.99,"currency":"USD"}'
```

## 📊 Docker Integration (agent example)

If you run the Java agent in Docker, attach the agent jar and point OTEL env vars to the collector (collector usually runs in docker-compose):

```dockerfile
FROM eclipse-temurin:21-jre-focal

# Add OpenTelemetry Java agent
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar
COPY target/your-app.jar /app/app.jar
WORKDIR /app

# Run with Java agent; use env vars to configure OTLP endpoint if preferred
ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "app.jar"]
```

And in `docker-compose.yml` you can set environment variables for the service:

```yaml
your-app:
    build: ../your-app
    ports:
        - "8081:8081"
    environment:
        - OTEL_SERVICE_NAME=your-app
        - OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
    networks:
        - observability
    depends_on:
        - otel-collector
```

## Project-specific settings (this repository)

The `collections-spring` application in this repo uses these concrete settings. Use these values when running the example locally or in docker-compose.

- `pom.xml` versions (see `collections-spring/pom.xml`):
    - `opentelemetry.version = 1.42.1`
    - `opentelemetry-spring-boot.version = 2.8.0`

- Application properties (file: `collections-spring/src/main/resources/application.properties`):
    - `server.port=8081`
    - `spring.application.name=collections-kafka-challenge`
    - `otel.service.name=collections-spring-app`
    - `otel.service.version=1.0.0`
    - `otel.resource.attributes=service.name=collections-spring-app,service.version=1.0.0,deployment.environment=local`
    - `otel.exporter.otlp.endpoint=http://otel-collector:4318` (OTLP/HTTP)
    - `otel.metrics.exporter=none` (project uses Micrometer/Prometheus)
    - `management.metrics.export.prometheus.enabled=true`
    - `otel.instrumentation.logback-appender.enabled=true`
    - `otel.instrumentation.spring-web.enabled=true`
    - `otel.instrumentation.jdbc.enabled=true`
    - `otel.instrumentation.kafka.enabled=true`
    - `otel.propagators=tracecontext,baggage`

- Logback canonical file: `collections-spring/src/main/resources/logback-spring.xml`
    - The file enables an OpenTelemetry appender plus console and file appenders.
    - It uses MDC keys for trace/span correlation: `%X{traceId}`, `%X{spanId}`

## Next steps & options

If you'd like, I can:

1. Update this guide to include a small end-to-end smoke test example that sends a trace to the collector and verifies it in Tempo.
2. Add env var examples for production vs local.
3. Replace the guide's long code snippets with direct excerpts from the canonical files.

Which of those would you like me to add now?
