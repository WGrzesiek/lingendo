NETWORK_NAME ?= learnwords-net

.PHONY: net
net:
	- docker network create $(NETWORK_NAME) 2>/dev/null || true

.PHONY: infra-kafka-up
infra-kafka-up: net
	docker compose -f docker-compose.kafka.yml up -d

.PHONY: infra-db-up
infra-db-up: net
	docker compose -f docker-compose.db.yml up -d

.PHONY: infra-connect-up
infra-connect-up: net
	docker compose -f docker-compose.connect.yml up -d

.PHONY: infra-up
infra-up: infra-kafka-up infra-db-up infra-connect-up

.PHONY: infra-down
infra-down:
	-docker compose -f docker-compose.connect.yml down
	-docker compose -f docker-compose.kafka.yml down
	-docker compose -f docker-compose.db.yml down

.PHONY: infra-logs
infra-logs:
	docker compose -f docker-compose.kafka.yml logs -f
