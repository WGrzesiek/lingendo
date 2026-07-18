# Lingendo → k3s (native, low-RAM) — plan migracji

Branch: `deploy/k3s-native-images`

Cel: wdrożenie Lingendo na własną domenę w klastrze k3s przy **minimalnym zużyciu RAM**.
Strategia: GraalVM native images, wypięcie observability, przeniesienie backing-services na
zewnętrzne darmowe managed (off-cluster), lekka szyna zdarzeń w klastrze.

---

## Decyzje (potwierdzone)

| Obszar            | Decyzja                                                                 |
| ----------------- | ---------------------------------------------------------------------- |
| Obrazy Java       | **GraalVM native image** (build `ghcr.io/graalvm/native-image-community:24` — zgodny z java.version=24, runtime `debian:bookworm-slim`, wzorzec devPath) |
| Statistics DB     | **Neon Postgres** (rezygnacja z ClickHouse — swap JDBC + Flyway PG)     |
| Kafka / CDC       | **Redpanda single-node (k3s) + Debezium Server** (Estuary odrzucone*)   |
| Frontend          | **Next static export → nginx** (`output: "export"`, zero RAM runtime)   |
| Observability     | **Wypięte** (Prometheus, Zipkin/tracing, ELK/logstash). Health zostaje. |
| Gateway refresh   | Domyślnie **managed Redis** (cloud.redis.io). Opcja: Postgres/R2DBC.    |
| Google auth       | **Po** działającym deployu (osobny feature).                            |

\* Estuary Flow robi CDC baza→hurtownia; nie potrafi być szyną request/response dla pętli
AI (koog, topic `ai.sentence.request`) ani dostarczać eventów do serwisowych `@KafkaListener`.
Architektura używa Kafki jednocześnie jako CDC (outbox→Debezium) **i** jako app event bus.

---

## Backing services (off-cluster, free tier)

| Usługa         | Provider           | Używane przez                                   |
| -------------- | ------------------ | ----------------------------------------------- |
| PostgreSQL     | Neon (1 baza)      | **wszystkie** serwisy PG + outbox — jedna baza `lingendo` |
| MongoDB        | Atlas (M0)         | vocabulary-read (baza `lingendo`)               |
| Redis          | cloud.redis.io     | api-gateway (refresh-token store)               |
| (Kafka/CDC)    | Redpanda + DBZ     | **in-cluster** (nie ma sensownego free managed) |

### ⚠ Współdzielona baza `lingendo` — separacja przez schema

Free-tier = **jedna** baza Postgres `lingendo` dla wszystkich serwisów. Każdy serwis ma
własne migracje Flyway → przy wspólnej bazie **kolidują na `flyway_schema_history`** i tabelach.
Wymagane przed rolloutem serwisu #2:

- `spring.flyway.default-schema=<serwis>` + `spring.flyway.schemas=<serwis>` (osobny schema + historia),
- `spring.jpa.properties.hibernate.default_schema=<serwis>`,
- schematy: `user`, `deck`, `vocabulary`, `statistics`, `public` (outbox).

Dla pilota (sam user-service) nie jest to blokujące. Debezium: baza `lingendo`, tabela `public.outbox`.

---

## Architektura eventów (zweryfikowana)

Producenci **direct KafkaTemplate** (app bus): user, deck, koog (`ai.sentence.request`),
vocabulary-read (statusy). Producenci **outbox→Debezium**: command services → tabela `outbox`
→ topiki `*.events`. Konsumenci: statistics ×8, vocabulary-read (projekcje), koog, deck.

```
command-services --(KafkaTemplate)--> Redpanda --> statistics / read / koog
command-services --(outbox table)--> Debezium Server --> Redpanda --> consumers
koog <--(ai.sentence.request / response)--> vocabulary-read
```

---

## Budżet RAM (szacunek, native)

| Komponent                     | Limit    |
| ----------------------------- | -------- |
| user-service (native)         | 256Mi    |
| deck-service (native)         | 256Mi    |
| vocabulary-command (native)   | 256Mi    |
| vocabulary-read (native)      | 256Mi    |
| statistics (native)           | 256Mi    |
| api-gateway (native)          | 256Mi    |
| koog-service (native/JVM**)   | 384Mi    |
| Redpanda (single node)        | 512–768Mi|
| Debezium Server               | 256–384Mi|
| nginx (frontend static)       | 64Mi     |
| **Razem w klastrze**          | ~2.8–3.2Gi (requests niżej) |

\*\* koog to Kotlin + framework AI `koog` — native najbardziej ryzykowny; fallback = slim JRE + CDS.

---

## Ryzyka native (per zależność)

- **net.devh grpc-server-spring-boot-starter 3.1.0** — niepełne reachability-metadata dla
  GraalVM; wymaga własnych hintów lub migracji na Spring gRPC. **Najwyższe ryzyko.**
- **Flyway** — działa native z hintami na zasoby migracji (`db/migration/*`).
- **Spring Kafka** — OK z hintami serializerów JSON.
- **koog (AI, Kotlin)** — native nietestowany; prawdopodobny fallback JVM+CDS.
- **Spring Cloud Gateway (WebFlux)** — native wspierane.

Każdy serwis wymaga realnego `native:compile` (Docker, ~10–15 min) do walidacji.
Środowisko lokalne: brak GraalVM/native-image → build wyłącznie w Dockerze (jest Docker 29).

---

## Build / reactor

`learnwords-parent/pom.xml` to **agregator reactora** (packaging `pom`, moduły:
`../common ../proto-shared ../user-service ../deck-service ../vocabulary-command-service
../vocabulary-read-service ../statistics-service`). Buildy idą przez ten reactor:
`cd learnwords-parent && ./mvnw ...`.

**NAPRAWIONE:** `<relativePath>` w 7 dzieciach wskazywał `../parent/pom.xml` (folder to
`learnwords-parent`) → **fatalny** błąd `Non-resolvable parent POM` (parent nie w `.m2`).
Poprawione na `../learnwords-parent/pom.xml`. Native Docker build działa dwuetapowo
(install deps → native compile) — patrz `user-service/Dockerfile.native`.

**NAPRAWIONE:** `proto-shared` — `protobuf-maven-plugin` miał niepinowaną wersję + stary schemat
configu (`<protocVersion>`/`<binaryMavenPlugins>`); Maven brał 5.1.7 → NPE. Przypięte 5.1.7 +
schemat `<protoc kind="binary-maven">` / `<plugins>`.

Krok `mvnw install` (JVM, Java 24) zwalidowany lokalnie: `common`+`proto-shared`+`user-service` = SUCCESS.
`native:compile` (krok 2 Dockerfile) nadal niezwalidowany — brak GraalVM lokalnie, wychodzi w CI/Dockerze.

`api-gateway` i `koog-service` (Gradle) są **poza** tym reactorem (standalone).

---

## Kolejność wdrożenia (stopniowo)

1. **Pilot: user-service** — native pom profile, `application-k8s.yml`, `Dockerfile.native`,
   manifest k8s, strip observability. Walidacja native-compile. ← *szablon dla reszty*
2. Współdzielone manifesty klastra: namespace, ConfigMap/Secret, Ingress, Redpanda, Debezium Server.
3. Rollout na deck / vocabulary-command / vocabulary-read (ten sam wzorzec).
4. statistics-service — dodatkowo **rewrite ClickHouse → Postgres** (JDBC + migracje).
5. api-gateway — native + wskazanie na managed Redis (+ ewentualnie Postgres/R2DBC później).
6. koog-service — native (fallback JVM+CDS jeśli AI framework nie kompiluje się native).
7. frontend — `output:"export"` + obraz nginx + manifest.
8. Google auth (osobny feature) po stabilizacji.

### Checklista

- [x] 1. user-service pilot — native-compile **przeszedł na CI** (GraalVM 16GB). Rozwiązane:
      relativePath, proto-shared plugin, Maven-w-obrazie, logowanie wyłączone (slf4j-nop),
      netty runtime-init, BouncyCastle build-time-init, observability wypięta.
- [x] 2. manifesty współdzielone (`k8s/`: ns+configmap+secret, redpanda+debezium, edge+ingress)
- [x] 3. deck / vocab-command / vocab-read — **native-ready** (pom logging-off+obs-off, logback usunięty,
      `application-k8s.yml`, workflow, manifest `11-services.yaml`). Kompilacja JVM = SUCCESS.
      DRY: profil `native` w parencie, hinty native w `common`, wspólny root `Dockerfile.native` (ARG SERVICE).
- [~] 4. statistics — **fundament gotowy**: migracja PG (tabele+VIEWs), pom/config swap, native/logging pattern,
      workflow, manifest. ⏳ **zostaje**: rewrite SQL w 9 repo (funkcje CH→PG) + upserty consumerów
      (patrz `statistics-service/CH-TO-PG-REPOS.md`). Bez tego stats zbuduje się ale nie zadziała poprawnie.
- [x] 5. api-gateway — native-ready: pom (logging/obs off + profil native), `native-image.properties`,
      własny `Dockerfile.native` (install proto-shared → build standalone), `application-k8s.yml` (Redis),
      workflow, manifest `13-api-gateway.yaml` (Redis env + JWT-keys volume z Secret `gateway-jwt-keys`).
- [~] 6. koog-service — config gotowy: `build.gradle.kts` (plugin native + obs/logging off),
      `Dockerfile.native` (Gradle nativeCompile), `application-k8s.yml`, workflow, manifest `14-koog.yaml`.
      ⚠ **native niezwalidowany** (ai.koog AI framework) — prawdopodobnie wymaga hintów lub fallbacku JVM+CDS.
- [ ] 7. frontend static → nginx (`output:"export"` + `Dockerfile.static`)
- [ ] 8. Google auth
