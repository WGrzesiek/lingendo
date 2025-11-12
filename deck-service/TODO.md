# TODO - Deck Service Improvements

## 🔴 Krytyczne - Obsługa Eventów Kafka

### 1. Idempotencja przy tworzeniu Flashcard

**Problem:** Event może być przetworzony wielokrotnie (retry, duplicate)

**Rozwiązanie:**

```java
@Override
public void processFlashcardCreate(VocabularyDto vocabularyDto) {
    // Sprawdź czy flashcard już istnieje
    if (flashcardRepository.existsByWordIdAndDeckId(
        vocabularyDto.getWordId(),
        vocabularyDto.getDeckId()
    )) {
        log.warn("Flashcard już istnieje dla wordId: {}", vocabularyDto.getWordId());
        return; // lub zaktualizuj
    }

    // Twórz nowy flashcard
    // ...
}
```

**Akcja:**

- [ ] Dodać metodę `existsByWordIdAndDeckId` w `FlashcardRepository`
- [ ] Zaimplementować sprawdzanie przed utworzeniem
- [ ] Dodać testy jednostkowe dla idempotencji

---

### 2. Dead Letter Queue (DLQ) dla Failed Events

**Problem:** Jeśli deck nie istnieje lub jest błąd, event blokuje kolejkę

**Rozwiązanie:**

- [ ] Skonfigurować DLQ w Kafka consumer
- [ ] Dodać retry mechanism z exponential backoff
- [ ] Logować failed events do monitoringu

**Przykładowa konfiguracja:**

```yaml
spring:
  kafka:
    consumer:
      properties:
        max.poll.interval.ms: 300000
    listener:
      ack-mode: manual
    retry:
      max-attempts: 3
      backoff:
        delay: 1000
        multiplier: 2.0
```

---

### 3. Obsługa VocabularyDeletedEvent

**Problem:** Co się dzieje gdy słówko zostanie usunięte z Vocabulary Service?

**Opcje:**

1. **Soft delete** - oznacz flashcard jako `deleted` (zachowaj historię nauki)
2. **Hard delete** - usuń flashcard całkowicie
3. **Orphan** - zostaw flashcard ale oznacz że słówko nie istnieje

**Rekomendacja:** Soft delete - zachowaj statystyki

**Akcja:**

- [ ] Dodać pole `isDeleted` w `Flashcard` entity
- [ ] Utworzyć `VocabularyDeletedEvent` DTO
- [ ] Zaimplementować consumer dla tego eventu
- [ ] Zaktualizować queries żeby pomijać usunięte flashcardy

---

### 4. Obsługa VocabularyUpdatedEvent

**Problem:** Co się dzieje gdy definicja słówka zostanie zmieniona?

**Scenariusze:**

- Zmiana definicji → czy resetować postęp nauki?
- Zmiana przykładu użycia → prawdopodobnie NIE resetować
- Fix błędu → możliwe że resetować

**Akcja:**

- [ ] Określić strategię aktualizacji
- [ ] Dodać pole `lastSyncedAt` w Flashcard
- [ ] Zaimplementować consumer dla VocabularyUpdatedEvent
- [ ] Rozważyć pole `resetProgressOnUpdate` w evencie

---

## 🟡 Ważne - Eventual Consistency

### 5. Frontend - Obsługa Asynchroniczności

**Problem:** Użytkownik tworzy słówko → flashcard pojawia się z opóźnieniem

**Rozwiązanie:**

- [ ] Dodać loading state w UI
- [ ] Pokazać notification "Słówko jest dodawane..."
- [ ] Opcjonalnie: WebSocket/SSE dla real-time updates
- [ ] Dodać endpoint do sprawdzania statusu: `GET /api/v1/flashcards/status/{wordId}`

---

## 🟢 Nice to Have - Dodatkowe Funkcjonalności

### 6. Monitoring i Observability

- [ ] Dodać metryki Kafka consumer lag
- [ ] Logować czas przetwarzania eventów
- [ ] Alerting gdy DLQ się zapełnia
- [ ] Dashboard dla event processing

### 7. Transactional Outbox Pattern

Jeśli Deck Service też wysyła eventy:

- [ ] Rozważyć implementację Outbox Pattern
- [ ] Zapewni atomowość DB + Kafka

### 8. Event Versioning

- [ ] Dodać pole `eventVersion` w eventach
- [ ] Plan migracji gdy struktura eventu się zmieni

---

## 🔵 Pytania do Product Owner / Team

1. **Polityka usuwania:**

   - Czy user może usunąć słówko gdy jest używane w deckach?
   - Czy flashcard powinien zostać usunięty czy zachowany?

2. **Polityka aktualizacji:**

   - Czy edycja definicji powinna resetować postęp nauki?
   - Czy powiadomić użytkownika że słówko zostało zmienione?

3. **SLA:**

   - Jaki jest akceptowalny czas opóźnienia (event lag)?
   - Czy 1-2 sekundy to OK dla użytkownika?

4. **Archiwizacja:**
   - Czy stare, nauczone flashcardy powinny być archiwizowane?
   - Jak długo przechowywać historię?

---

## 📚 Dokumentacja do Uzupełnienia

- [ ] Diagram sekwencji: Vocabulary Create → Flashcard Create
- [ ] Diagram obsługi błędów i retry
- [ ] API documentation dla eventów Kafka
- [ ] Runbook dla operations team (co robić gdy DLQ się zapełni)

---

## ✅ Już Zrobione (Dobra Architektura!)

- ✅ Wydzielony Vocabulary Service jako core domain
- ✅ Event-driven communication z Kafka
- ✅ Separation of Concerns (Vocabulary vs Flashcard)
- ✅ CQRS pattern (vocabulary-command-service + vocabulary-read-service)
- ✅ Loose coupling między serwisami
- ✅ **Statistics Integration** - rozszerzona Session entity i event classes
- ✅ **StatisticsEventPublisher** - service do wysyłania eventów
- ✅ **KafkaProducerConfig** - konfiguracja z idempotencją i retry
- ✅ **SessionStatisticsHelper** - przykładowa implementacja

---

## 📊 Nowe - Integracja Statystyk

### 9. Dodanie zależności Kafka

- [ ] Dodać `spring-kafka` do pom.xml
- [ ] Skonfigurować `bootstrap-servers` w application.yml

### 10. Migracje bazy danych

- [ ] Utworzyć migrację Liquibase/Flyway dla nowych pól w `Session`:
  - `user_id`
  - `total_flashcards`
  - `correct_answers`
  - `wrong_answers`
  - `skipped`
  - `duration_seconds`
  - `completed_at`
  - `status`

### 11. Integracja z SessionService

- [ ] Wstrzyknąć `SessionStatisticsHelper` do istniejącej implementacji
- [ ] Użyć `recordFlashcardAnswer()` podczas odpowiedzi
- [ ] Użyć `completeSession()` podczas zakończenia
- [ ] Użyć `markFlashcardAsLearned()` gdy flashcard zostanie opanowany

### 12. Statistics Service - Consumer Implementation

- [ ] Utworzyć consumers dla 3 topików:
  - `flashcard.answered`
  - `session.completed`
  - `flashcard.progress`
- [ ] Zaimplementować agregację statystyk
- [ ] Utworzyć tabele: `user_daily_statistics`, `deck_statistics`

### 13. Event ID dla Idempotencji

- [ ] Rozważyć dodanie `eventId` (UUID) do wszystkich eventów
- [ ] Statistics Service: sprawdzać czy event już był przetworzony
- [ ] Utworzyć tabelę `processed_events` z TTL

---

**Priorytet wykonania:**

1. Idempotencja Vocabulary events (🔴 krytyczne)
2. DLQ configuration (🔴 krytyczne)
3. **Kafka dependency i konfiguracja (🔴 krytyczne - dla statystyk)**
4. **Migracje DB dla Session (🔴 krytyczne - dla statystyk)**
5. VocabularyDeletedEvent (🟡 ważne)
6. VocabularyUpdatedEvent (🟡 ważne)
7. **Statistics Service consumers (🟡 ważne)**
8. Monitoring (🟢 nice to have)

---

**📖 Zobacz też:**

- `STATISTICS_INTEGRATION.md` - pełna dokumentacja integracji statystyk

https://macbook-air-grzegorz.ibis-tautara.ts.net/
https://macbook-air-grzegorz.ibis-tautara.ts.net/dashboard
https://macbook-air-grzegorz.ibis-tautara.ts.net/dashboard-teacher
https://macbook-air-grzegorz.ibis-tautara.ts.net/course/course-123
https://macbook-air-grzegorz.ibis-tautara.ts.net/learn/session-1
