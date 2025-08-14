# Observability Stack Setup Guide

This directory contains a complete observability stack using Docker Compose with the following components:

## 🏗️ Architecture Overview

```
┌─────────────────────────────────┐
│         Applications            │
│ (Java Apps with OpenTelemetry) │
└──────────────┬──────────────────┘
               │
               ▼
    ┌─────────────────────┐
    │ OpenTelemetry       │
    │ Collector           │
    └───────┬─────────────┘
            │
   ┌────────┼────────┐
   │        │        │
   ▼        ▼        ▼
┌─────┐ ┌─────┐ ┌─────────┐
│Prom │ │Tempo│ │Logstash │
│etheus│ │     │ │   +     │
│     │ │     │ │  ELK    │
└──┬──┘ └──┬──┘ └─────┬───┘
   │       │          │
   └───────┼──────────┘
           ▼
    ┌─────────────┐
    │   Grafana   │
    │ (Unified    │
    │ Dashboard)  │
    └─────────────┘
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
docker-compose ps

# View logs
docker-compose logs -f grafana
```

### 2. Access the Services

| Service | URL | Default Credentials |
|---------|-----|-------------------|
| **Grafana** | http://localhost:3000 | admin / admin123 |
| **Prometheus** | http://localhost:9090 | - |
| **Kibana** | http://localhost:5601 | - |
| **Elasticsearch** | http://localhost:9200 | - |
| **Tempo** | http://localhost:3200 | - |
| **AlertManager** | http://localhost:9093 | - |

### 3. Monitor Java Applications

To monitor your Java applications, add the following dependencies to your `pom.xml`:

```xml
<!-- Spring Boot Actuator for metrics -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer Prometheus -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<!-- OpenTelemetry -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
</dependency>
```

Add to your `application.properties`:

```properties
# Actuator endpoints
management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true

# OpenTelemetry configuration
otel.service.name=your-service-name
otel.exporter.otlp.endpoint=http://localhost:4317
otel.exporter.otlp.protocol=grpc
otel.traces.exporter=otlp
otel.metrics.exporter=otlp
otel.logs.exporter=otlp
```

## 📊 Features

### Metrics (Prometheus + Grafana)
- Application metrics (HTTP requests, JVM, custom metrics)
- Infrastructure metrics (CPU, memory, disk)
- Container metrics (Docker stats)
- Custom alerting rules

### Logs (ELK Stack)
- Centralized log collection
- Structured log parsing
- Log correlation with traces
- Advanced search and filtering

### Traces (Tempo)
- Distributed tracing
- Request flow visualization
- Performance bottleneck identification
- Trace-to-logs correlation

### Unified Visualization (Grafana)
- Pre-built dashboards
- Metrics, logs, and traces correlation
- Custom dashboard creation
- Alert management

## 🔧 Configuration

### Adding New Java Applications

1. Update `config/prometheus/prometheus.yml`:
```yaml
scrape_configs:
  - job_name: 'your-app'
    static_configs:
      - targets: ['your-app:8080']
    metrics_path: '/actuator/prometheus'
```

2. Add your application to the same Docker network:
```yaml
networks:
  - observability_observability
```

### Custom Alerts

Edit `config/prometheus/rules/alerts.yml` to add custom alerting rules.

### Log Parsing

Modify `config/logstash/pipeline/logstash.conf` to add custom log parsing patterns.

## 🛠️ Management Commands

### Start/Stop Services
```powershell
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# Restart specific service
docker-compose restart grafana

# View service logs
docker-compose logs -f prometheus
```

### Data Management
```powershell
# Clean up all data (WARNING: This will delete all metrics, logs, and traces)
docker-compose down -v

# Backup data
docker run --rm -v observability-stack_prometheus_data:/data -v ${PWD}/backup:/backup alpine tar czf /backup/prometheus_backup.tar.gz -C /data .
```

### Troubleshooting
```powershell
# Check service health
docker-compose ps
docker-compose logs service-name

# Check network connectivity
docker network ls
docker network inspect observability-stack_observability
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

1. **Instrument your Java applications** with OpenTelemetry
2. **Create custom dashboards** in Grafana
3. **Set up alerting** via email/Slack
4. **Implement log correlation** with trace IDs
5. **Scale components** based on your load

For more detailed configuration, check the individual config files in the `config/` directory.
