# ==== Parametry ====
APP_NAME        ?= user-service
IMAGE_NAME      ?= $(APP_NAME)
TAG             ?= dev
IMAGE           := $(IMAGE_NAME):$(TAG)

PROFILE         ?= dev
PORT            ?= 8812
GRPC_PORT       ?= 9090
SKIP_TESTS      ?= true

COMPOSE_FILE    ?= docker-compose.user.yml
NETWORK_NAME    ?= learnwords-net

# DB
DATASOURCE_URL          ?= jdbc:postgresql://postgres:5432/user_management
DB_POSTGRESQL_USER      ?= admin
DB_POSTGRESQL_PASSWORD  ?= adminpassword

# Kafka + JWKS (opcjonalnie)
KAFKA_BOOTSTRAP_SERVERS ?= kafka:9092
JWKS_URI                ?= http://api-gateway:8811/.well-known/jwks.json

# ==== Zadania ====

.PHONY: help
help:
	@echo "make package          - mvn clean package (SKIP_TESTS=$(SKIP_TESTS))"
	@echo "make docker-build     - build obrazu: $(IMAGE)"
	@echo "make rebuild          - usuń stary kontener/obraz i zbuduj od nowa"
	@echo "make compose-up       - uruchom usługę przez docker compose (na sieci $(NETWORK_NAME))"
	@echo "make compose-down     - zatrzymaj usługę"
	@echo "make logs             - podgląd logów"
	@echo "make clean            - mvn clean"

.PHONY: package
package:
	./mvnw clean package -DskipTests=$(SKIP_TESTS)

.PHONY: docker-rm-image
docker-rm-image:
	- docker rm -f $(APP_NAME) 2>/dev/null || true
	- docker rmi -f $(IMAGE) 2>/dev/null || true

.PHONY: docker-build
docker-build: package
	docker build --pull --no-cache -t $(IMAGE) .

.PHONY: rebuild
rebuild: docker-rm-image docker-build

.PHONY: network
network:
	- docker network create $(NETWORK_NAME) 2>/dev/null || true

.PHONY: compose-up
compose-up: network
	IMAGE_NAME=$(IMAGE_NAME) IMAGE_TAG=$(TAG) PROFILE=$(PROFILE) \
	PORT=$(PORT) GRPC_PORT=$(GRPC_PORT) \
	DATASOURCE_URL=$(DATASOURCE_URL) DB_POSTGRESQL_USER=$(DB_POSTGRESQL_USER) DB_POSTGRESQL_PASSWORD=$(DB_POSTGRESQL_PASSWORD) \
	KAFKA_BOOTSTRAP_SERVERS=$(KAFKA_BOOTSTRAP_SERVERS) JWKS_URI=$(JWKS_URI) \
	docker compose -f $(COMPOSE_FILE) up -d

.PHONY: compose-down
compose-down:
	docker compose -f $(COMPOSE_FILE) down

.PHONY: logs
logs:
	docker compose -f $(COMPOSE_FILE) logs -f $(APP_NAME)

.PHONY: clean
clean:
	./mvnw clean
