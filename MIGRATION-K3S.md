# Lingendo → k3s (native, low-RAM) — plan migracji

Branch: `deploy/k3s-native-images`

Cel: wdrożenie Lingendo na własną domenę w klastrze k3s przy **minimalnym zużyciu RAM**.
Strategia: GraalVM native images, wypięcie observability, przeniesienie backing-services na
zewnętrzne darmowe managed (off-cluster), lekka szyna zdarzeń w klastrze.

---

## Decyzje (potwierdzone)

| Obszar            | Decyzja                                                                 |
| ----------------- | ---------------------------------------------------------------------- |
| Obrazy Java       | **GraalVM native image** (build `ghcr.io/graalvm/native-image-community:25`, runtime `debian:bookworm-slim`, wzorzec devPath) |
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

`<relativePath>../parent/pom.xml</relativePath>` w dzieciach jest nieaktualne (folder to
`learnwords-parent`), ale w trybie reactora to **tylko warning** — parent rozwiązywany po GAV.
Standalone single-module build (`cd user-service && mvn`) natomiast padnie, dopóki parent +
`common` + `proto-shared` nie są w `~/.m2`. Native Docker build robi to dwuetapowo
(install deps → native compile) — patrz `user-service/Dockerfile.native`.

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

- [~] 1. user-service pilot — **config gotowy** (`application-k8s.yml`, profil `native` w pom,
      observability wypięta, `Dockerfile.native`). ⏳ **native-compile do walidacji** (Docker, brak GraalVM lokalnie).
- [x] 2. manifesty współdzielone (`k8s/`: ns+configmap+secret, user-service, redpanda+debezium, edge+ingress)
- [ ] 3. deck / vocab-command / vocab-read (kopia wzorca user-service)
- [ ] 4. statistics (ClickHouse→Postgres — rewrite JDBC + migracje)
- [ ] 5. api-gateway (native + managed Redis)
- [ ] 6. koog-service (native, fallback JVM+CDS)
- [ ] 7. frontend static → nginx (`output:"export"` + `Dockerfile.static`)
- [ ] 8. Google auth
