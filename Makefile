.PHONY: infra infra-down infra-clean infra-logs infra-status obs obs-down obs-status obs-logs core ingestion dev help

INFRA_SERVICES = mysql redis elasticsearch
OBS_SERVICES = otel-collector prometheus loki tempo grafana

# ── Infra ────────────────────────────────────────────────────────────
infra:
	docker compose up -d $(INFRA_SERVICES)
	@echo "⏳ Waiting for MySQL..."
	@until docker exec chat_mysql mysqladmin ping -h localhost -uroot -proot --silent 2>/dev/null; \
		do printf '.'; sleep 2; done
	@echo "✅ Infra is up"

infra-down:
	docker compose down $(INFRA_SERVICES)

infra-clean:
	docker compose down $(INFRA_SERVICES) -v

infra-logs:
	docker compose logs -f $(INFRA_SERVICES)

infra-status:
	docker compose ps $(INFRA_SERVICES)

# ── Observability (LGTM + OTel) ─────────────────────────────────────
obs:
	docker compose up -d $(OBS_SERVICES)
	@echo "✅ Observability stack is up — Grafana at http://localhost:3000"

obs-down:
	docker compose stop $(OBS_SERVICES)

obs-status:
	docker compose ps $(OBS_SERVICES)

obs-logs:
	docker compose logs -f $(OBS_SERVICES)

# ── Services ─────────────────────────────────────────────────────────
core:
	cd services/core && set -a && . ./.env && set +a && ./gradlew bootRun

ingestion:
	cd services/ingestion && set -a && . ./.env && set +a && go run ./cmd/api

# ── Development ─────────────────────────────────────────────────────

dev: infra
	@echo "🚀 Starting services..."
	@trap 'kill 0' SIGINT; \
	(cd services/core && set -a && . ./.env && set +a && ./gradlew bootRun) & \
	(cd services/core && set -a && . ./.env && set +a && ./gradlew -t compileJava) & \
	(cd services/ingestion && set -a && . ./.env && set +a && air) & \
	wait

# ── Help ─────────────────────────────────────────────────────────────
help:
	@echo ""
	@echo "  make infra              Start MySQL, Redis, Elasticsearch"
	@echo "  make infra-down         Stop infra (data persists)"
	@echo "  make infra-clean        Stop infra + wipe all volumes"
	@echo "  make infra-logs         Tail infra container logs"
	@echo "  make infra-status       Show container health"
	@echo ""
	@echo "  make obs                Start LGTM observability stack (Grafana :3000)"
	@echo "  make obs-down           Stop observability stack"
	@echo "  make obs-status         Show observability container status"
	@echo "  make obs-logs           Tail observability container logs"
	@echo ""
	@echo "  make core               Run Spring core service locally"
	@echo "  make ingestion          Run Go ingestion service locally"
	@echo "  make dev                Run all services locally"
	@echo ""