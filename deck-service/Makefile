# ==== Parametry ====
APP_NAME        ?= deck-service
IMAGE_NAME      ?= $(APP_NAME)
TAG             ?= dev
IMAGE           := $(IMAGE_NAME):$(TAG)

PROFILE         ?= dev
PORT            ?= 8814
SKIP_TESTS      ?= true

COMPOSE_FILE    ?= docker-compose.deck.yml
NETWORK_NAME    ?= learnwords-net

# DB
DB_POSTGRESQL_HOST ?= postgres
DB_POSTGRESQL_PORT ?= 5432
DB_POSTGRESQL_NAME ?= deck
DB_POSTGRESQL_USER ?= admin
DB_POSTGRESQL_PASSWORD ?= adminpassword

# Kafka
KAFKA_BOOTSTRAP ?= kafka:9092
KAFKA_GROUP     ?= deck-service-group

# gRPC
GRPC_VOCAB_READ_ADDRESS      ?= static://vocabulary-read-service:9099
GRPC_VOCAB_READ_NEGOTIATION  ?= PLAINTEXT

# Logi / JVM
LOG_LEVEL_KAFKA     ?= INFO
LOG_LEVEL_HIBERNATE ?= INFO
LOG_LEVEL_POSTGRESQL?= INFO
JAVA_OPTS           ?=

LOGSTASH_HOST ?= logstash-1

# ==== Zadania ====

.PHONY: help
help:
	@echo "make package          - mvn clean package (SKIP_TESTS=$(SKIP_TESTS))"
	@echo "make docker-build     - build obrazu: $(IMAGE)"
	@echo "make rebuild          - usuń stary kontener/obraz i zbuduj od nowa"
	@echo "make compose-up       - uruchom usługę przez docker compose (sieć $(NETWORK_NAME))"
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
	IMAGE_NAME=$(IMAGE_NAME) IMAGE_TAG=$(TAG) PROFILE=$(PROFILE) PORT=$(PORT) \
	DB_POSTGRESQL_HOST=$(DB_POSTGRESQL_HOST) DB_POSTGRESQL_PORT=$(DB_POSTGRESQL_PORT) \
	DB_POSTGRESQL_NAME=$(DB_POSTGRESQL_NAME) DB_POSTGRESQL_USER=$(DB_POSTGRESQL_USER) DB_POSTGRESQL_PASSWORD=$(DB_POSTGRESQL_PASSWORD) \
	KAFKA_BOOTSTRAP=$(KAFKA_BOOTSTRAP) KAFKA_GROUP=$(KAFKA_GROUP) \
	GRPC_VOCAB_READ_ADDRESS="$(GRPC_VOCAB_READ_ADDRESS)" GRPC_VOCAB_READ_NEGOTIATION="$(GRPC_VOCAB_READ_NEGOTIATION)" \
	LOG_LEVEL_KAFKA=$(LOG_LEVEL_KAFKA) LOG_LEVEL_HIBERNATE=$(LOG_LEVEL_HIBERNATE) LOG_LEVEL_POSTGRESQL=$(LOG_LEVEL_POSTGRESQL) \
	JAVA_OPTS="$(JAVA_OPTS)" LOGSTASH_HOST=$(LOGSTASH_HOST) \
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
