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
  - CPU, memory, disk, network utilization
  - System load, filesystem statistics
  - Hardware monitoring capabilities

- **cAdvisor**: Container metrics collection
  - Container resource usage (CPU, memory, network, disk)
  - Container lifecycle events
  - Docker and Kubernetes integration

### 🔍 **Tracing Stack**
- **Tempo**: Distributed tracing storage and query engine
  - Stores trace data with configurable retention
  - Provides trace search and retrieval APIs
  - Integrates with Grafana for trace visualization
  - Supports OpenTelemetry trace format

### 📋 **Logging Stack (ELK)**
- **Logstash**: Log processing and enrichment pipeline
  - Parses structured and unstructured logs
  - Enriches logs with additional metadata
  - Transforms and normalizes log formats
  - Routes logs to Elasticsearch

- **Elasticsearch**: Log storage and search engine
  - Indexes logs for fast search and retrieval
  - Provides full-text search capabilities
  - Supports complex queries and aggregations
  - Manages data lifecycle and retention policies

- **Kibana**: Log exploration and visualization
  - Advanced log search and filtering
  - Log pattern analysis and discovery
  - Custom dashboards and visualizations
  - Index pattern management

### 📊 **Visualization & Alerting**
- **Grafana**: Unified observability dashboard
  - Visualizes metrics, traces, and logs in one interface
  - Correlates data across different sources
  - Provides alerting capabilities
  - Supports custom dashboard creation

- **AlertManager**: Alert routing and notification
  - Receives alerts from Prometheus
  - Groups and routes alerts based on rules
  - Manages alert silencing and inhibition
  - Integrates with notification channels (email, Slack, webhooks)

## 🔄 Component Interactions

### 📡 **Data Flow**
1. **Collection**: Applications send telemetry to OpenTelemetry Collector
2. **Processing**: Collector processes and routes data to appropriate backends
3. **Storage**: Data is stored in specialized databases (Prometheus, Tempo, Elasticsearch)
4. **Visualization**: Grafana and Kibana provide interfaces to query and visualize data
5. **Alerting**: Prometheus evaluates rules and sends alerts to AlertManager

### 🔗 **Integration Points**
- **Metrics**: Apps → OTel Collector → Prometheus → Grafana
- **Traces**: Apps → OTel Collector → Tempo → Grafana
- **Logs**: Apps → Logstash → Elasticsearch → Kibana/Grafana
- **Alerts**: Prometheus → AlertManager → Notification Channels

### 🌐 **Network Communication**
- All services communicate within the `observability` Docker network
- External access through exposed ports on localhost
- Internal service discovery using container names

## ⚙️ Configuration Requirements

### 🔧 **Core Configuration Files**

| Component | Configuration File | Purpose |
|-----------|-------------------|---------|
| **Prometheus** | `config/prometheus/prometheus.yml` | Scrape targets, alerting rules |
| **OpenTelemetry** | `config/otel-collector/otel-collector-config.yml` | Receivers, processors, exporters |
| **Tempo** | `config/tempo/tempo.yml` | Storage, retention, query settings |
| **Logstash** | `config/logstash/pipeline/logstash.conf` | Log parsing and routing |
| **Grafana** | `config/grafana/provisioning/` | Data sources, dashboards |
| **AlertManager** | `config/alertmanager/alertmanager.yml` | Alert routing, notifications |

### 🔐 **Security Configuration**
- **Authentication**: Grafana (admin/admin123), others disabled for development
- **Network Security**: Services isolated in Docker network
- **TLS**: Disabled for development, enable for production

### 💾 **Storage Configuration**
- **Data Persistence**: Docker volumes for all services
- **Retention Policies**: 
  - Prometheus: 15 days
  - Elasticsearch: Configurable via ILM
  - Tempo: Configurable in tempo.yml

## 🔌 Java Application Integration

### 📦 **Dependencies**
Add to your `pom.xml`:

```xml
<dependencies>
    <!-- Spring Boot Actuator for metrics -->
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
