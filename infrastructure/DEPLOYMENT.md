# 🚀 LearnWords - Instrukcja uruchomienia projektu od zera

## 📋 Spis treści

1. [Wymagania wstępne](#wymagania-wstępne)
2. [Struktura projektu](#struktura-projektu)
3. [Krok 1: Sieć Docker](#krok-1-sieć-docker)
4. [Krok 2: Bazy danych](#krok-2-bazy-danych)
5. [Krok 3: Weryfikacja PostgreSQL](#krok-3-weryfikacja-konfiguracji-postgresql)
6. [Krok 4: Kafka](#krok-4-kafka)
7. [Krok 5: Debezium Connect](#krok-5-debezium-connect)
8. [Krok 6: Utworzenie konektorów](#krok-6-utworzenie-konektorów)
9. [Krok 7: Serwisy backendowe](#krok-7-serwisy-backendowe)
10. [Krok 8: Koog Service (AI)](#krok-8-koog-service-ai---opcjonalnie)
11. [Krok 9: API Gateway](#krok-9-api-gateway)
12. [Krok 10: Frontend](#krok-10-frontend)
13. [Krok 10a: Mobile App (Expo)](#krok-10a-mobile-app-expo---opcjonalnie)
14. [Krok 11: Nginx](#krok-11-nginx)
15. [Krok 12: Monitoring (opcjonalnie)](#krok-12-monitoring-opcjonalnie)
16. [Krok 13: ELK Stack (opcjonalnie)](#krok-13-elk-stack-opcjonalnie)
17. [Troubleshooting](#troubleshooting)

---

## Uwaga

Jezeli w trakcie działania aplikacji wystapią błędy
bardzo proszę o restart serwisów aplikacyjnych(deck-service uruchomić na końcu).

## Wymagania wstępne

- **Docker** >= 24.0
- **Docker Compose** >= 2.20
- **Java** 24 (do budowania serwisów)
- **Node.js** 20+ (do budowania frontendu)
- **Maven** 3.9+ lub użyj wrappera `./mvnw`
- **Git**

### Porty używane przez projekt

| Port  | Serwis                     |
| ----- | -------------------------- |
| 80    | Nginx (produkcja)          |
| 3000  | Grafana                    |
| 3001  | Frontend (Next.js)         |
| 5432  | PostgreSQL                 |
| 6379  | Redis                      |
| 8080  | Jenkins                    |
| 8081  | pgAdmin                    |
| 8083  | Kafka Connect (Debezium)   |
| 8090  | AKHQ (Kafka UI)            |
| 8091  | Debezium UI                |
| 8092  | Mongo Express              |
| 8123  | ClickHouse HTTP            |
| 8799  | statistics-service         |
| 8810  | vocabulary-command-service |
| 8811  | api-gateway                |
| 8812  | user-service               |
| 8813  | vocabulary-read-service    |
| 8814  | deck-service               |
| 9090  | Prometheus                 |
| 9092  | Kafka                      |
| 9411  | Zipkin                     |
| 27017 | MongoDB                    |

---

## Struktura projektu

```
learnwords/
├── Infra/                          # Infrastruktura Docker
│   ├── docker-compose.network.yml  # Sieć Docker
│   ├── docker-compose.db.yml       # Bazy danych
│   ├── docker-compose.kafka.yml    # Kafka
│   ├── docker-compose.connect.yml  # Debezium Connect
│   ├── docker-compose.nginx.yml    # Nginx
│   ├── docker-compose.monitoring.yml # Prometheus, Grafana, Zipkin
│   ├── docker-elk/                 # ELK Stack
│   ├── connector_outbox.json       # Konektor Debezium
│   ├── connector_outbox_AI.json    # Konektor AI
│   ├── postgresql.conf             # Konfiguracja PostgreSQL
│   └── Makefile                    # Skróty
├── learnwords-backend/
│   ├── api-gateway/
│   └── learnwords-services/
│       ├── user-service/
│       ├── deck-service/
│       ├── vocabulary-command-service/
│       ├── vocabulary-read-service/
│       └── statistics-service/
├── learnwords-frontend/
├── moblie/
├── qaa
└── koog-service/                   # Serwis AI
```

---

## Krok 1: Sieć Docker

Wszystkie kontenery muszą być w tej samej sieci.

```bash

cd Infra
docker network create learnwords-net
```

---

## Krok 2: Bazy danych

Uruchamia: PostgreSQL, MongoDB, Redis, ClickHouse + UI do każdej.

**Automatyczna inicjalizacja**: Bazy danych tworzą się automatycznie przy pierwszym uruchomieniu dzięki skryptom w `init-db/`, `init-mongo/`, `init-clickhouse/`.

```bash

cd Infra
docker compose -f docker-compose.db.yml up -d
```

**Co zostanie utworzone automatycznie:**

- PostgreSQL: bazy `outbox`, `user_management`, `deck`, `koog` + włączony `wal_level=logical`
- MongoDB: baza `vocabulary-command-service` z użytkownikiem
- ClickHouse: baza `analytics` (tabele tworzy Flyway)

---

## Krok 3: Weryfikacja konfiguracji PostgreSQL

Konfiguracja `wal_level = logical` jest teraz ładowana automatycznie z pliku `postgresql.conf`.

**Weryfikacja:**

```bash

docker exec postgres psql -U admin -d postgres -c "SHOW wal_level;"
```

**Oczekiwany wynik:**

```
 wal_level
-----------
 logical
```

---

## Krok 4: Kafka

```bash

cd Infra
docker compose -f docker-compose.kafka.yml up -d
```

Poczekaj ~30 sekund na pełne uruchomienie.

---

## Krok 5: Debezium Connect

```bash

cd Infra
docker compose -f docker-compose.connect.yml up -d
```

**Weryfikacja:**

```bash

# Sprawdź status Kafka Connect
curl -s http://localhost:8083/
```

**Oczekiwany wynik:**

```json
{
  "version": "3.x.x",
  "commit": "...",
  "kafka_cluster_id": "..."
}
```

---

## Krok 6: Utworzenie konektorów

⚠️ Poczekaj aż Kafka Connect będzie w pełni gotowy (~30-60 sekund).

### 7.1 Konektor outbox (vocabulary-command-service)

```bash

cd Infra
curl -X POST -H 'Content-Type: application/json' \
  --data-binary @connector_outbox.json \
  http://localhost:8083/connectors
```

### 7.2 Konektor AI (koog-service) - opcjonalnie

```bash
curl -X POST -H 'Content-Type: application/json' \
  --data-binary @connector_outbox_AI.json \
  http://localhost:8083/connectors
```

### Weryfikacja konektorów

```bash
curl -s http://localhost:8083/connectors
```

**Oczekiwany wynik:**

```json
["outbox-connector", "koog-outbox-connector"]
```

### Sprawdzenie statusu konektora

```bash
curl -s http://localhost:8083/connectors/outbox-connector/status

curl -s http://localhost:8083/connectors/koog-outbox-connector/status

```

**Oczekiwany wynik:**

```json
{
  "name": "outbox-connector",
  "connector": {
    "state": "RUNNING",
    "worker_id": "172.23.0.7:8083"
  },
  "tasks": [{ "id": 0, "state": "RUNNING", "worker_id": "172.23.0.7:8083" }],
  "type": "source"
}
```

---

## Krok 7: Serwisy backendowe

Serwisy można uruchomić przez Docker (budowanie obrazów lokalnie) lub bezpośrednio Mavenem.

### 7.1 Budowanie wspólnych modułów (WYMAGANE)

⚠️ **Przed budowaniem serwisów musisz zainstalować moduły `common` i `proto-shared` w lokalnym repozytorium Maven.** Bez tego serwisy nie znajdą zależności.

> **WAŻNE:** Komendy Maven uruchamiamy z folderu `learnwords-services/` (gdzie jest `mvnw`), ale wskazujemy pom z `parent/` za pomocą flagi `-f`.

```bash
cd learnwords-backend/learnwords-services

# Instalacja modułu common
./mvnw -f parent/pom.xml install -pl :common -am -DskipTests

# Instalacja modułu proto-shared (generuje klasy z Protobuf)
./mvnw -f parent/pom.xml install -pl :proto-shared -am -DskipTests
```

### 7.2 Budowanie serwisów

**Budowanie wszystkich serwisów naraz:**

```bash
cd learnwords-backend/learnwords-services
./mvnw -f parent/pom.xml clean package -DskipTests
```

**Lub pojedynczy serwis:**

```bash
cd learnwords-backend/learnwords-services
./mvnw -f parent/pom.xml clean package -pl :user-service -am -DskipTests
```

### Opcja A: Docker (lokalne obrazy bez registry)

Najpierw budujemy obraz Docker, a następnie uruchamiamy docker-compose z przekazaniem nazwy obrazu przez zmienne środowiskowe.

```bash
cd learnwords-backend/learnwords-services

# user-service
docker build -t user-service:local -f user-service/Dockerfile user-service
IMAGE_NAME=user-service IMAGE_TAG=local docker compose -f user-service/docker-compose.user.yml up -d

# deck-service
docker build -t deck-service:local -f deck-service/Dockerfile deck-service
IMAGE_NAME=deck-service IMAGE_TAG=local docker compose -f deck-service/docker-compose.deck.yml up -d

# vocabulary-command-service
docker build -t vocabulary-command-service:local -f vocabulary-command-service/Dockerfile vocabulary-command-service
IMAGE_NAME=vocabulary-command-service IMAGE_TAG=local docker compose -f vocabulary-command-service/docker-compose.vocabulary-command.yml up -d

# vocabulary-read-service
docker build -t vocabulary-read-service:local -f vocabulary-read-service/Dockerfile vocabulary-read-service
IMAGE_NAME=vocabulary-read-service IMAGE_TAG=local docker compose -f vocabulary-read-service/docker-compose.vocabulary-read.yml up -d

# statistics-service
docker build -t statistics-service:local -f statistics-service/Dockerfile statistics-service
IMAGE_NAME=statistics-service IMAGE_TAG=local docker compose -f statistics-service/docker-compose.statistics.yml up -d
```

> **Uwaga:** Zmienne `IMAGE_NAME` i `IMAGE_TAG` są wymagane, ponieważ docker-compose używa ich w konfiguracji: `image: ${IMAGE_NAME:-user-service}:${IMAGE_TAG:-dev}`

### Opcja B: Lokalnie (development)

Uruchamianie bezpośrednio z Mavena (każdy serwis w osobnym terminalu):

```bash
cd learnwords-backend/learnwords-services

# user-service
./mvnw spring-boot:run -pl user-service -Dspring-boot.run.profiles=dev

# deck-service
./mvnw spring-boot:run -pl deck-service -Dspring-boot.run.profiles=dev

# vocabulary-command-service
./mvnw spring-boot:run -pl vocabulary-command-service -Dspring-boot.run.profiles=dev

# vocabulary-read-service
./mvnw spring-boot:run -pl vocabulary-read-service -Dspring-boot.run.profiles=dev

# statistics-service
./mvnw spring-boot:run -pl statistics-service -Dspring-boot.run.profiles=dev
```

### Migracje Flyway

Migracje uruchamiają się **automatycznie** przy starcie serwisu w trybie Docker/prod.

Aby ręcznie uruchomić migracje z maszyny lokalnej (używaj `localhost`, nie nazw kontenerów):

> **Uwaga:** Jeśli schema już istnieje (np. z init-db), dodaj flagę `-Dflyway.baselineOnMigrate=true`

#### user-service (PostgreSQL)

```bash
cd learnwords-backend/learnwords-services/user-service

./mvnw -B flyway:migrate \
  -Dflyway.url="jdbc:postgresql://localhost:5432/user_management" \
  -Dflyway.user="admin" \
  -Dflyway.password="adminpassword" \
  -Dflyway.schemas="public" \
  -Dflyway.baselineOnMigrate=true \
  -DskipTests
```

#### deck-service (PostgreSQL)

```bash
cd learnwords-backend/learnwords-services/deck-service

./mvnw -B flyway:migrate \
  -Dflyway.url="jdbc:postgresql://localhost:5432/deck" \
  -Dflyway.user="admin" \
  -Dflyway.password="adminpassword" \
  -Dflyway.schemas="public" \
  -Dflyway.baselineOnMigrate=true \
  -DskipTests
```

#### vocabulary-command-service (PostgreSQL)

```bash
cd learnwords-backend/learnwords-services/vocabulary-command-service

./mvnw -B flyway:migrate \
  -Dflyway.url="jdbc:postgresql://localhost:5432/outbox" \
  -Dflyway.user="admin" \
  -Dflyway.password="adminpassword" \
  -Dflyway.schemas="public" \
  -Dflyway.baselineOnMigrate=true \
  -DskipTests
```

#### statistics-service (ClickHouse)

```bash
cd learnwords-backend/learnwords-services/statistics-service

./mvnw -B flyway:migrate \
  -Dflyway.url="jdbc:clickhouse://localhost:8123/analytics" \
  -Dflyway.user="wawrzen" \
  -Dflyway.password="Ubuntu98" \
  -DskipTests
```

Jeśli jakaś migracja się nie powiodła i trzeba zacząć od nowa:

```bash
# Usuń tabelę historii Flyway (przykład dla ClickHouse)
docker exec clickhouse clickhouse-client --query "DROP TABLE IF EXISTS analytics.flyway_schema_history"

# Uruchom migracje ponownie
./mvnw -B flyway:migrate \
  -Dflyway.url="jdbc:clickhouse://localhost:8123/analytics" \
  -Dflyway.user="wawrzen" \
  -Dflyway.password="Ubuntu98" \
  -DskipTests
```

> **Uwaga:** Aby sprawdzić status migracji, użyj `flyway:info` zamiast `flyway:migrate`.

#### Cron dla Leaderboard Snapshot

Po wykonaniu migracji Flyway (tabele są już utworzone), skonfiguruj zadanie cron aktualizujące ranking użytkowników co godzinę.

**1. Utwórz plik SQL:**

```bash
cat > ~/leaderboard_snapshot.sql << 'EOF'
INSERT INTO analytics.leaderboard_snapshot
SELECT
    now() AS snapshot_time,
    row_number() OVER (ORDER BY sum(u.points) DESC) AS rank,
    u.user_id,
    dictGetString('analytics.usernames_dict', 'username', u.user_id) AS username,
    sum(u.points) AS total_points,
    countDistinctIf(d.deck_enrollment_id, d.deck_enrollment_id != '') AS finished_decks_count
FROM analytics.user_points_total u
LEFT JOIN analytics.deck_enrollments_finished d ON u.user_id = d.user_id
GROUP BY u.user_id
ORDER BY rank
LIMIT 1000;
EOF
```

**2. Dodaj zadanie cron:**

```bash
crontab -e
```

Dodaj linię (wykonuje się co godzinę o pełnej godzinie):

```cron
0 * * * * docker exec clickhouse clickhouse-client --query "$(cat ~/leaderboard_snapshot.sql)" >> ~/leaderboard_cron.log 2>&1
```

**3. Weryfikacja:**

```bash
# Sprawdź czy cron jest aktywny
crontab -l

# Ręczne wykonanie testu
docker exec clickhouse clickhouse-client --query "$(cat ~/leaderboard_snapshot.sql)"

# Sprawdź dane
docker exec clickhouse clickhouse-client --query "SELECT * FROM analytics.leaderboard_snapshot ORDER BY snapshot_time DESC LIMIT 10"
```

---

## Krok 8: Koog Service (AI) - opcjonalnie

Serwis AI do generowania przykładowych zdań. Wymaga klucza OpenAI API.

> ⚠️ **Klucz OpenAI API:** Aby uzyskać klucz, skontaktuj się z twórcą projektu.

### Budowanie i uruchamianie

```bash
cd koog-service

./gradlew clean build -x test
docker build -t koog-service:local .
OPENAI_API_KEY=sk-xxx IMAGE_NAME=koog-service IMAGE_TAG=local docker compose -f docker-compose.koog.yml up -d
```

> Migracje Flyway dla bazy `koog` uruchomi się automatycznie przy starcie serwisu

---

## Krok 9: API Gateway

```bash
cd learnwords-backend/api-gateway
./mvnw clean package -DskipTests
docker build -t api-gateway:local -f Dockerfile .
IMAGE_NAME=api-gateway IMAGE_TAG=local docker compose -f docker-compose.api-gateway.yml up -d
```

---

## Krok 10: Frontend

```bash
cd learnwords-frontend
docker build -t learnwords-frontend:local .
IMAGE_NAME=learnwords-frontend IMAGE_TAG=local docker compose -f docker-compose.frontend.yml up -d
```

**Weryfikacja:**

```bash
curl -s http://localhost:3001 | head -20
```

---

## Krok 10a: Mobile App (Expo) - opcjonalnie

Aplikacja mobilna do lokalnego testowania.

```bash
cd mobile
npm install
npm start
```

Po uruchomieniu pojawi się menu z opcjami:

| Klawisz | Akcja                                                 |
| ------- | ----------------------------------------------------- |
| `i`     | Uruchom na symulatorze iOS (wymaga Xcode)             |
| `a`     | Uruchom na emulatorze Android (wymaga Android Studio) |
| `w`     | Uruchom w przeglądarce (web)                          |

Możesz też zeskanować **QR kod** aplikacją **Expo Go** na fizycznym urządzeniu (iOS/Android).

---

## Krok 11: Nginx

```bash
cd Infra
docker compose -f docker-compose.nginx.yml up -d --build
```

**Weryfikacja:**

```bash
curl -s http://localhost/health
# Oczekiwany wynik: OK
```

---

## Krok 12: Monitoring (opcjonalnie)

```bash
cd Infra
docker compose -f docker-compose.monitoring.yml up -d
```

**Dostęp:**

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)
- Zipkin: http://localhost:9411

---

## Krok 13: ELK Stack (opcjonalnie)

```bash
cd Infra/docker-elk

# Pierwsze uruchomienie - setup użytkowników
docker compose --profile setup up -d

# Poczekaj na zakończenie setup, potem:
docker compose up -d
```

**Dostęp:**

- Kibana: http://localhost:5601
- Elasticsearch: http://localhost:9200

---

### Reset całego środowiska

```bash
cd Infra

# Zatrzymaj wszystko
docker compose -f docker-compose.nginx.yml down
docker compose -f docker-compose.connect.yml down
docker compose -f docker-compose.kafka.yml down
docker compose -f docker-compose.db.yml down
docker compose -f docker-compose.monitoring.yml down

# Usuń volumes (UWAGA: kasuje dane!)
docker volume prune -f

# Usuń sieć
docker network rm learnwords-net
```
