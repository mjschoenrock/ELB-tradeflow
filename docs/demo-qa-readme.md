# TradeFlow Demo & QA Runbook

Use this file as the quick prep guide for the Day 10 demo and QA session.
It summarizes the live links, the important code paths, the startup flow, and
the order to test the app features end to end.

## Live Links

- Frontend trades page: http://localhost:5173/trades
- Frontend new trade form: http://localhost:5173/trades/new
- Recon breaks page: http://localhost:5173/recon
- Backend health: http://localhost:8080/actuator/health
- Backend Prometheus metrics: http://localhost:8080/actuator/prometheus
- Prometheus UI: http://localhost:9090
- Grafana UI: http://localhost:3000
- Kafdrop topic messages: http://localhost:9000/topic/trade-events/allmessages

## Default Login Data

- Admin: `admin` / `admin`
- Trader: `trader` / `trader`
- Viewer: `viewer` / `viewer`

Use `trader` for creating or editing trades, and `admin` for actuator or
metrics checks.

## Project Map

### Backend

- [backend/src/main/java/com/dbtraining/tradeflow/TradeflowApplication.java](../backend/src/main/java/com/dbtraining/tradeflow/TradeflowApplication.java)
  - Spring Boot entry point.
  - Includes the demo runner that is only active in the `dev` profile.
- [backend/src/main/java/com/dbtraining/tradeflow/config/SecurityConfig.java](../backend/src/main/java/com/dbtraining/tradeflow/config/SecurityConfig.java)
  - In-memory users and role rules.
- [backend/src/main/java/com/dbtraining/tradeflow/config/KafkaConfig.java](../backend/src/main/java/com/dbtraining/tradeflow/config/KafkaConfig.java)
  - Kafka consumer factory, DLT setup, and consumer metrics binding.
- [backend/src/main/java/com/dbtraining/tradeflow/kafka/TradeEventConsumer.java](../backend/src/main/java/com/dbtraining/tradeflow/kafka/TradeEventConsumer.java)
  - Consumes trade lifecycle events and logs them.
- [backend/src/main/java/com/dbtraining/tradeflow/kafka/AuditEventConsumer.java](../backend/src/main/java/com/dbtraining/tradeflow/kafka/AuditEventConsumer.java)
  - Audits Kafka events through `AuditService`.
- [backend/src/main/java/com/dbtraining/tradeflow/service/AuditService.java](../backend/src/main/java/com/dbtraining/tradeflow/service/AuditService.java)
  - Minimal audit sink used by the consumer wiring.

### Frontend

- [frontend/src/App.jsx](../frontend/src/App.jsx)
  - Router and app shell.
- [frontend/src/pages/Dashboard.jsx](../frontend/src/pages/Dashboard.jsx)
  - Summary cards and refresh logic.
- [frontend/src/pages/Trades.jsx](../frontend/src/pages/Trades.jsx)
  - Trade list and filters.
- [frontend/src/pages/Recon.jsx](../frontend/src/pages/Recon.jsx)
  - Recon break filters and resolve actions.
- [frontend/src/components/AddTradeForm.jsx](../frontend/src/components/AddTradeForm.jsx)
  - New trade form and validation.
- [frontend/src/services/apiService.js](../frontend/src/services/apiService.js)
  - Central API client and auth headers.

### Platform and Monitoring

- [docker-compose.yml](../docker-compose.yml)
  - Full stack runtime for postgres, kafka, backend, frontend, prometheus,
    grafana, and kafdrop.
- [monitoring/prometheus/prometheus.yml](../monitoring/prometheus/prometheus.yml)
  - Prometheus scrape config.
- [monitoring/grafana/provisioning/datasources/prometheus.yml](../monitoring/grafana/provisioning/datasources/prometheus.yml)
  - Grafana datasource provisioning.
- [monitoring/grafana/provisioning/dashboards/dashboard.yml](../monitoring/grafana/provisioning/dashboards/dashboard.yml)
  - Grafana dashboard provisioning.
- [.github/workflows/ci.yml](../.github/workflows/ci.yml)
  - CI pipeline with build, test, and GHCR publish.

## Start The Stack

Run everything from the project root:

```bash
docker compose down -v
docker compose up -d
docker compose ps
```

If a previous Kafka stack was left behind, `down -v` is the safest reset.

## Quick Commands

Use these during prep or live QA:

```bash
docker compose ps
docker compose logs -f backend
docker compose logs -f kafka
curl -s http://localhost:8080/actuator/health
curl -s -u admin:admin http://localhost:8080/actuator/prometheus | grep -E 'kafka_producer_record_send_total|kafka_consumer'
curl -s -u trader:trader -H 'Content-Type: application/json' -X POST http://localhost:8080/api/v1/trades -d '{"tradeRef":"TRD-2026-9998","instrumentId":1,"counterpartyId":1,"quantity":100,"price":99.5,"tradeDate":"2026-08-03"}'
curl -s http://localhost:9000/topic/trade-events/allmessages | grep -F 'TRD-2026-9998'
docker compose down -v
```

## If Something Fails

- Frontend blank page: open the browser console first, then check `frontend/src/pages/` and `frontend/src/App.jsx`.
- Backend will not start: check the Liquibase changelog under [backend/src/main/resources/db/changelog/](../backend/src/main/resources/db/changelog/).
- Kafka is unhealthy: run `docker compose down -v` and start again to clear stale broker data.
- Trade POST returns 401: use `trader/trader` for write operations.
- Metrics are missing: verify [backend/src/main/java/com/dbtraining/tradeflow/config/KafkaConfig.java](../backend/src/main/java/com/dbtraining/tradeflow/config/KafkaConfig.java) and [backend/src/main/resources/application.yml](../backend/src/main/resources/application.yml).

## Suggested Demo Order

1. Open the frontend and show the `Trades` page.
2. Create a new trade from `New Trade` using the `trader` account.
3. Open Kafdrop and show the `trade-events` topic message.
4. Open the `Recon` page and show the open break workflow.
5. Open Grafana and show the dashboard or datasource.
6. Open Prometheus and confirm the backend target is `up`.

## Feature Checklist

- Trades page loads and lists seeded trades.
- New trade form validates input and creates a trade successfully.
- Kafka receives the trade event and Kafdrop shows the message.
- Recon page loads and supports the open/resolved filters.
- Backend health endpoint returns `UP`.
- Prometheus scrapes the backend.
- Grafana is provisioned and reachable.

## Useful Testing Notes

- Use `trader/trader` for write actions.
- Use `admin/admin` for actuator and metrics endpoints.
- If the frontend shows a blank page, check the browser console and the
  `frontend/src/pages/` components first.
- If Kafka metrics are missing, check [backend/src/main/java/com/dbtraining/tradeflow/config/KafkaConfig.java](../backend/src/main/java/com/dbtraining/tradeflow/config/KafkaConfig.java).
- If the backend fails to start in Docker, check [docker-compose.yml](../docker-compose.yml)
  and the Liquibase changelog under [backend/src/main/resources/db/changelog/](../backend/src/main/resources/db/changelog/).

## Demo Day Commands

```bash
docker compose ps
curl -s http://localhost:8080/actuator/health
curl -s -u admin:admin http://localhost:8080/actuator/prometheus | grep -E 'kafka_producer_record_send_total|kafka_consumer'
```

## Quick Links For Teammates

- Main README: [../README.md](../README.md)
- Day 10 guide: [../student-guides/day10/README.md](../student-guides/day10/README.md)
- Day 10 local CI/CD guide: [../student-guides/day10/day10-local-cicd.md](../student-guides/day10/day10-local-cicd.md)
