#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
COMPOSE_FILE="$ROOT_DIR/docker-compose.yml"

TIMEOUT=90  # seconds per endpoint
INTERVAL=3

failures=0
failed_services=()

echo "Smoke test starting: using compose file $COMPOSE_FILE"

function run_compose() {
  docker compose -f "$COMPOSE_FILE" "$@"
}

function wait_for_http() {
  local label="$1"; shift
  local url="$1"; shift
  local expect="$1"; shift || true
  local timeout=${1:-$TIMEOUT}
  local interval=${2:-$INTERVAL}

  echo -n "Checking $label -> $url ... "
  local elapsed=0
  while [ $elapsed -lt $timeout ]; do
    code=$(curl -sS -o /dev/null -w "%{http_code}" --max-time 5 "$url" || echo "000")
    if [ "$expect" = "ANY" ]; then
      if [ "$code" != "000" ]; then
        echo "OK ($code)"
        return 0
      fi
    else
      if [ "$code" = "$expect" ]; then
        echo "OK ($code)"
        return 0
      fi
    fi
    sleep $interval
    elapsed=$((elapsed+interval))
  done
  echo "FAIL (last code=$code)"
  failures=$((failures+1))
  failed_services+=("$label")
  return 1
}

function check_body() {
  local label="$1"; shift
  local url="$1"; shift
  local timeout=${1:-$TIMEOUT}
  local interval=${2:-$INTERVAL}

  echo -n "Checking $label -> $url (body) ... "
  local elapsed=0
  while [ $elapsed -lt $timeout ]; do
    out=$(curl -sS --max-time 5 "$url" || true)
    if [ -n "$out" ]; then
      echo "OK"
      return 0
    fi
    sleep $interval
    elapsed=$((elapsed+interval))
  done
  echo "FAIL (no body)"
  failures=$((failures+1))
  return 1
}

echo "Bringing stack down..."
run_compose down --remove-orphans || true

echo "Bringing stack up..."
run_compose up -d --remove-orphans

echo "Waiting 6s for containers to settle..."
sleep 6

echo "Container status:"
run_compose ps

echo
echo "Running endpoint checks (timeout per endpoint: ${TIMEOUT}s)"

# label | url | expected_http_code (or ANY)
checks=(
  "Prometheus|http://localhost:9090/-/ready|200"
  "Grafana|http://localhost:3000/api/health|ANY"
  "Elasticsearch|http://localhost:9200/_cluster/health?pretty|ANY"
  "Kibana|http://localhost:5601/api/status|ANY"
  "Logstash|http://localhost:9600/_node?pretty|200"
  "Tempo|http://localhost:3200/status|ANY"
  "OTelCollectorHealth|http://localhost:13133/|200"
  "OTelCollectorMetrics|http://localhost:8888/metrics|200"
  "Alertmanager|http://localhost:9093/-/ready|200"
  "node-exporter|http://localhost:9100/metrics|200"
  "cadvisor|http://localhost:8080/healthz|200"
)

for item in "${checks[@]}"; do
  IFS='|' read -r label url expect <<< "$item"
  wait_for_http "$label" "$url" "$expect" || true
done

echo
if [ $failures -eq 0 ]; then
  echo "SMOKE TEST: PASS - all checks succeeded"
  exit 0
else
  echo "SMOKE TEST: FAIL - $failures checks failed"
  echo "Failed services:"
  for s in "${failed_services[@]}"; do
    echo " - $s"
  done
  exit 2
fi
