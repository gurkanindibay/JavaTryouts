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
  - Exposes CPU, memory, disk, and network metrics
  - Runs on host system to collect machine-level statistics

- **cAdvisor**: Container metrics collection
  - Monitors Docker container resource usage
  - Provides container-specific performance metrics

### 🎯 **Visualization & Alerting**
- **Grafana**: Unified dashboard and visualization platform
  - Queries Prometheus for metrics, Tempo for traces, Elasticsearch for logs
  - Pre-configured with datasources and sample dashboards
  - Supports alerting and notification management

- **Kibana**: Log exploration and analysis interface
  - Provides advanced search and filtering for logs
  - Creates visualizations and index management for Elasticsearch

- **AlertManager**: Alert routing and notification hub
  - Receives alerts from Prometheus rule evaluation
  - Handles grouping, silencing, and routing to notification channels

## 📋 Quick Start Guide

### Prerequisites
- Docker Desktop installed and running
- At least 8GB RAM available
- Git (to clone repository)

### 🚀 **5-Minute Setup**

1. **Start the stack:**
```bash
# Navigate to observability-stack directory
cd observability-stack

# Start all services (Linux/macOS/WSL)
./manage-stack.sh up

# Or on Windows
.\manage-stack.ps1 start
```

2. **Verify stack health:**
```bash
# Run health checks
./manage-stack.sh smoke-test

# Or on Windows
.\validate-stack.ps1
```

3. **Access the services:** (see service URLs table below)

## 📊 Service Access URLs

| Service | URL | Default Credentials |
|---------|-----|-------------------|
| **Grafana** | http://localhost:3000 | admin / admin123 |
| **Prometheus** | http://localhost:9090 | - |
| **Kibana** | http://localhost:5601 | - |
| **Elasticsearch** | http://localhost:9200 | - |
| **Tempo** | http://localhost:3200/status | - |
| **AlertManager** | http://localhost:9093 | - |

## 🛠️ Management Scripts

This folder contains helper scripts for common operations:

- **`manage-stack.sh`** (Linux/macOS/WSL): Wraps docker compose operations and smoke testing
- **`manage-stack.ps1`** (Windows): PowerShell equivalent with additional features
- **`stack-smoke-test.sh`**: Validates all service endpoints after stack startup
- **`validate-stack.ps1`**: Windows health validation script

Example usage:
```bash
# Linux/macOS/WSL
./manage-stack.sh status
./manage-stack.sh smoke-test

# Windows
.\manage-stack.ps1 status
.\validate-stack.ps1
```

## 📖 Architecture Overview

This observability stack provides comprehensive monitoring through three main data types:

**Top-level data flow:**
```
Application(s) → OpenTelemetry SDK/Appenders → OpenTelemetry Collector → Storage (Prometheus/Tempo/Elasticsearch)
```

**Detailed architecture diagram:**

                                +-------------------------+
                                |  collections-spring App |
                                |  (Spring Boot)          |
                                |  - Actuator (/prom)     |
                                |  - OpenTelemetry SDK    |
                                +-----------+-------------+
                                            | OTLP gRPC / OTLP HTTP
                                            | (configured: otel.exporter.otlp.endpoint)
                                            v
                          +------------------------------------------+
                          |         OpenTelemetry Collector          |
                          |  Receivers: OTLP(gRPC:4317, HTTP:4318)   |
                          |  HostMetrics receiver (scrape-like)      |
                          |  Processors: batch, resourcedetection    |
                          |  Exporters: prometheus(8889), otlp/tempo |
                          +---------+---------------+----------------+
                                    |               |
                                    |               |
                          Traces (OTLP)       Metrics (OTLP/Prometheus)
                                    |               |
                                    v               v
                 +-------------------------+     +---------------+
                 |          Tempo          |     |   Prometheus  |
                 |       (traces store)    |     |    (9090)     |
                 |        (3200)           |     | scrape targets|
                 +-----------+-------------+     +-------+-------+
                             |                            |
                             |                            |
                             v                            v
                       +-----------+                +------------+
                       |  Grafana  |<---queries-----| Prometheus |
                       |  (3000)   |  (metrics & UI)|  (9090)    |
                       +----+------+                +------------+
                            |                             |
                            | optional: query alerts      | alerts
                            | (if Alertmanager datasource)| from rule eval
                            v                             |
                     +--------------+                     v
                     |    (optional)|                    +------------+
                     | Alertmanager |  <---------------- | Prometheus |
                     |    (9093)    |      alerts route +------------+
                     +--------------+
datasources: Prometheus, Tempo, Elasticsearch
(configured via `config/grafana/provisioning/datasources/datasources.yml`)

## 🔗 Data Flow Paths

### **Logs Path:**
```
App logs → Logstash (beats/tcp/http) → Elasticsearch → Kibana
Protocols: beats (5044), tcp/udp (5000), http input (8080), ES HTTP API (9200)
```

### **Metrics Path:**
```
App metrics → Prometheus scraping OR OTLP Collector → Prometheus → Grafana
Protocols: HTTP scraping (/actuator/prometheus), OTLP (4317/4318)
```

### **Traces Path:**
```
App traces → OTLP Collector → Tempo → Grafana
Protocols: OTLP gRPC/HTTP (4317/4318)
```

### **Alerts Path:**
```
Prometheus rule evaluation → AlertManager → Notification channels
Grafana → Prometheus (queries) OR AlertManager (alert display)
```

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

  ```bash
  # from observability-stack directory
  docker compose up -d
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

  ```bash
  # Ensure the tempo volume is owned by the tempo user (uid 10001)
  docker run --rm -v observability-stack_tempo_data:/data alpine sh -c "chown -R 10001:10001 /data && echo 'chown done'"
  ```

    - After either fix, restart the service:

  ```bash
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

  ## 🔧 Useful Commands
  From `observability-stack` directory:

  ```bash
  # Start stack
  docker compose up -d

  # Stop stack  
  docker compose down

  # View logs for specific service
  docker compose logs -f grafana

  # Check collector health
  curl http://localhost:13133

  # Run smoke test (validates all endpoints)
  ./stack-smoke-test.sh

  # Or use management helpers
  ./manage-stack.sh status
  ./manage-stack.sh smoke-test
  ```

  ---

## 📚 Additional Resources

- **Java Integration Guide**: `java-instrumentation-guide.md` - Detailed Java/Spring Boot setup
- **Access Guide**: `ACCESS-GUIDE.md` - Service access and authentication details
- **Configuration Files**: `config/` directory contains all service configurations

For questions or enhancements, check the individual config files in the `config/` directory or refer to the troubleshooting section above.
