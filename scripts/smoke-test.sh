#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"
TMP_DIR="$ROOT_DIR/.tmp"
mkdir -p "$TMP_DIR"

if ! command -v curl >/dev/null 2>&1; then
  echo "curl is required for the smoke test" >&2
  exit 1
fi

BASE_URL="${BASE_URL:-http://localhost:8080}"
TRADE_REF="${TRADE_REF:-TR-DEMO-$(date +%s)}"
TRADE_PAYLOAD="{\"tradeRef\":\"$TRADE_REF\",\"instrumentId\":1,\"counterpartyId\":1,\"quantity\":10,\"price\":123.45,\"tradeDate\":\"2026-08-04\"}"
AUTH_USER="${AUTH_USER:-trader}"
AUTH_PASSWORD="${AUTH_PASSWORD:-trader}"

echo "Checking backend health..."
curl --retry 10 --retry-delay 2 --retry-connrefused -fsS "$BASE_URL/actuator/health" >"$TMP_DIR/tradeflow-health.json"
cat "$TMP_DIR/tradeflow-health.json"

echo "\nPosting a demo trade..."
curl --retry 5 --retry-delay 2 --retry-connrefused -fsS -X POST "$BASE_URL/api/v1/trades" \
  -H 'Content-Type: application/json' \
  -u "$AUTH_USER:$AUTH_PASSWORD" \
  --data "$TRADE_PAYLOAD" >"$TMP_DIR/tradeflow-trade.json"
cat "$TMP_DIR/tradeflow-trade.json"

echo "\nSmoke test completed."
