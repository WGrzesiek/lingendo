# ==== Parametry ====
APP_NAME        ?= vocabulary-read-service
IMAGE_NAME      ?= $(APP_NAME)
TAG             ?= dev
IMAGE           := $(IMAGE_NAME):$(TAG)

PROFILE         ?= dev
SERVER_PORT     ?= 8813
GRPC_PORT       ?= 9099
SKIP_TESTS      ?= true

COMPOSE_FILE    ?= docker-compose.vocabulary-read.yml
NETWORK_NAME    ?= learnwords-net

# MongoDB
DB_MONGODB_HOST ?= mongodb
DB_MONGODB_PORT ?= 27017
DB_MONGODB_NAME ?= vocabulary-command-service
DB_MONGODB_USER ?= User
DB_MONGODB_PASSWORD ?= password

# Kafka
KAFKA_BOOTSTRAP ?= kafka:9092
KAFKA_GROUP     ?= vocabulary-read-service-group

# Logi / JVM
LOG_LEVEL_DEBEZIUM     ?= INFO
LOG_LEVEL_DEBEZIUM_PG  ?= INFO
LOG_LEVEL_KAFKA        ?= INFO
LOG_LEVEL_HIBERNATE    ?= INFO
LOG_LEVEL_POSTGRESQL   ?= INFO
JAVA_OPTS              ?=

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
	IMAGE_NAME=$(IMAGE_NAME) IMAGE_TAG=$(TAG) PROFILE=$(PROFILE) \
	SERVER_PORT=$(SERVER_PORT) GRPC_PORT=$(GRPC_PORT) \
	DB_MONGODB_HOST=$(DB_MONGODB_HOST) DB_MONGODB_PORT=$(DB_MONGODB_PORT) \
	DB_MONGODB_NAME=$(DB_MONGODB_NAME) DB_MONGODB_USER=$(DB_MONGODB_USER) DB_MONGODB_PASSWORD=$(DB_MONGODB_PASSWORD) \
	KAFKA_BOOTSTRAP=$(KAFKA_BOOTSTRAP) KAFKA_GROUP=$(KAFKA_GROUP) \
	LOG_LEVEL_DEBEZIUM=$(LOG_LEVEL_DEBEZIUM) LOG_LEVEL_DEBEZIUM_PG=$(LOG_LEVEL_DEBEZIUM_PG) \
	LOG_LEVEL_KAFKA=$(LOG_LEVEL_KAFKA) LOG_LEVEL_HIBERNATE=$(LOG_LEVEL_HIBERNATE) LOG_LEVEL_POSTGRESQL=$(LOG_LEVEL_POSTGRESQL) \
	JAVA_OPTS="$(JAVA_OPTS)" \
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
