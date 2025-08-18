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

## 📂 Configuration Files Reference

### **Core Configuration Files**
| Component | Configuration File | Purpose |
|-----------|-------------------|---------|
| **OpenTelemetry Collector** | `config/otel-collector/otel-collector-config.yml` | OTLP receivers, processors, exporters |
| **Prometheus** | `config/prometheus/prometheus.yml` | Scrape targets, alerting rules |
| **Prometheus Rules** | `config/prometheus/rules/*.yml` | Alert definitions and recording rules |
| **Tempo** | `config/tempo/tempo.yml` | Trace storage and retention settings |
| **Logstash** | `config/logstash/pipeline/logstash.conf` | Log parsing and routing |
| **Logstash Settings** | `config/logstash/logstash.yml` | Logstash daemon configuration |
| **Kibana** | `config/kibana/kibana.yml` | Elasticsearch connection and UI settings |
| **Grafana Datasources** | `config/grafana/provisioning/datasources/` | Auto-configured data sources |
| **Grafana Dashboards** | `config/grafana/dashboards/*.json` | Pre-built visualization dashboards |
| **AlertManager** | `config/alertmanager/alertmanager.yml` | Alert routing and notifications |

### **Pre-Built Dashboards Available**
The stack includes ready-to-use Grafana dashboards:
- `collections-spring-dashboard.json` - Main application overview
- `collections-spring-simple.json` - Simplified application metrics
- `java-app-metrics.json` - JVM and application performance
- `jvm-metrics.json` - Detailed JVM monitoring
- `spring-boot-overview.json` - Spring Boot specific metrics
- `kafka-metrics.json` - Kafka messaging metrics
- `database-connections.json` - Database connection monitoring
- `http-api-performance.json` - API performance and latency
- `system-resources.json` - Infrastructure resource monitoring
- `library-business-metrics.json` - Custom business metrics

### **Alert Rules Configured**
Current alerting coverage includes:
- **Application Health**: Service availability, error rates
- **JVM Monitoring**: Heap usage, garbage collection
- **Infrastructure**: CPU, memory, disk space
- **Elasticsearch**: Cluster health monitoring

View all rules in: `config/prometheus/rules/alerts.yml` and `config/prometheus/rules/collections-spring-alerts.yml`

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

## 🔧 Advanced Configuration

### **OpenTelemetry Collector Details**
The collector is configured with:
- **Receivers**: OTLP (gRPC:4317, HTTP:4318), Host metrics every 10s
- **Processors**: Memory limiter (512MB), resource detection, batching, log transforms
- **Exporters**: 
  - Prometheus metrics (port 8889)
  - Tempo traces (OTLP to tempo:4317)
  - Elasticsearch logs (with data streams)
- **Extensions**: Health check (13133), pprof profiling (1888)
- **Telemetry**: Self-monitoring on port 8888

### **Prometheus Alerting Setup**
Current alert rules monitor:

**Application Alerts:**
- High CPU usage (>80% for 5min)
- High memory usage (>90% for 5min) 
- Application downtime (>1min)
- High error rate (>10% for 2min)
- JVM heap usage (>85% for 5min)

**Infrastructure Alerts:**
- Low disk space (>85% usage)
- Elasticsearch cluster health (red status)

To add custom alerts, edit `config/prometheus/rules/alerts.yml`

### **AlertManager Configuration**
**Current Setup (Development):**
- Basic routing with null receiver (no notifications)
- 10s group wait, 1h repeat interval

**Production Enhancement Needed:**
```yaml
# Add to config/alertmanager/alertmanager.yml
receivers:
  - name: 'slack-alerts'
    slack_configs:
    - api_url: 'YOUR_SLACK_WEBHOOK_URL'
      channel: '#alerts'
      title: 'Alert: {{ .GroupLabels.alertname }}'
      text: '{{ range .Alerts }}{{ .Annotations.summary }}{{ end }}'

  - name: 'email-alerts'
    email_configs:
    - to: 'admin@company.com'
      from: 'alertmanager@company.com'
      subject: 'Alert: {{ .GroupLabels.alertname }}'
      body: '{{ range .Alerts }}{{ .Annotations.description }}{{ end }}'

route:
  group_by: ['alertname', 'cluster', 'service']
  routes:
  - match:
      severity: critical
    receiver: 'email-alerts'
  - match:
      severity: warning  
    receiver: 'slack-alerts'
```

### **Tempo Trace Storage**
- **Backend**: Local storage in `/var/tempo/`
- **Retention**: 1h compaction window, 10min compacted block retention
- **Performance**: 5min max block duration, 1MB max block size
- **Ports**: 3200 (HTTP), 4317 (OTLP gRPC), 4318 (OTLP HTTP)

For production, consider object storage backends (S3, GCS, Azure)

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

  ## 🐛 Troubleshooting Guide

### **Common Issues & Solutions**

#### **1. Prometheus Target Down (`host.docker.internal:8081`)**
**Symptoms**: Target shows as "Down" in Prometheus UI
**Causes & Solutions**:
- App running in container: Change target to `collections-spring:8081` and ensure container is on `observability` network
- Actuator not enabled: Verify `management.endpoints.web.exposure.include=prometheus` in application.properties
- Port mismatch: Confirm app uses `server.port=8081`

#### **2. OTLP Data Not Reaching Tempo**
**Symptoms**: No traces visible in Grafana/Tempo
**Solutions**:
- Verify collector endpoint: `otel.exporter.otlp.endpoint=http://otel-collector:4318` (if app in container) or `http://localhost:4318` (if app on host)
- Check collector logs: `docker compose logs otel-collector`
- Verify collector health: `curl http://localhost:13133`
- Check memory limits: Collector has 512MB limit configured

#### **3. Logs Not Appearing in Kibana**
**Symptoms**: No log indices visible in Kibana
**Solutions**:
- Check Logstash status: `docker compose logs logstash`
- Verify Elasticsearch health: `curl http://localhost:9200/_cluster/health`
- Test log ingestion: Send test log to `http://localhost:8080` (Logstash HTTP input)
- Check data streams: `curl http://localhost:9200/_data_stream`

#### **4. Tempo Permission Errors** ⚠️
**Symptoms**: 
```
permission denied: /var/tempo/wal/00000000
failed to replay wal
```
**Cause**: Volume ownership mismatch (Tempo runs as uid 10001)
**Automated Fix**: Already implemented in docker-compose.yml via entrypoint
**Manual Fix** (if needed):
```bash
docker run --rm -v observability-stack_tempo_data:/data alpine sh -c "chown -R 10001:10001 /data"
docker compose restart tempo
```

#### **5. Grafana Dashboards Not Loading**
**Symptoms**: Empty dashboard list or import errors
**Solutions**:
- Check provisioning: `docker compose logs grafana | grep -i provision`
- Verify datasource connectivity: Grafana UI → Configuration → Data Sources
- Dashboard files location: Ensure `config/grafana/dashboards/*.json` exists
- Restart Grafana: `docker compose restart grafana`

#### **6. AlertManager Not Receiving Alerts**
**Symptoms**: No alerts in AlertManager UI
**Solutions**:
- Check Prometheus alert rules: `curl http://localhost:9090/api/v1/rules`
- Verify AlertManager config: `curl http://localhost:9093/api/v1/status`
- Test alert rule: Trigger condition manually (e.g., stop Java app for 2+ minutes)
- Check AlertManager targets in Prometheus: Status → Runtime & Build Information

#### **7. High Resource Usage**
**Symptoms**: High CPU/Memory usage from stack components
**Solutions**:
- **Elasticsearch**: Increase `ES_JAVA_OPTS=-Xms2g -Xmx2g` in docker-compose.yml
- **Collector**: Adjust `memory_limiter` limit in otel-collector-config.yml
- **Prometheus**: Reduce retention or scrape frequency
- **Logstash**: Increase `LS_JAVA_OPTS=-Xmx1g -Xms1g`

### **Health Check URLs**
- **Collector Health**: http://localhost:13133
- **Prometheus**: http://localhost:9090/-/healthy
- **Elasticsearch**: http://localhost:9200/_cluster/health  
- **Tempo Status**: http://localhost:3200/status
- **Grafana Health**: http://localhost:3000/api/health
- **AlertManager**: http://localhost:9093/api/v1/status

## 🔒 Security & Production Readiness

### **Current Security Status** ⚠️
The stack is configured for **development use** with minimal security:
- No authentication on most services
- HTTP-only communication (no TLS)
- Default passwords
- Debug outputs enabled

### **Production Security Checklist**

#### **1. Enable Authentication**
```yaml
# Grafana (add to docker-compose.yml environment)
- GF_SECURITY_ADMIN_USER=admin
- GF_SECURITY_ADMIN_PASSWORD=secure_password_here
- GF_USERS_ALLOW_SIGN_UP=false
- GF_AUTH_ANONYMOUS_ENABLED=false

# Elasticsearch (add to docker-compose.yml)
- xpack.security.enabled=true
- ELASTIC_PASSWORD=secure_password_here

# Kibana (add authentication config)
- ELASTICSEARCH_USERNAME=kibana_user
- ELASTICSEARCH_PASSWORD=secure_password_here
```

#### **2. Enable TLS/HTTPS**
- Configure reverse proxy (nginx/traefik) with TLS termination
- Use Let's Encrypt or internal CA certificates
- Update all inter-service URLs to use https://

#### **3. Network Security**
```yaml
# Restrict external access in docker-compose.yml
networks:
  observability:
    driver: bridge
    internal: true  # Prevents external access

# Only expose required ports externally
ports:
  - "127.0.0.1:3000:3000"  # Grafana only on localhost
```

#### **4. Resource Limits & Security**
```yaml
# Add resource limits to all services
deploy:
  resources:
    limits:
      memory: 2G
      cpus: '1.0'
```

### **Production Configuration Updates**

#### **1. Data Retention Policies**
```yaml
# Prometheus (extend retention)
command:
  - '--storage.tsdb.retention.time=90d'
  - '--storage.tsdb.retention.size=100GB'

# Elasticsearch ILM
# Configure in Kibana: Stack Management → Index Lifecycle Management
```

#### **2. External Storage Integration**
- **Prometheus**: Configure remote_write to long-term storage
- **Tempo**: Use S3/GCS backend instead of local storage
- **Elasticsearch**: Use external managed service (AWS OpenSearch, Elastic Cloud)

#### **3. High Availability Setup**
- Run multiple replicas of stateless services
- Use external load balancer
- Implement service discovery (Consul, etcd)
- Configure Prometheus federation

#### **4. Monitoring the Monitoring Stack**
- Enable self-monitoring for all components
- Set up external health checks
- Monitor resource usage with dedicated dashboards
- Configure backup procedures for configurations and data

## ⚡ Performance Tuning & Operations

### **Resource Requirements**
**Minimum System Requirements:**
- RAM: 8GB (16GB recommended)
- CPU: 4 cores (8 cores recommended) 
- Disk: 50GB free space (SSD recommended)
- Network: 1Gbps for high-throughput environments

### **Component-Specific Tuning**

#### **OpenTelemetry Collector**
```yaml
# Increase throughput (edit config/otel-collector/otel-collector-config.yml)
processors:
  batch:
    timeout: 1s
    send_batch_size: 2048  # Increase from 1024
  
  memory_limiter:
    limit_mib: 1024  # Increase from 512MB

# For high-volume environments
exporters:
  otlp/tempo:
    endpoint: http://tempo:4317
    sending_queue:
      queue_size: 5000
      num_consumers: 10
```

#### **Prometheus Optimization**
```yaml
# Reduce cardinality and improve performance
scrape_configs:
  - job_name: 'java-apps'
    scrape_interval: 30s  # Increase from 10s for lower load
    metric_relabel_configs:
      # Drop high-cardinality metrics
      - source_labels: [__name__]
        regex: 'http_request_duration_seconds_bucket'
        action: drop
```

#### **Elasticsearch Performance**
```yaml
# Increase heap size (in docker-compose.yml)
environment:
  - "ES_JAVA_OPTS=-Xms4g -Xmx4g"  # Use 50% of available RAM
  - bootstrap.memory_lock=true
  - thread_pool.write.queue_size=1000
```

### **Backup & Disaster Recovery**

#### **Automated Backup Strategy**
```bash
# Create backup script (backup-stack.sh)
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="./backups/$DATE"

# Backup configurations
mkdir -p "$BACKUP_DIR/config"
cp -r config/* "$BACKUP_DIR/config/"

# Backup volumes
docker run --rm -v observability-stack_prometheus_data:/data \
  -v "$(pwd)/$BACKUP_DIR":/backup alpine \
  tar czf /backup/prometheus_data.tar.gz -C /data .

docker run --rm -v observability-stack_elasticsearch_data:/data \
  -v "$(pwd)/$BACKUP_DIR":/backup alpine \
  tar czf /backup/elasticsearch_data.tar.gz -C /data .

# Schedule with cron: 0 2 * * * /path/to/backup-stack.sh
```

#### **Configuration Versioning**
- Store all config files in Git repository
- Use Infrastructure as Code (Terraform, Ansible)
- Implement CI/CD for configuration changes
- Document all customizations

### **Monitoring Best Practices**

#### **SLI/SLO Definition**
Define Service Level Indicators and Objectives:
```yaml
# Example SLOs for collections-spring app
availability_slo: 99.9%  # < 43 minutes downtime/month
latency_slo: 95% of requests < 500ms
error_rate_slo: < 0.1% error rate
```

#### **Alert Tuning Guidelines**
- **Avoid alert fatigue**: Set appropriate thresholds
- **Runbook references**: Include troubleshooting steps in alert annotations
- **Escalation paths**: Define who gets alerted when
- **Silence management**: Use for planned maintenance

#### **Dashboard Organization**
- **Executive**: High-level KPIs and SLOs
- **Operational**: Service health and performance
- **Debug**: Detailed metrics for troubleshooting
- **Infrastructure**: Resource utilization and capacity

## 🔧 Useful Commands & Operations

### **Stack Management**
```bash
# Start/stop stack
docker compose up -d
docker compose down

# Restart specific service
docker compose restart prometheus

# View logs
docker compose logs -f grafana
docker compose logs --tail=100 tempo

# Check service status
docker compose ps
```

### **Health Checks & Validation**
```bash
# Run comprehensive smoke test
./stack-smoke-test.sh

# Use management helpers
./manage-stack.sh status
./manage-stack.sh smoke-test
./manage-stack.sh logs grafana

# Manual health checks
curl http://localhost:13133          # Collector health
curl http://localhost:9090/-/healthy # Prometheus
curl http://localhost:3200/status    # Tempo
curl http://localhost:9200/_cluster/health # Elasticsearch
```

### **Data Queries & Debugging**
```bash
# Query Prometheus metrics
curl "http://localhost:9090/api/v1/query?query=up"

# Check Elasticsearch indices
curl http://localhost:9200/_cat/indices?v

# View Tempo traces
curl http://localhost:3200/api/search?q=service.name=collections-spring

# Test OTLP endpoint
curl -X POST http://localhost:4318/v1/traces \
  -H "Content-Type: application/json" \
  -d '{"resourceSpans":[]}'
```

### **Performance Analysis**
```bash
# Monitor resource usage
docker stats

# Check volume sizes
docker system df -v

# Analyze collector performance
curl http://localhost:8888/metrics | grep otelcol
```

---

## 📚 Additional Resources & Next Steps

### **Documentation Hierarchy**
1. **README.md** (this file) - Complete stack overview and operations guide
2. **java-instrumentation-guide.md** - Detailed Java/Spring Boot integration
3. **ACCESS-GUIDE.md** - Service access, authentication, and URLs
4. **config/** - All service configuration files with inline comments

### **Recommended Learning Path**
1. **Start Here**: Follow the 5-minute quick start
2. **Integrate Your App**: Use the collections-spring example as reference
3. **Customize Dashboards**: Modify existing dashboards in config/grafana/dashboards/
4. **Set Up Alerting**: Configure AlertManager with your notification channels
5. **Production Prep**: Follow security and performance guidelines above

### **Community & Support**
- **Issues**: Check troubleshooting section and health check URLs
- **Enhancements**: All configurations are in Git - submit PRs for improvements
- **Production Deployments**: Consider managed services for critical components

### **Future Enhancements** (Not Yet Implemented)
- [ ] Service mesh integration (Istio/Linkerd)
- [ ] Multi-cluster federation
- [ ] Advanced anomaly detection
- [ ] Capacity planning automation
- [ ] Cost optimization dashboards

For questions, issues, or contributions, refer to the configuration files in the `config/` directory or check the troubleshooting section above.

  ---

## 📚 Additional Resources

- **Java Integration Guide**: `java-instrumentation-guide.md` - Detailed Java/Spring Boot setup
- **Access Guide**: `ACCESS-GUIDE.md` - Service access and authentication details
- **Configuration Files**: `config/` directory contains all service configurations

For questions or enhancements, check the individual config files in the `config/` directory or refer to the troubleshooting section above.
