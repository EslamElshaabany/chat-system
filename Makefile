.PHONY: infra infra-down infra-clean infra-logs infra-status core ingestion dev help

INFRA_SERVICES = mysql redis elasticsearch

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

# ── Services ─────────────────────────────────────────────────────────
core:
	cd services/core && . ./.env && ./gradlew bootRun

ingestion:
	cd services/ingestion && . ./.env && go run ./cmd/api/main.go

# ── Development ─────────────────────────────────────────────────────

dev: infra
	@echo "🚀 Starting services..."
	@trap 'kill 0' SIGINT; \
	(cd services/core && . ./.env && ./gradlew bootRun) & \
	(cd services/core && . ./.env && ./gradlew -t compileJava) & \
	(cd services/ingestion && . ./.env && air) & \
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
	@echo "  make core               Run Spring core service locally"
	@echo "  make ingestion          Run Go ingestion service locally"
	@echo "  make dev                Run all services locally"
	@echo ""