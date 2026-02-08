# ==== Parametry ====
APP_NAME        ?= vocabulary-command-service
IMAGE_NAME      ?= $(APP_NAME)
TAG             ?= dev                 # np. dev / latest / $(shell git rev-parse --short HEAD)
IMAGE           := $(IMAGE_NAME):$(TAG)

PROFILE         ?= dev                 # dev / prod
PORT            ?= 8810
SKIP_TESTS      ?= true                # ustaw na false, jeśli chcesz uruchamiać testy

COMPOSE_FILE    ?= docker-compose.app.yml
NETWORK_NAME    ?= learnwords-net

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
	IMAGE_NAME=$(IMAGE_NAME) IMAGE_TAG=$(TAG) PORT=$(PORT) PROFILE=$(PROFILE) \
	DATASOURCE_URL=${DATASOURCE_URL} DATASOURCE_USERNAME=${DATASOURCE_USERNAME} DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD} \
	KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS} \
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
