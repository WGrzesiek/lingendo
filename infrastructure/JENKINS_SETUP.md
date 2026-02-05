# 🔧 Jenkins - Instrukcja uruchomienia i konfiguracji

## 📋 Spis treści

1. [Import maszyny wirtualnej](#1-import-maszyny-wirtualnej)
2. [Połączenie SSH](#2-połączenie-ssh)
3. [Uruchomienie Docker Registry](#3-uruchomienie-docker-registry)
4. [Konfiguracja node'a "deploy"](#4-konfiguracja-nodea-deploy)
5. [Dostępne funkcjonalności](#5-dostępne-funkcjonalności)

---

## 1. Import maszyny wirtualnej

Maszyna wirtualna z Jenkins została wyeksportowana do formatu **OVF**.

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

## 3. Uruchomienie Docker Registry

Jenkins wymaga lokalnego Docker Registry do przechowywania zbudowanych obrazów.

### Sprawdzenie istniejącego kontenera:

```bash
docker ps -a
```

Znajdź kontener `registry` na liście.

### Uruchomienie istniejącego kontenera:

```bash
# Użyj ID kontenera z listy, np.:
docker start 84b372666a7d

# lub po nazwie:
docker start registry
```

### Jeśli kontener nie istnieje - utwórz nowy:

```bash
docker run -d \
  -p 5000:5000 \
  --restart always \
  --name registry \
  registry:3
```

### Weryfikacja działania:

```bash
# Sprawdź czy kontener działa
docker ps | grep registry

# Sprawdź dostępność registry
curl http://localhost:5000/v2/_catalog
```

---

## 4. Konfiguracja node'a "deploy"

Node **"deploy"** służy do deployowania serwisów na docelową maszynę. Przed użyciem należy skonfigurować hosta docelowego.

### Kroki konfiguracji:

1. **Otwórz Jenkins** w przeglądarce: `http://<adres_IP_Jenkins>:8080`

2. **Przejdź do zarządzania node'ami:**
   - `Manage Jenkins` → `Manage Nodes and Clouds`
   - Kliknij na node **"deploy"**
   - Wybierz `Configure`

3. **Zmień ustawienia hosta docelowego:**

   | Pole                 | Wartość                                     |
   | -------------------- | ------------------------------------------- |
   | **Host**             | Adres IP maszyny docelowej                  |
   | **SSH User**         | Nazwa użytkownika SSH na maszynie docelowej |
   | **SSH Password/Key** | Hasło lub klucz SSH                         |

4. **Zapisz konfigurację** klikając `Save`

5. **Przetestuj połączenie** - uruchom testowy pipeline lub sprawdź status node'a

> **Ważne:** Node "deploy" musi mieć dostęp SSH do maszyny docelowej, gdzie będą uruchamiane kontenery Docker.

---

## 5. Dostępne funkcjonalności

Po poprawnej konfiguracji Jenkins umożliwia:

### 🏗️ Budowanie serwisów

- Automatyczne budowanie aplikacji backendowych (Java/Maven)
- Budowanie frontendu (Next.js)
- Tworzenie obrazów Docker
- Push do lokalnego registry

### 🚀 Deployowanie

- **Infrastruktura** - uruchamianie docker-compose dla baz danych, Kafka, monitoring
- **Serwisy aplikacyjne** - deployment mikroserwisów
- **Frontend** - deployment aplikacji Next.js

### 🔄 Migracje

- Uruchamianie migracji baz danych
- Aktualizacje schematów

### 📊 Raporty

- **Allure Reports** - raporty z testów automatycznych
- **Coverage Reports** - pokrycie kodu testami
- **Build Reports** - historia buildów i logów

---

## 🔗 Przydatne linki

| Serwis          | URL                  |
| --------------- | -------------------- |
| Jenkins         | `http://<host>:8080` |
| Docker Registry | `http://<host>:5000` |

---
