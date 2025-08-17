#!/usr/bin/env bash
set -euo pipefail

# manage-stack.sh - simple helper for managing the observability docker-compose stack
# Usage: ./manage-stack.sh <command> [args]

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"

usage() {
  cat <<EOF
Usage: $(basename "$0") <command>

Commands:
  up             Start the stack (docker compose up -d)
  down           Stop the stack (docker compose down)
  restart        Restart the stack (down then up)
  status         Show docker compose ps for the stack
  logs [svc]     Tail logs for the whole stack or a specific service
  smoke-test     Run the bundled stack smoke test (./stack-smoke-test.sh)
  help           Show this help

Examples:
  $(basename "$0") up
  $(basename "$0") logs logstash
  $(basename "$0") smoke-test
EOF
}

require_compose() {
  if ! command -v docker >/dev/null 2>&1; then
    echo "docker is not installed or not in PATH" >&2
    exit 1
  fi
}

do_up() {
  require_compose
  echo "Bringing stack up (detached) using $COMPOSE_FILE..."
  docker compose -f "$COMPOSE_FILE" up -d --remove-orphans
}

do_down() {
  require_compose
  echo "Bringing stack down using $COMPOSE_FILE..."
  docker compose -f "$COMPOSE_FILE" down --remove-orphans
}

do_restart() {
  do_down
  do_up
}

do_status() {
  require_compose
  echo "Status for stack (compose file: $COMPOSE_FILE):"
  docker compose -f "$COMPOSE_FILE" ps
}

do_logs() {
  require_compose
  local svc=${1:-}
  if [ -n "$svc" ]; then
    docker compose -f "$COMPOSE_FILE" logs -f --tail=200 "$svc"
  else
    docker compose -f "$COMPOSE_FILE" logs -f --tail=200
  fi
}

do_smoke() {
  if [ ! -x "$SCRIPT_DIR/stack-smoke-test.sh" ]; then
    echo "Smoke test script not found or not executable at $SCRIPT_DIR/stack-smoke-test.sh" >&2
    echo "Make it executable with: chmod +x $SCRIPT_DIR/stack-smoke-test.sh" >&2
    exit 1
  fi
  echo "Running smoke test (may bring the stack down/up)..."
  (cd "$SCRIPT_DIR" && ./stack-smoke-test.sh)
}

cmd=${1:-help}
shift || true

case "$cmd" in
  up)
    do_up "$@" ;;
  down)
    do_down "$@" ;;
  restart)
    do_restart "$@" ;;
  status)
    do_status "$@" ;;
  logs)
    do_logs "$@" ;;
  smoke-test|smoke)
    do_smoke "$@" ;;
  help|--help|-h)
    usage ;;
  *)
    echo "Unknown command: $cmd" >&2
    usage
    exit 2 ;;
esac
