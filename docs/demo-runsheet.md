# TradeFlow demo runsheet

## 15-minute demo plan

| Time | Segment | Narrator | Notes |
| --- | --- | --- | --- |
| 0:00-1:00 | Title and problem statement | Team Lead | Introduce TradeFlow and the reconciliation problem. |
| 1:00-3:00 | Architecture walkthrough | Engineer | Reference the architecture diagram in docs/architecture.md. |
| 3:00-8:00 | Live demo: submit trade | Engineer | Post a trade through the UI and confirm the flow. |
| 8:00-11:00 | Observability checks | Analyst | Open Grafana and confirm the dashboards update. |
| 11:00-14:00 | Resolve and summarize | Team Lead | Explain the reconciliation outcome and next steps. |
| 14:00-15:00 | Q&A | Team Lead | Handle questions and close. |

## Backup plan

| Failure | Backup action |
| --- | --- |
| Kafka is unhealthy | Switch to the recorded fallback demo and explain the expected event flow. |
| GHCR image pull fails | Use the locally built images from the current machine. |
| Grafana is unavailable | Use Prometheus targets and the backend logs as the fallback evidence. |
| Postgres is unavailable | Restart the compose stack and verify the database health check. |
