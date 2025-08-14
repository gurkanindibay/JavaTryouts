# 🔍 Observability Stack Access Guide

## 📊 Service Access Points

| Service | URL | Purpose | Credentials |
|---------|-----|---------|-------------|
| **Grafana** | http://localhost:3000 | **Main Dashboard** - View all metrics, logs, traces | admin / admin123 |
| **Prometheus** | http://localhost:9090 | Metrics query interface | - |
| **Kibana** | http://localhost:5601 | Log exploration and analysis | - |
| **Elasticsearch** | http://localhost:9200 | Raw log data API | - |
| **Tempo** | http://localhost:3200/status | Tracing backend (API only) | - |
| **AlertManager** | http://localhost:9093 | Alert management | - |

## 🎯 **How to Access Traces (Tempo Data)**

**❌ WRONG**: Going to http://localhost:3200 
- Tempo doesn't serve a web UI on the root path
- You'll get a 404 error

**✅ CORRECT**: Access traces via Grafana
1. Open http://localhost:3000
2. Login: `admin` / `admin123`
3. Go to **Explore** (compass icon in left sidebar)
4. Select **"Tempo"** from the data source dropdown
5. Query traces using trace IDs or service names

## 🔧 **Tempo API Endpoints** (for debugging)

```bash
# Check if Tempo is running
curl http://localhost:3200/status

# Check service status
curl http://localhost:3200/status/services

# Check readiness
curl http://localhost:3200/ready

# Get metrics
curl http://localhost:3200/metrics
```

## 📈 **Getting Started with Your Observability Stack**

### 1. **View System Metrics** (Prometheus + Grafana)
- Go to Grafana → Dashboards
- Check out the "Java Application Metrics" dashboard
- Explore system metrics from Node Exporter and cAdvisor

### 2. **Explore Logs** (ELK Stack)
- Open Kibana: http://localhost:5601
- Create index patterns for `logs-*`
- Search and filter your application logs

### 3. **Query Metrics** (Prometheus)
- Open Prometheus: http://localhost:9090
- Try queries like:
  - `up` (service availability)
  - `node_cpu_seconds_total` (CPU usage)
  - `container_memory_usage_bytes` (container memory)

### 4. **Send Sample Data**
To test the stack with your Java applications:
1. Add OpenTelemetry dependencies to your Java projects
2. Configure them to send data to: `http://localhost:4317` (gRPC) or `http://localhost:4318` (HTTP)
3. View the data in Grafana

## 🚨 **Troubleshooting**

### Tempo Shows 404
- **Normal behavior** - Tempo is API-only
- Access traces via Grafana instead

### Service Not Responding
```bash
# Check container status
docker-compose ps

# Check logs
docker-compose logs <service-name>

# Restart specific service
docker-compose restart <service-name>
```

### Want to Send Test Data?
```bash
# Send a test trace (requires curl and valid trace data)
curl -X POST http://localhost:4318/v1/traces \
  -H "Content-Type: application/json" \
  -d '{"resourceSpans":[...]}'

# Or configure your Java app to send to:
# OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
```

## 🎉 **Your Stack is Working!**

All services are running correctly:
- ✅ Grafana (web UI for everything)
- ✅ Prometheus (metrics storage)
- ✅ Tempo (trace storage)
- ✅ ELK Stack (log storage)
- ✅ OpenTelemetry Collector (data ingestion)

**Next Step**: Start instrumenting your Java applications to send telemetry data to the collector!
