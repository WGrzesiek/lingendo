# k8s — manifesty deploymentu Lingendo (k3s, low-RAM native)

Pełny plan i decyzje: [`../MIGRATION-K3S.md`](../MIGRATION-K3S.md).

## Pliki

| Plik              | Zawartość                                                        |
| ----------------- | --------------------------------------------------------------- |
| `00-base.yaml`    | namespace `lingendo`, wspólny ConfigMap, **szablon** Secretu     |
| `10-user-service.yaml` | Deployment+Service user-service (**szablon** dla reszty Java) |
| `20-eventing.yaml`| Redpanda (single node) + Debezium Server (CDC outbox)           |
| `30-edge.yaml`    | frontend (nginx static) + Ingress (Traefik)                     |

Serwisy jeszcze nieprzepisane (deck, vocabulary-command, vocabulary-read, statistics,
api-gateway, koog) → skopiuj wzorzec z `10-user-service.yaml`, podmień port/DB/grupę.

## Prerekwizyty (poza klastrem)

- **Neon**: **jedna** baza `lingendo` (wszystkie serwisy PG + tabela `public.outbox`).
  Separacja serwisów przez schema — patrz `../MIGRATION-K3S.md` (kolizja Flyway).
  Dla CDC włącz **logical replication** na bazie `lingendo` (+ slot `dbz_outbox_slot`).
- **Atlas MongoDB** M0: baza `lingendo`.
- **cloud.redis.io**: instancja dla api-gateway.
- Obrazy native zbudowane i wypchnięte do rejestru dostępnego z k3s
  (`docker build -f user-service/Dockerfile.native -t lingendo/user-service:native .`).

## Kolejność

```bash
# 1. Uzupełnij realne wartości w 00-base.yaml (Secret) — NIE commituj.
kubectl apply -f 00-base.yaml
kubectl apply -f 20-eventing.yaml     # broker + CDC najpierw
kubectl apply -f 10-user-service.yaml # potem serwisy
kubectl apply -f 30-edge.yaml         # na końcu edge/ingress
```

## Uwaga bezpieczeństwo

`00-base.yaml` zawiera **placeholdery** `REPLACE_ME`. Realne sekrety trzymaj poza repo:
`kubectl create secret generic lingendo-secrets --from-literal=...` albo sealed-secrets / SOPS.
