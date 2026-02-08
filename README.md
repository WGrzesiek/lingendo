# Lingendo

> Platforma do nauki języków obcych oparta na architekturze mikroserwisowej

Lingendo to kompleksowa aplikacja umożliwiająca naukę słownictwa poprzez fiszki, talie, sesje nauki z algorytmem spaced repetition oraz generowanie zdań przykładowych z wykorzystaniem AI (OpenAI). System wspiera role nauczyciela i ucznia, grupy, leaderboardy oraz zaawansowaną analitykę postępów.

---

## Spis treści

- [Architektura](#architektura)
- [Stack technologiczny](#stack-technologiczny)
- [Struktura repozytorium](#struktura-repozytorium)
- [Mikroserwisy](#mikroserwisy)
- [Wzorce architektoniczne](#wzorce-architektoniczne)
- [Komunikacja](#komunikacja)
- [Bazy danych](#bazy-danych)
- [Frontend](#frontend)
- [Infrastruktura](#infrastruktura)
- [Monitoring i Observability](#monitoring-i-observability)
- [CI/CD](#cicd)
- [Testy](#testy)
- [Uruchomienie lokalne](#uruchomienie-lokalne)
- [Zmienne środowiskowe](#zmienne-środowiskowe)
- [Licencja](#licencja)

---

## Architektura

```
┌──────────────────────────────────────────────────────────────────────┐
│                           CLOUDFLARE                                 │
│                    (DNS, DDoS Protection, SSL)                       │
└────────────────────────────┬─────────────────────────────────────────┘
                             │
┌────────────────────────────▼─────────────────────────────────────────┐
│                            NGINX                                     │
│                  (Reverse Proxy, Load Balancer)                       │
│        "/" → Frontend (Next.js)    "/api" → API Gateway              │
└──────────┬────────────────────────────────────┬──────────────────────┘
           │                                    │
┌──────────▼──────────┐          ┌──────────────▼──────────────┐
│     FRONTEND        │          │       API GATEWAY            │
│  Next.js 15 + React │          │  Spring Cloud Gateway        │
│  React Native/Expo  │          │  JWT Auth + Routing          │
└─────────────────────┘          └──────────────┬──────────────┘
                                                │
          ┌─────────────────────────────────────┼──────────────────────┐
          │                                     │                      │
 ┌────────▼────────┐  ┌────────────▼──────────┐  ┌────────▼──────────┐
 │  USER-SERVICE   │  │    DECK-SERVICE       │  │ VOCABULARY-CMD    │
 │  (PostgreSQL)   │  │    (PostgreSQL)       │  │  (PostgreSQL)     │
 └─────────────────┘  └──────────────────────-┘  └────────┬──────────┘
                                                          │ Outbox
         ┌────────────────────────────────────────────────▼──────────┐
         │                     DEBEZIUM (CDC)                        │
         └────────────────────────────┬─────────────────────────────┘
                                      │
         ┌────────────────────────────▼─────────────────────────────┐
         │                       KAFKA                              │
         └────────────┬──────────────┬──────────────┬──────────────┘
                      │              │              │
            ┌─────────▼───┐ ┌───────▼──────┐ ┌────▼──────────┐
            │ VOCAB-READ  │ │ STATS-SVC    │ │ KOOG-SERVICE  │
            │  (MongoDB)  │ │ (ClickHouse) │ │ (Kotlin + AI) │
            └─────────────┘ └──────────────┘ └───────────────┘
```

---

## Stack technologiczny

### Backend

| Technologia          | Wersja    | Zastosowanie                 |
| -------------------- | --------- | ---------------------------- |
| Java                 | 24        | Główny język backendowy      |
| Kotlin               | 2.x       | Koog-service (AI)            |
| Spring Boot          | 3.5 / 4.0 | Framework mikroserwisów      |
| Spring Cloud Gateway | 2025.0    | API Gateway (WebFlux)        |
| gRPC                 | 1.75      | Komunikacja między serwisami |
| Flyway               | —         | Migracje baz danych          |

### Frontend

| Technologia                        | Zastosowanie               |
| ---------------------------------- | -------------------------- |
| Next.js 15 (App Router, Turbopack) | Aplikacja webowa           |
| React 19                           | Komponenty UI              |
| TypeScript                         | Type safety                |
| TailwindCSS 4                      | Stylowanie (utility-first) |
| TanStack React Query               | Server state management    |
| Radix UI + shadcn/ui               | Headless UI components     |
| React Native (Expo)                | Aplikacja mobilna          |

### Bazy danych

| Baza            | Zastosowanie                                                                |
| --------------- | --------------------------------------------------------------------------- |
| PostgreSQL 15   | User-service, Vocabulary-command, Deck-service (dane transakcyjne + Outbox) |
| MongoDB 8.0     | Vocabulary-read (CQRS query side, dokumenty)                                |
| ClickHouse 24.8 | Statistics-service (OLAP, analytics)                                        |
| Redis 8.2       | Cache, rate limiting, sesje                                                 |

### Message Broker i CDC

| Technologia          | Zastosowanie                         |
| -------------------- | ------------------------------------ |
| Apache Kafka (KRaft) | Event streaming                      |
| Debezium 3.1         | Change Data Capture (Outbox Pattern) |
| Kafka Connect        | Integracja Debezium z Kafka          |

### Monitoring

| Technologia | Zastosowanie             |
| ----------- | ------------------------ |
| Prometheus  | Zbieranie metryk         |
| Grafana     | Wizualizacja dashboardów |
| Zipkin      | Distributed tracing      |
| ELK Stack   | Centralne logowanie      |

### DevOps

| Technologia             | Zastosowanie                   |
| ----------------------- | ------------------------------ |
| Docker / Docker Compose | Konteneryzacja                 |
| Jenkins                 | CI/CD pipelines                |
| Nginx                   | Reverse proxy, load balancer   |
| Cloudflare              | DNS, SSL, WAF, DDoS protection |

---

## Struktura repozytorium

```
learnwords/
├── learnwords-backend/           # Monorepo backendu
│   ├── api-gateway/              # Spring Cloud Gateway
│   └── learnwords-services/
│       ├── common/               # Wspólne moduły
│       ├── parent/               # Parent POM
│       ├── proto-shared/         # Definicje gRPC (.proto)
│       ├── user-service/         # Zarządzanie użytkownikami
│       ├── deck-service/         # Zarządzanie taliami
│       ├── vocabulary-command-service/   # CQRS Command
│       ├── vocabulary-read-service/      # CQRS Query
│       └── statistics-service/   # Analityka (ClickHouse)
│
├── koog-service/                 # Kotlin - serwis AI (OpenAI)
│
├── learnwords-frontend/          # Next.js 15 + React 19
│   └── src/
│       ├── app/                  # App Router (route groups)
│       ├── features/             # Feature modules
│       ├── components/           # Shared UI (shadcn/ui)
│       ├── lib/                  # Utilities, API client
│       └── types/                # Shared TypeScript types
│
├── my-expo-app/                  # React Native (Expo)
│
├── qaa/                          # Testy E2E (Playwright + Python)
│
├── Infra/                        # Infrastruktura (Docker Compose)
│   ├── docker-compose.db.yml
│   ├── docker-compose.kafka.yml
│   ├── docker-compose.connect.yml
│   ├── docker-compose.monitoring.yml
│   ├── docker-compose.nginx.yml
│   ├── docker-compose.jenkins.yml
│   ├── docker-elk/
│   ├── grafana-dashboard/
│   ├── nginx/
│   └── init-db/ / init-mongo/ / init-clickhouse/
│
└── docs/                         # Dokumentacja pomocnicza
```

---

## Mikroserwisy

### API Gateway (port 8811)

Pojedynczy punkt wejścia do systemu. Odpowiada za routing, autoryzację JWT (cookie lub header), rate limiting (Redis), CORS i propagację kontekstu użytkownika (`X-User-Id`, `X-Username`) do serwisów downstream.

**Technologie:** Spring Cloud Gateway (WebFlux), Spring Security OAuth2 Resource Server, Redis Reactive, gRPC Client.

### User-Service (port 8812)

Rejestracja, autentykacja, zarządzanie profilami, relacje nauczyciel–uczeń, grupy studentów, znajomi. Generowanie JWT (RS256) i udostępnianie JWKS endpoint.

**Baza:** PostgreSQL · **gRPC:** `AuthService`, `UserRelationsService`, `StudentGroupService`

### Deck-Service (port 8814)

CRUD talii (decks), zarządzanie flashcards, udostępnianie talii, kursy i enrollment. Komunikuje się z Vocabulary-Read przez gRPC.

**Baza:** PostgreSQL · **gRPC:** `DeckReadService`

### Vocabulary-Command (port 8810)

Strona zapisu CQRS — tworzenie, aktualizacja i usuwanie słówek. Każda operacja zapisywana jest w jednej transakcji razem z eventem w tabeli `outbox`.

**Baza:** PostgreSQL (+ Outbox) · **Pattern:** CQRS Command Side

### Vocabulary-Read (port 8813)

Strona odczytu CQRS — zoptymalizowane zapytania do MongoDB. Synchronizowany przez eventy Kafka pochodzące z Debezium.

**Baza:** MongoDB · **gRPC:** `VocabularyReadService` · **Pattern:** CQRS Query Side

### Statistics-Service

Konsumuje wszystkie eventy domenowe z Kafka i agreguje je w ClickHouse. Dostarcza dashboardy dla uczniów i nauczycieli, leaderboardy, metryki retencji.

**Baza:** ClickHouse (SummingMergeTree, Materialized Views)

### Koog-Service (Kotlin)

Serwis AI oparty na frameworku Koog (JetBrains). Konsumuje eventy z Kafka, generuje zdania przykładowe i tłumaczenia z użyciem OpenAI API (Structured Output).

**Technologie:** Kotlin, Spring Boot, Koog AI Framework, Kafka Consumer

---

## Wzorce architektoniczne

### CQRS (Command Query Responsibility Segregation)

Zastosowany w domenie Vocabulary — osobny model zapisu (PostgreSQL) i odczytu (MongoDB). Pozwala na niezależne skalowanie i optymalizację obu stron.

```
POST/PUT/DELETE → Vocabulary-Command (PostgreSQL)
                        │ Outbox → Debezium → Kafka
                        ▼
GET             → Vocabulary-Read (MongoDB)
```

### Transactional Outbox + Debezium

Gwarantuje spójność between zapisem do bazy a publikacją eventów — bez Two-Phase Commit. Event jest zapisywany w tabeli `outbox` w tej samej transakcji co dane domenowe. Debezium (CDC) nasłuchuje WAL PostgreSQL i publikuje eventy do Kafka.

### Event-Driven Architecture

Serwisy Statistics, Vocabulary-Read i Koog reagują na eventy asynchronicznie przez Kafka. Brak zależności synchronicznych — odporność na awarie i niezależne skalowanie.

**Topiki Kafka:** `vocabulary.events`, `deck.events`, `flashcard.events`, `user.events`, `session.events`, `ai.sentence.request`

### API Gateway Pattern

Jeden punkt wejścia do mikroserwisów z cross-cutting concerns: autoryzacja, rate limiting, tracing, CORS.

### Database per Service

Każdy mikroserwis posiada własną bazę danych — izolacja schematów, brak współdzielonych tabel.

### Domain-Driven Design

Bounded Contexts: **Identity** (User-Service), **Learning** (Deck, Vocabulary, Koog), **Analytics** (Statistics).

---

## Komunikacja

### gRPC (synchroniczna)

Wspólne kontrakty `.proto` w module `proto-shared`:

| Proto              | Serwis          | Opis                 |
| ------------------ | --------------- | -------------------- |
| `auth.proto`       | User-Service    | Autentykacja         |
| `users.proto`      | User-Service    | Relacje użytkowników |
| `groups.proto`     | User-Service    | Grupy studentów      |
| `deck.proto`       | Deck-Service    | Odczyt talii         |
| `vocabulary.proto` | Vocabulary-Read | Odczyt słówek        |
| `sentence.proto`   | Koog-Service    | Zdania AI            |

### Kafka (asynchroniczna)

Event streaming z wykorzystaniem Debezium CDC i Outbox Pattern. Konsumenci: `statistics-service`, `vocabulary-read-service`, `koog-service`.

---

## Bazy danych

| Baza           | Serwis                                         | Uzasadnienie                                           |
| -------------- | ---------------------------------------------- | ------------------------------------------------------ |
| **PostgreSQL** | user-service, vocabulary-command, deck-service | ACID, relacje, Outbox + WAL dla Debezium               |
| **MongoDB**    | vocabulary-read                                | Elastyczne schematy, zoptymalizowany odczyt dokumentów |
| **ClickHouse** | statistics-service                             | Kolumnowa OLAP, MergeTree, Materialized Views          |
| **Redis**      | api-gateway                                    | Cache, rate limiting, sesje                            |

Konfiguracja PostgreSQL dla CDC:

```
wal_level = logical
max_replication_slots = 4
max_wal_senders = 4
```

---

## Frontend

### Next.js 15 (Web)

App Router z route groups: `(public)` dla logowania/rejestracji, `(protected)` dla dashboard i nauki. Autoryzacja oparta na JWT w httpOnly cookie — backend zarządza tokenem, frontend nie manipuluje.

```
src/
├── app/
│   ├── (public)/         # Login, signup
│   └── (protected)/      # Dashboard, nauka, statystyki
├── features/
│   ├── auth/             # Logowanie, rejestracja, hooks autoryzacji
│   ├── deck/             # Zarządzanie taliami
│   ├── learning/         # Sesje nauki
│   ├── statistics/       # Statystyki i wykresy
│   ├── groups/           # Grupy studentów
│   ├── friends/          # Znajomi
│   ├── leaderboard/      # Ranking
│   └── ...
├── components/ui/        # Shared components (shadcn/ui pattern)
└── lib/api/              # Axios client z auto-refresh
```

**Kluczowe technologie:** React 19, TypeScript, TailwindCSS 4, TanStack React Query, Radix UI, Framer Motion, Zod, React Hook Form.

### React Native / Expo (Mobile)

Aplikacja mobilna współdzieląca kontrakty API z wersją webową. Expo Router, NativeWind (TailwindCSS).

---

## Infrastruktura

Cała infrastruktura zdefiniowana w Docker Compose (katalog `Infra/`):

| Plik                            | Komponenty                             |
| ------------------------------- | -------------------------------------- |
| `docker-compose.db.yml`         | PostgreSQL, MongoDB, ClickHouse, Redis |
| `docker-compose.kafka.yml`      | Kafka (KRaft), Kafka UI                |
| `docker-compose.connect.yml`    | Kafka Connect, Debezium, AKHQ          |
| `docker-compose.monitoring.yml` | Prometheus, Grafana, Zipkin            |
| `docker-compose.nginx.yml`      | Nginx (reverse proxy)                  |
| `docker-compose.jenkins.yml`    | Jenkins CI/CD                          |
| `docker-compose.portainer.yml`  | Portainer                              |
| `docker-elk/`                   | Elasticsearch, Logstash, Kibana        |

Wszystkie kontenery pracują w sieci `learnwords-net`.

---

## Monitoring i Observability

```
              ┌─────────────┐
              │   GRAFANA    │
              │  Dashboards  │
              └──────┬───────┘
                     │
      ┌──────────────┼──────────────┐
      │              │              │
      ▼              ▼              ▼
┌───────────┐ ┌───────────┐ ┌───────────┐
│PROMETHEUS │ │  ZIPKIN   │ │ ELK STACK │
│  Metrics  │ │  Tracing  │ │  Logging  │
└─────┬─────┘ └─────┬─────┘ └─────┬─────┘
      │              │              │
      └──────────────┼──────────────┘
                     ▼
          Spring Boot Actuator
    /actuator/prometheus  (Micrometer)
    /actuator/health      (Health checks)
    Brave Tracing         → Zipkin
    Logback JSON          → Logstash
```

**Exportery Prometheus:** nginx-exporter (9113), kafka-exporter (9308), Spring Actuator per serwis.

---

## CI/CD

Jenkins pipelines (`Jenkinsfile` w każdym serwisie):

1. **Build** — `mvnw clean package` / `gradle build`
2. **Test** — unit + integracyjne (JaCoCo coverage)
3. **E2E** — Playwright (Python)
4. **Docker Build** — budowanie obrazu kontenera
5. **Deploy** — deploy na staging/produkcję

Osobne `deploy.Jenkinsfile` dla deploymentu produkcyjnego.

---

## Testy

| Typ          | Narzędzia                | Zakres                         |
| ------------ | ------------------------ | ------------------------------ |
| Unit         | JUnit 5, Mockito         | Logika domenowa, Value Objects |
| Integracyjne | Testcontainers, WireMock | Porty + Adaptery, bazy danych  |
| E2E          | Playwright (Python)      | Pełne scenariusze użytkownika  |
| Raporty      | Allure Report            | Wizualizacja wyników           |
| Coverage     | JaCoCo                   | Pokrycie kodu                  |

Testy E2E znajdują się w katalogu `qaa/` — Page Object Pattern, konfiguracja w `conftest.py`.

---

## Uruchomienie lokalne

### Wymagania

- Docker + Docker Compose
- Java 24+ (SDKMAN zalecany)
- Node.js 20+ / pnpm
- Kotlin (dla koog-service)

### 1. Uruchomienie infrastruktury

```bash
cd Infra

# Sieć Docker
docker compose -f docker-compose.network.yml up -d

# Bazy danych
docker compose -f docker-compose.db.yml up -d

# Kafka + Debezium
docker compose -f docker-compose.kafka.yml up -d
docker compose -f docker-compose.connect.yml up -d

# Monitoring (opcjonalnie)
docker compose -f docker-compose.monitoring.yml up -d
```

### 2. Uruchomienie backendu

```bash
cd learnwords-backend

# API Gateway
cd api-gateway && ./mvnw spring-boot:run

# User-Service
cd learnwords-services/user-service && ./mvnw spring-boot:run

# Deck-Service
cd learnwords-services/deck-service && ./mvnw spring-boot:run

# Vocabulary-Command
cd learnwords-services/vocabulary-command-service && ./mvnw spring-boot:run

# Vocabulary-Read
cd learnwords-services/vocabulary-read-service && ./mvnw spring-boot:run

# Statistics-Service
cd learnwords-services/statistics-service && ./mvnw spring-boot:run
```

### 3. Uruchomienie Koog-Service

```bash
cd koog-service
./gradlew bootRun
```

### 4. Uruchomienie frontendu

```bash
cd learnwords-frontend
npm install
npm run dev
```

### 5. Rejestracja konektorów Debezium

```bash
# Konektor Outbox
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d @Infra/connector_outbox.json

# Konektor AI
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d @Infra/connector_outbox_AI.json
```

---

## Zmienne środowiskowe

Każdy serwis wymaga odpowiednich zmiennych środowiskowych. Plik `.env.example` w `learnwords-services/` zawiera wzorzec konfiguracji.

Kluczowe zmienne:

| Zmienna                           | Opis                            |
| --------------------------------- | ------------------------------- |
| `POSTGRES_HOST` / `POSTGRES_PORT` | Połączenie z PostgreSQL         |
| `MONGODB_URI`                     | Connection string MongoDB       |
| `KAFKA_BOOTSTRAP_SERVERS`         | Adresy brokerów Kafka           |
| `REDIS_HOST` / `REDIS_PORT`       | Połączenie z Redis              |
| `CLICKHOUSE_URL`                  | Adres ClickHouse                |
| `OPENAI_API_KEY`                  | Klucz API OpenAI (koog-service) |
| `JWT_SECRET` / `JWT_PRIVATE_KEY`  | Klucze JWT (user-service)       |
| `ZIPKIN_BASE_URL`                 | Endpoint Zipkin                 |

---

## Bezpieczeństwo

- **JWT (RS256)** w httpOnly cookie — walidacja w API Gateway
- **Cloudflare** — WAF, DDoS protection, SSL termination
- **Segmentacja sieci** — Docker network, brak bezpośredniego dostępu do serwisów
- **Database per Service** — izolacja danych
- **Rate Limiting** — Redis w API Gateway
- **Secrets** — zmienne środowiskowe, poza repozytorium

---

## Licencja

Projekt inżynierski — wszelkie prawa zastrzeżone.

© 2026 Lingendo
