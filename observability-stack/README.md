# 🔍 Observability Stack - Production-Ready Monitoring Solution

This directory contains a complete observability stack using Docker Compose with enterprise-grade monitoring, logging, and tracing capabilities.

## 🏗️ General Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                        APPLICATION LAYER                            │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐   │
│  │   Java Apps     │    │  .NET Core Apps │    │   Other Apps    │   │
│  │                 │    │                 │    │                 │   │
│  │  ┌───────────┐  │    │  ┌───────────┐  │    │  ┌───────────┐  │   │
│  │  │ OpenTel   │  │    │  │ OpenTel   │  │    │  │ OpenTel   │  │   │
│  │  │ SDK/Agent │  │    │  │ SDK/Agent │  │    │  │ SDK/Agent │  │   │
│  │  └─────┬─────┘  │    │  └─────┬─────┘  │    │  └─────┬─────┘  │   │
│  └────────┼────────┘    └────────┼────────┘    └────────┼────────┘   │
└───────────┼─────────────────────┼─────────────────────┼─────────────┘
            │                     │                     │
            │ Metrics, Logs,      │                     │
            │ Traces via OTLP     │                     │
            │                     │                     │
            └─────────────────────┼─────────────────────┘
                                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    TELEMETRY COLLECTION LAYER                      │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │               OpenTelemetry Collector                       │   │
│  │                                                             │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │   │
│  │  │  Receivers  │  │ Processors  │  │      Exporters      │  │   │
│  │  │             │  │             │  │                     │  │   │
│  │  │ • OTLP gRPC │  │ • Batching  │  │ • Prometheus        │  │   │
│  │  │ • OTLP HTTP │  │ • Filtering │  │ • Tempo             │  │   │
│  │  │ • Host      │  │ • Transform │  │ • Console (logs)    │  │   │
│  │  │   Metrics   │  │ • Sampling  │  │                     │  │   │
│  │  └─────────────┘  └─────────────┘  └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────┬───────────────┬───────────────┬───────────────────┘
                  │               │               │
                  ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     STORAGE & PROCESSING LAYER                     │
│                                                                     │
│ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────────────┐ │
│ │   METRICS       │ │     TRACES      │ │         LOGS            │ │
│ │                 │ │                 │ │                         │ │
│ │ ┌─────────────┐ │ │ ┌─────────────┐ │ │ ┌─────────┐ ┌─────────┐ │ │
│ │ │ Prometheus  │ │ │ │    Tempo    │ │ │ │Logstash │ │  Elastic│ │ │
│ │ │             │ │ │ │             │ │ │ │         │ │ Search  │ │ │
│ │ │ • Time      │ │ │ │ • Trace     │ │ │ │ • Parse │ │         │ │ │
│ │ │   Series    │ │ │ │   Storage   │ │ │ │ • Filter│ │ • Index │ │ │
│ │ │ • Rules     │ │ │ │ • Query     │ │ │ │ • Enrich│ │ • Store │ │ │
│ │ │ • Alerts    │ │ │ │   Engine    │ │ │ │         │ │ • Search│ │ │
│ │ └─────────────┘ │ │ └─────────────┘ │ │ └─────────┘ └─────────┘ │ │
│ └─────────────────┘ └─────────────────┘ └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    VISUALIZATION & ALERTING LAYER                  │
│                                                                     │
│ ┌─────────────────────────┐  ┌─────────────────────────────────────┐ │
│ │        Grafana          │  │          Kibana                     │ │
│ │                         │  │                                     │ │
│ │ • Unified Dashboards    │  │ • Log Exploration                   │ │
│ │ • Metrics Visualization │  │ • Advanced Search                   │ │
│ │ • Trace Correlation     │  │ • Log Analysis                      │ │
│ │ • Log Integration       │  │ • Index Management                  │ │
│ │ • Alert Management      │  │ • Visualizations                    │ │
│ └─────────────────────────┘  └─────────────────────────────────────┘ │
│                                                                     │
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │                    AlertManager                                 │ │
│ │                                                                 │ │
│ │ • Alert Routing    • Notification Management                   │ │
│ │ • Grouping         • Silence Management                        │ │
│ │ • Inhibition       • Integration (Email, Slack, Webhook)       │ │
│ └─────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE MONITORING                       │
│                                                                     │
│ ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────┐   │
│ │ Node Exporter   │  │    cAdvisor     │  │   Custom Exporters  │   │
│ │                 │  │                 │  │                     │   │
│ │ • CPU, Memory   │  │ • Container     │  │ • Application       │   │
│ │ • Disk, Network │  │   Metrics       │  │   Specific          │   │
│ │ • System Stats  │  │ • Docker Stats  │  │ • Business Metrics  │   │
│ └─────────────────┘  └─────────────────┘  └─────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

## 🔧 Component Responsibilities

### 📊 **Telemetry Collection**
- **OpenTelemetry Collector**: Central hub for receiving, processing, and forwarding telemetry data
  - Receives data via OTLP (gRPC/HTTP), Prometheus scraping, and file monitoring
  - Processes data through batching, filtering, and enrichment
  - Routes data to appropriate storage backends

### 📈 **Metrics Stack**
- **Prometheus**: Time-series metrics storage and alerting
  - Scrapes metrics from applications and infrastructure
  - Stores metrics with configurable retention (15 days default)
  - Evaluates alerting rules and forwards alerts to AlertManager
  - Provides PromQL query language for metrics analysis

- **Node Exporter**: System-level metrics collection
  ## Observability stack — comprehensive documentation

  This document explains the structure, responsibilities, protocols and configuration of the observability stack in this folder. It includes ASCII interaction diagrams, step-by-step integration notes for the `collections-spring` application, file locations for important configs, and troubleshooting tips.

  Checklist (requirements from your request):
  - Create documentation for the observability stack (this file) — Done
  - Provide interaction diagrams with protocols — Done (ASCII diagrams)
  - Show all data flows including the `collections-spring` app — Done
  - Describe responsibility of each item/component — Done
  - Provide configuration of each element (file paths, snippets) — Done
  - Add other useful items for comprehensibility (ports, common issues, troubleshooting, TLS/auth notes) — Done

  If you want a different layout or additional diagrams (sequence diagrams, PlantUML, or network diagrams), tell me which format and I'll add them.

  ## Quick architecture overview (ASCII)

  Top-level flow and protocols:

  Application(s)  ->  OpenTelemetry SDK/Appenders  ->  OpenTelemetry Collector  ->  (Prometheus / Tempo / Elasticsearch)

  Detailed ASCII diagram (showing protocols and ports):

                                +-------------------------+
                                |  collections-spring App |
                                |  (Spring Boot)          |
                                |  - Actuator (/prom)     |
                                |  - OpenTelemetry SDK    |
                                +-----------+-------------+
                                            | OTLP gRPC / OTLP HTTP
                                            | (configured: otel.exporter.otlp.endpoint)
                                            v
                          +-------------------------------------------+
                          |         OpenTelemetry Collector         |
                          |  Receivers: OTLP(gRPC:4317, HTTP:4318)   |
                          |  HostMetrics receiver (scrape-like)      |
                          |  Processors: batch, resourcedetection    |
                          |  Exporters: prometheus(8889), otlp/tempo |
                          +---------+---------------+----------------+
                                    |               |
                                    | Prometheus    | Tempo (OTLP -> Tempo storage)
                                    | (pull)        | (OTLP gRPC to tempo:4317)
                                    v               v
                 +-------------------------+     +---------------+
                 |      Prometheus        |     |     Tempo     |
                 |  (9090) scrape targets |     |   (3200)      |
                 |  - scrapes: node,     |     |   traces store|
                 |    cadvisor, otel     |     |               |
                 +-----------+------------+     +-------+-------+
                             |                            |
                             |                            |
                             v                            v
                       +-----------+                +------------+
                       | Grafana   |                | AlertManager|
                       | (3000)    |                | (9093)      |
                       +-----------+                +------------+

  Logs path (separate):

    App logs -> Logstash (beats/tcp/http) -> Elasticsearch -> Kibana
    Protocols: beats (5044), tcp/udp (5000), http input (8080), ES HTTP API (9200)

  ## Component responsibilities (short)
  - OpenTelemetry Collector (`config/otel-collector/otel-collector-config.yml`): central receiver for OTLP (traces/metrics/logs), processors (batching, memory limits, transforms), and exporters to Prometheus/Tempo/Elastic.
  - Prometheus (`config/prometheus/prometheus.yml`): time-series DB for metrics, scrapes exporters and the OTel Collector (for custom metrics), evaluates alerting rules and sends alerts to AlertManager.
  - Grafana (`config/grafana/`): dashboards and alerting UI; visualizes Prometheus metrics and links traces from Tempo.
  - Tempo (`config/tempo/tempo.yml`): distributed traces storage and query backend for traces collected by OTel Collector.
  - Elasticsearch + Logstash + Kibana (ELK): log ingestion, parsing, indexing and exploration. Logstash transforms incoming logs and ships to Elasticsearch; Kibana visualizes indices.
  - AlertManager (`config/alertmanager/alertmanager.yml`): routes and manages alerts.
  - node-exporter, cAdvisor: infrastructure and container-level metric exporters scraped by Prometheus.

  ## Important configuration file locations (what to edit)
  - OpenTelemetry Collector: `config/otel-collector/otel-collector-config.yml`
  - Prometheus: `config/prometheus/prometheus.yml` and `config/prometheus/rules/`
  - Tempo: `config/tempo/tempo.yml`
  - Logstash pipeline: `config/logstash/pipeline/logstash.conf`
  - Kibana config: `config/kibana/kibana.yml`
  - Grafana provisioning and dashboards: `config/grafana/provisioning/` and `config/grafana/dashboards/`
  - Alertmanager: `config/alertmanager/alertmanager.yml`

  ## How data flows (detailed, including `collections-spring` app)

  1) Instrumentation in the app
     - `collections-spring` is configured to export OTLP to the collector. See `collections-spring/src/main/resources/application.properties`:
       - `otel.exporter.otlp.endpoint=http://otel-collector:4318`
       - `otel.traces.exporter=otlp`
       - `management.metrics.export.prometheus.enabled=true` (exposes /actuator/prometheus)

  2) Metrics
     - JVM & application metrics: exposed by Micrometer via the Actuator endpoint `/actuator/prometheus` on the app.
     - Prometheus scrapes the app directly (static config points to host.docker.internal:8081 in `config/prometheus/prometheus.yml`) OR you can configure the app to expose Prometheus and let Prometheus scrape it.
     - Application-level custom metrics can also be sent to the collector (OTLP metrics) and the collector exposes a Prometheus endpoint for scraping (`prometheus` exporter in collector on :8889).

  3) Traces
     - App sends traces via OTLP (gRPC or HTTP) to the Collector. Collector exports traces to Tempo via OTLP (configured `otlp/tempo` exporter).
     - Tempo stores traces; Grafana can query Tempo to show traces.

  4) Logs
     - App logs: Logback is configured in the project to use an OpenTelemetry appender (if enabled) that can emit logs to the collector via OTLP OR the app can log to stdout and/or file which is shipped to Logstash/Beats.
     - Logstash receives logs on ports 5044 (beats), 5000 (tcp/udp) and 8080 (http input) and forwards to Elasticsearch as data streams (see `logstash.conf`).

  5) Visualization and alerting
     - Grafana reads Prometheus for metrics and Tempo for traces. Kibana reads Elasticsearch indices for logs.
     - Prometheus evaluates alert rules and sends alerts to AlertManager which handles routing and notifications.

  ## Protocols and ports (summary)
  - OTLP gRPC: collector listens 0.0.0.0:4317 (configured in `config/otel-collector/otel-collector-config.yml`)
  - OTLP HTTP: collector listens 0.0.0.0:4318
  - Prometheus server: 9090 (host port)
  - Prometheus scrape endpoint (collector for metrics): 8888 (collector telemetry) and collector's prometheus exporter: 8889
  - Tempo: 3200
  - Elasticsearch: 9200
  - Kibana: 5601
  - Logstash: 5044 (beats), 5000 (tcp/udp), 8080 (http input)
  - Grafana: 3000
  - AlertManager: 9093

  ## Example snippets and explanations

  OpenTelemetry Collector (what matters)
   - Receivers: otlp (gRPC + HTTP), hostmetrics
   - Processors: batch, memory_limiter, resourcedetection, transform (for logs)
   - Exporters: prometheus, otlp/tempo, elasticsearch (for logs)

  File: `config/otel-collector/otel-collector-config.yml` — key exporter snippet (already present):

    otlp/tempo:
      endpoint: http://tempo:4317
      tls:
        insecure: true

  Prometheus (how the Java app is scraped)
  File: `config/prometheus/prometheus.yml`

    - job_name: 'java-apps'
      scrape_interval: 10s
      metrics_path: '/actuator/prometheus'
      static_configs:
        - targets: ['host.docker.internal:8081']
          labels:
            service: 'collections-spring'

  Notes: in Docker on Windows `host.docker.internal` maps to the host machine where your app might be running. If you run the Spring app in a container on the same Docker network, change to `collections-spring:8081` and ensure the container is attached to the `observability` network.

  Logstash (ingest and parsing)
  File: `config/logstash/pipeline/logstash.conf`
   - Inputs: beats (5044), tcp/udp 5000, http 8080
   - Outputs: Elasticsearch using data streams

  Elasticsearch output config in Logstash sends data streams named `collections-spring` (see `data_stream_dataset` in `logstash.conf`).

  Grafana provisioning
   - Data sources and dashboards are stored under `config/grafana/provisioning/` and `config/grafana/dashboards/`. Edit these to add dashboards or add panels.

  ## How to integrate `collections-spring` (step-by-step)
  1. Ensure the app has the OpenTelemetry SDK and Micrometer Prometheus registry dependencies present (check `pom.xml` under `collections-spring`).
  2. In `collections-spring/src/main/resources/application.properties` confirm:
     - `otel.exporter.otlp.endpoint=http://otel-collector:4318`
     - `management.metrics.export.prometheus.enabled=true`
     - `server.port=8081` (default used by Prometheus scrape config)
  3. Run the stack with Docker Compose:

  ```powershell
  # from observability-stack directory
  docker-compose up -d
  ```

  4. Start the Spring app locally (or in a container attached to the `observability` network). If running locally on the host, Prometheus uses `host.docker.internal:8081` to scrape metrics. If you run the app in a container, adjust `prometheus.yml` to point at the container name.

  5. Verify traces are arriving:
     - Generate some traffic (curl or use the app UI) that will create traces.
     - Check the collector health: http://localhost:13133
     - Query Tempo from Grafana or hit Tempo status: http://localhost:3200/status

  6. Verify metrics:
     - Visit Prometheus: http://localhost:9090 and run a query like `jvm_memory_used_bytes` or check targets page `Status -> Targets` to ensure the `java-apps` target is up.

  7. Verify logs:
     - Check Logstash logs or Kibana to see new indices: http://localhost:5601

  ## Troubleshooting (common issues and checks)
  - Prometheus target down for `host.docker.internal:8081`:
    - If app runs in container, change target to container name `collections-spring:8081` and attach it to the `observability` network.
    - Ensure Actuator endpoint `/actuator/prometheus` is enabled (management endpoints config).

  - OTLP data not seen in Tempo:
    - Confirm `otel.exporter.otlp.endpoint` in app points to `http://otel-collector:4318` or `http://localhost:4318` depending on where the app runs.
    - Check collector logs for receiver errors and memory_limiter triggers.

  - Logs not indexed in Kibana:
    - Ensure Logstash is receiving logs (check `docker-compose logs logstash`).
    - Validate Logstash pipeline (`logstash.conf`) grok patterns; run sample logs through the grok debugger (Kibana Dev Tools or online grok tester).

  - Tempo permission errors on local volumes:
    - Symptom: Tempo fails to start with messages like "permission denied" for paths under `/var/tempo` (index.pb.zst, wal files).
    - Cause: The Docker volume `observability-stack_tempo_data` contains files owned by `root:root` while Tempo runs as user `tempo` (uid 10001). Tempo must be able to write and unlink WAL and trace files.
    - Automated fix: The `tempo` service in `docker-compose.yml` now runs an `entrypoint` that does `chown -R 10001:10001 /var/tempo` before starting Tempo. This ensures correct ownership on container start.
      - Automated fix: The stack now includes `init-tempo.sh` (mounted into the tempo container) which fixes ownership and starts Tempo. This script is used as the `entrypoint` for the `tempo` service so the fix runs automatically on container start.
    - Manual fallback (run from `observability-stack` directory):

  ```powershell
  # Ensure the tempo volume is owned by the tempo user (uid 10001)
  docker run --rm -v observability-stack_tempo_data:/data alpine sh -c "chown -R 10001:10001 /data && echo 'chown done'"
  ```

    - After either fix, restart the service:

  ```powershell
  docker compose restart tempo
  docker compose logs tempo --tail=100
  ```

  ## Security and production notes
  - Currently many services run without authentication for local development. For production:
    - Enable TLS and authentication for Elasticsearch, Kibana, Grafana, Tempo and the collector.
    - Use secure OTLP endpoints with TLS and auth when sending telemetry.
    - Do not use `host.docker.internal` in production; use service discovery and secure networking.

  ## Backup / maintenance
  - Backup volumes are supported in `manage-stack.ps1` (backup subcommand). Check `manage-stack.ps1` for how it archives volumes to `./backup/`.

  ## Useful commands
  From `observability-stack` directory:

  ```powershell
  # Start
  docker-compose up -d

  # Stop
  docker-compose down

  # View logs
  docker-compose logs -f grafana

  # Check collector health
  curl http://localhost:13133
  ```

  ## Next steps / enhancements I recommend
  - Add self-monitoring dashboards for the observability stack in Grafana.
  - Enable TLS and basic auth for production deployments.
  - Configure ILM for Elasticsearch indices and external long-term storage for Prometheus (remote_write) and Tempo.
  - Add sample dashboards and alerting rules in `config/grafana/provisioning` and `config/prometheus/rules` respectively.

  If you'd like, I can also:
  - Generate a PlantUML sequence diagram for the flows.
  - Create a small README for `collections-spring` showing how to run it in a container attached to the `observability` network and adjust `prometheus.yml` accordingly.

  ---
  Requirements coverage: all checklist items above are implemented in this file. If you want any section expanded (for example full example dashboards or a visual sequence diagram), tell me which one and I will add it.

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
    
    <!-- OpenTelemetry OTLP Exporter -->
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-exporter-otlp</artifactId>
    </dependency>
    
    <!-- Logback OpenTelemetry Appender -->
    <dependency>
        <groupId>io.opentelemetry.instrumentation</groupId>
        <artifactId>opentelemetry-logback-appender-1.0</artifactId>
        <version>1.29.0-alpha</version>
    </dependency>
</dependencies>
```

### ⚙️ **Application Properties**
Add to `application.properties`:

```properties
# Application Information
spring.application.name=my-java-service
management.endpoints.web.exposure.include=health,metrics,prometheus,info
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true

# OpenTelemetry Configuration
otel.service.name=${spring.application.name}
otel.service.version=@project.version@
otel.service.instance.id=${spring.application.name}-${random.uuid}
otel.resource.attributes=environment=development,team=backend

# OTLP Exporter (to OpenTelemetry Collector)
otel.exporter.otlp.endpoint=http://localhost:4317
otel.exporter.otlp.protocol=grpc
otel.traces.exporter=otlp
otel.metrics.exporter=otlp,prometheus
otel.logs.exporter=otlp

# Instrumentation
otel.instrumentation.spring-webmvc.enabled=true
otel.instrumentation.jdbc.enabled=true
otel.instrumentation.hikaricp.enabled=true
otel.instrumentation.kafka.enabled=true
```

### 📝 **Logback Configuration**
Create `logback-spring.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    
    <!-- Console Appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level [%X{traceId},%X{spanId}] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- OpenTelemetry Appender -->
    <appender name="OTLP" class="io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender">
        <captureExperimentalAttributes>true</captureExperimentalAttributes>
        <captureMdcAttributes>*</captureMdcAttributes>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="OTLP"/>
    </root>
</configuration>
```

### 🎯 **Prometheus Configuration Update**
Add your Java application to `config/prometheus/prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'my-java-service'
    static_configs:
      - targets: ['my-java-service:8080']  # Adjust host and port
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s
```

## 🔌 .NET Core Application Integration

### 📦 **NuGet Packages**
Add to your `.csproj`:

```xml
<PackageReference Include="OpenTelemetry" Version="1.5.1" />
<PackageReference Include="OpenTelemetry.Extensions.Hosting" Version="1.5.1" />
<PackageReference Include="OpenTelemetry.Instrumentation.AspNetCore" Version="1.5.1-beta.1" />
<PackageReference Include="OpenTelemetry.Instrumentation.Http" Version="1.5.1-beta.1" />
<PackageReference Include="OpenTelemetry.Instrumentation.SqlClient" Version="1.5.1-beta.1" />
<PackageReference Include="OpenTelemetry.Exporter.OpenTelemetryProtocol" Version="1.5.1" />
<PackageReference Include="OpenTelemetry.Exporter.Prometheus.AspNetCore" Version="1.5.1-rc.1" />
```

### ⚙️ **Program.cs Configuration**
```csharp
using OpenTelemetry.Logs;
using OpenTelemetry.Metrics;
using OpenTelemetry.Resources;
using OpenTelemetry.Trace;

var builder = WebApplication.CreateBuilder(args);

// Configure OpenTelemetry
builder.Services.AddOpenTelemetry()
    .WithTracing(tracerProviderBuilder =>
    {
        tracerProviderBuilder
            .AddSource("MyDotNetService")
            .SetResourceBuilder(ResourceBuilder.CreateDefault()
                .AddService("my-dotnet-service", "1.0.0")
                .AddAttributes(new Dictionary<string, object>
                {
                    ["environment"] = "development",
                    ["team"] = "backend"
                }))
            .AddAspNetCoreInstrumentation()
            .AddHttpClientInstrumentation()
            .AddSqlClientInstrumentation()
            .AddOtlpExporter(options =>
            {
                options.Endpoint = new Uri("http://localhost:4317");
                options.Protocol = OpenTelemetry.Exporter.OtlpExportProtocol.Grpc;
            });
    })
    .WithMetrics(meterProviderBuilder =>
    {
        meterProviderBuilder
            .SetResourceBuilder(ResourceBuilder.CreateDefault()
                .AddService("my-dotnet-service", "1.0.0"))
            .AddAspNetCoreInstrumentation()
            .AddHttpClientInstrumentation()
            .AddPrometheusExporter()
            .AddOtlpExporter(options =>
            {
                options.Endpoint = new Uri("http://localhost:4317");
                options.Protocol = OpenTelemetry.Exporter.OtlpExportProtocol.Grpc;
            });
    });

// Configure Logging
builder.Logging.AddOpenTelemetry(logging =>
{
    logging.SetResourceBuilder(ResourceBuilder.CreateDefault()
        .AddService("my-dotnet-service", "1.0.0"));
    logging.AddOtlpExporter(options =>
    {
        options.Endpoint = new Uri("http://localhost:4317");
        options.Protocol = OpenTelemetry.Exporter.OtlpExportProtocol.Grpc;
    });
});

var app = builder.Build();

// Add Prometheus metrics endpoint
app.MapPrometheusScrapingEndpoint();

app.Run();
```

### ⚙️ **appsettings.json Configuration**
```json
{
  "Logging": {
    "LogLevel": {
      "Default": "Information",
      "Microsoft.AspNetCore": "Warning"
    }
  },
  "OpenTelemetry": {
    "ServiceName": "my-dotnet-service",
    "ServiceVersion": "1.0.0"
  }
}
```

### 🎯 **Prometheus Configuration Update**
Add your .NET application to `config/prometheus/prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'my-dotnet-service'
    static_configs:
      - targets: ['my-dotnet-service:5000']  # Adjust host and port
    metrics_path: '/metrics'
    scrape_interval: 10s
```

## 🚀 Quick Start

### Prerequisites
- Docker Desktop installed and running
- At least 8GB RAM available
- PowerShell (for Windows scripts)

### 1. Start the Stack

```powershell
# Start all services
docker-compose up -d

# Check status
.\manage-stack.ps1 status

# Validate health
.\validate-stack.ps1
```

### 2. Access the Services

| Service | URL | Default Credentials |
|---------|-----|-------------------|
| **Grafana** | http://localhost:3000 | admin / admin123 |
| **Prometheus** | http://localhost:9090 | - |
| **Kibana** | http://localhost:5601 | - |
| **Elasticsearch** | http://localhost:9200 | - |
| **Tempo** | http://localhost:3200/status | - |
| **AlertManager** | http://localhost:9093 | - |

### 3. Send Test Data

```bash
# Test metrics endpoint (if your app is running)
curl http://localhost:8080/actuator/prometheus

# Test OTLP endpoint
curl -X POST http://localhost:4318/v1/traces \
  -H "Content-Type: application/json" \
  -d '{"resourceSpans":[]}'
```

## 🛠️ Management Commands

```powershell
# Start/Stop Services
.\manage-stack.ps1 start
.\manage-stack.ps1 stop

# Restart specific service
.\manage-stack.ps1 restart -Service prometheus

# View logs
.\manage-stack.ps1 logs -Service grafana -Follow

# Validate stack health
.\validate-stack.ps1

# Cleanup (removes all data)
.\manage-stack.ps1 clean
```

## 🏭 Production Considerations

### Scaling
- Use external storage for Elasticsearch (AWS OpenSearch, etc.)
- Implement Prometheus federation for large environments
- Use Tempo's distributed storage backend
- Set up Grafana in HA mode

### Security
- Enable authentication in all services
- Use TLS certificates
- Implement network segmentation
- Regular security updates

### Retention Policies
- Prometheus: 15 days (configurable)
- Elasticsearch: Configure ILM policies
- Tempo: Configure retention in tempo.yml

### Monitoring the Monitoring Stack
- Enable self-monitoring for all components
- Set up external health checks
- Monitor resource usage

## 📈 Next Steps

1. **Instrument your applications** with OpenTelemetry
2. **Create custom dashboards** in Grafana
3. **Set up alerting** via email/Slack
4. **Implement log correlation** with trace IDs
5. **Scale components** based on your load

For more detailed configuration, check the individual config files in the `config/` directory.
