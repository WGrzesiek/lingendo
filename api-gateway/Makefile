# ==== Parametry ====
APP_NAME        ?= api-gateway
IMAGE_NAME      ?= $(APP_NAME)
TAG             ?= dev                 # np. dev / latest / $(shell git rev-parse --short HEAD)
IMAGE           := $(IMAGE_NAME):$(TAG)

PROFILE         ?= dev                 # dev / prod
PORT            ?= 8811
SKIP_TESTS      ?= true                # ustaw na false, jeśli chcesz uruchamiać testy

COMPOSE_FILE    ?= docker-compose.api-gateway.yml
NETWORK_NAME    ?= learnwords-net

# JWT / JWKS / gRPC
JWT_PRIVATE_KEY_LOCATION ?= file:/etc/keys/private_key.pem
JWT_PUBLIC_KEY_LOCATION  ?= file:/etc/keys/public_key.pem
JWT_EXPIRE_IN            ?= 86400000
ISSUER_URI               ?= http://api-gateway:8811
JWKS_URI                 ?= http://api-gateway:8811/.well-known/jwks.json

GRPC_CLIENT_AUTH_ADDRESS         ?= static://user-service:9090
GRPC_CLIENT_AUTH_NEGOTIATIONTYPE ?= PLAINTEXT

# Opcjonalnie: JVM flags
JAVA_OPTS ?=

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
	JWT_PRIVATE_KEY_LOCATION="$(JWT_PRIVATE_KEY_LOCATION)" JWT_PUBLIC_KEY_LOCATION="$(JWT_PUBLIC_KEY_LOCATION)" \
	JWT_EXPIRE_IN="$(JWT_EXPIRE_IN)" ISSUER_URI="$(ISSUER_URI)" JWKS_URI="$(JWKS_URI)" \
	GRPC_CLIENT_AUTH_ADDRESS="$(GRPC_CLIENT_AUTH_ADDRESS)" GRPC_CLIENT_AUTH_NEGOTIATIONTYPE="$(GRPC_CLIENT_AUTH_NEGOTIATIONTYPE)" \
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
