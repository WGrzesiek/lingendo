# 🖥️ Staging VM - Instrukcja uruchomienia i konfiguracji

## 📋 Spis treści

1. [Import maszyny wirtualnej](#1-import-maszyny-wirtualnej)
2. [Połączenie SSH](#2-połączenie-ssh)
3. [Portainer - zarządzanie kontenerami](#3-portainer---zarządzanie-kontenerami)
4. [Uruchamianie kontenerów](#4-uruchamianie-kontenerów)
5. [Zawartość VM](#5-zawartość-vm)

---

## 1. Import maszyny wirtualnej

Maszyna wirtualna Staging została wyeksportowana do formatu **OVF**.

### Kroki importu do VMware:

1. Otwórz **VMware Workstation** lub **VMware Player**
2. Wybierz: `File` → `Open...` (lub `Import`)
3. Wskaż plik `.ovf` z wyeksportowaną maszyną
4. Wybierz lokalizację dla zaimportowanej VM
5. Kliknij `Import` i poczekaj na zakończenie procesu
6. Uruchom zaimportowaną maszynę wirtualną

> **Uwaga:** Jeśli VMware zapyta o typ importu, wybierz "I copied it" lub "I moved it" w zależności od sytuacji.

---

## 2. Połączenie SSH

Po uruchomieniu maszyny wirtualnej możesz połączyć się przez SSH.

### Dane logowania:

| Parametr  | Wartość              |
| --------- | -------------------- |
| **Host**  | `<adres_IP_maszyny>` |
| **User**  | `wawrzen`            |
| **Hasło** | `Ubuntu98`           |

### Komenda połączenia:

```bash
ssh wawrzen@<adres_IP_maszyny>
```

---

## 3. Portainer - zarządzanie kontenerami

Na maszynie działa **Portainer** - graficzny interfejs do zarządzania kontenerami Docker.

### Dostęp do Portainer:

1. Otwórz przeglądarkę i przejdź do: `http://<adres_IP_maszyny>:9443/#!/home`

2. Zaloguj się danymi:

   | Pole      | Wartość            |
   | --------- | ------------------ |
   | **Login** | `wawrzen`          |
   | **Hasło** | `Ubuntu98Ubuntu98` |

3. Po zalogowaniu zobaczysz dashboard z listą kontenerów

---

## 4. Uruchamianie kontenerów

Kontenery można uruchomić na dwa sposoby:

### Opcja A: Przez Portainer (GUI)

1. Zaloguj się do Portainer
2. Przejdź do sekcji `Containers`
3. Znajdź kontener który chcesz uruchomić
4. Kliknij przycisk `Start` przy wybranym kontenerze

### Opcja B: Przez Jenkins

1. Zaloguj się do Jenkins (patrz: [JENKINS_SETUP.md](JENKINS_SETUP.md))
2. Upewnij się że node **"deploy"** jest skonfigurowany na tę maszynę staging
3. Uruchom odpowiedni pipeline:
   - Infrastructure pipelines (bazy danych, Kafka, etc.)
   - Service pipelines (mikroserwisy)
   - Frontend pipeline

---

## 5. Zawartość VM

Maszyna wirtualna Staging zawiera:

### 🐳 Zainstalowane oprogramowanie

| Oprogramowanie | Opis                      |
| -------------- | ------------------------- |
| **Docker**     | Konteneryzacja aplikacji  |
| **SSH**        | Zdalne połączenia         |
| **Portainer**  | GUI do zarządzania Docker |

### 💾 Bazy danych z testowymi danymi

- **PostgreSQL** - główna baza danych z przykładowymi użytkownikami, taliami i słówkami
- **MongoDB** - baza dla read models
- **ClickHouse** - baza dla statystyk
- **Redis** - cache

### 📊 Monitoring

- **Grafana** - gotowe dashboardy do monitoringu aplikacji
- **Prometheus** - metryki
- **Zipkin** - distributed tracing

---

## 🔗 Przydatne linki

| Serwis                | URL                                        |
| --------------------- | ------------------------------------------ |
| Portainer             | `http://<host>:9443/#!/home`               |
| Grafana - Dashboardy  | `http://<host>:3000/dashboards`            |
| Kibana                | `http://<host>:5601/`                      |
| Zipkin                | `http://<host>:9411/zipkin/`               |
| Swagger (API Gateway) | `http://<host>:8811/swagger-ui/index.html` |
| Frontend (via Nginx)  | `http://<host>:80`                         |

---
