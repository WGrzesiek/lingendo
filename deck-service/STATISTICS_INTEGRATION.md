# 📊 Integracja Statystyk - Dokumentacja

## ✅ Co zostało zaimplementowane:

### 1. **Session Entity** - rozszerzone pola

- `userId` - identyfikator użytkownika
- `totalFlashcards` - liczba fiszek w sesji
- `correctAnswers` - poprawne odpowiedzi
- `wrongAnswers` - błędne odpowiedzi
- `skipped` - pominięte fiszki
- `durationSeconds` - czas trwania sesji
- `completedAt` - timestamp zakończenia
- `status` - status sesji (IN_PROGRESS, COMPLETED, ABANDONED)

### 2. **Event Classes** (w pakiecie `event/`)

- `FlashcardAnsweredEvent` - wysyłany po każdej odpowiedzi
- `SessionCompletedEvent` - wysyłany po zakończeniu sesji
- `FlashcardProgressEvent` - wysyłany gdy flashcard zostanie opanowany

### 3. **StatisticsEventPublisher**

Service do wysyłania eventów do Kafka z trzema metodami:

- `publishFlashcardAnswered()` - topic: `flashcard.answered`
- `publishSessionCompleted()` - topic: `session.completed`
- `publishFlashcardProgress()` - topic: `flashcard.progress`

### 4. **KafkaProducerConfig**

Konfiguracja Kafka z:

- Idempotencją
- Retry mechanizmem
- Compression (snappy)
- JSON serialization

### 5. **SessionStatisticsHelper**

Przykładowy helper pokazujący jak używać całej integracji

---

## 🚀 Jak używać:

### 1. Rejestrowanie odpowiedzi na flashcard

```java
@Service
public class YourSessionService {
    private final SessionStatisticsHelper statisticsHelper;

    public void answerFlashcard(String sessionId, String flashcardId, boolean isCorrect) {
        // Ta metoda:
        // - Aktualizuje Session (correctAnswers/wrongAnswers)
        // - Aktualizuje Flashcard (totalAttempts/correctAnswers)
        // - Wysyła FlashcardAnsweredEvent do Kafka
        statisticsHelper.recordFlashcardAnswer(sessionId, flashcardId, isCorrect);
    }
}
```

### 2. Zakończenie sesji

```java
public void finishSession(String sessionId) {
    // Ta metoda:
    // - Ustawia status na COMPLETED
    // - Oblicza czas trwania
    // - Wysyła SessionCompletedEvent do Kafka
    statisticsHelper.completeSession(sessionId);
}
```

### 3. Oznaczenie fiszki jako opanowanej

```java
public void markAsLearned(String flashcardId) {
    // Ta metoda:
    // - Ustawia isLearned = true
    // - Oblicza accuracy
    // - Wysyła FlashcardProgressEvent do Kafka
    statisticsHelper.markFlashcardAsLearned(flashcardId);
}
```

---

## 🔧 Konfiguracja

### application.yml / application.properties

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092 # Zmień na produkcyjny adres
    producer:
      acks: all
      retries: 3
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

### Dodaj dependency w pom.xml (jeśli jeszcze nie masz):

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

---

## 📈 Statistics Service - co musi robić:

### 1. Utworzyć Kafka Consumers dla trzech topików:

```java
@KafkaListener(topics = "flashcard.answered", groupId = "statistics-service")
public void handleFlashcardAnswered(FlashcardAnsweredEvent event) {
    // Aktualizuj agregowane statystyki
    // Np. dodaj do UserDailyStatistics
}

@KafkaListener(topics = "session.completed", groupId = "statistics-service")
public void handleSessionCompleted(SessionCompletedEvent event) {
    // Aktualizuj statystyki sesji
    // Oblicz streak
    // Aktualizuj totalStudyTime
}

@KafkaListener(topics = "flashcard.progress", groupId = "statistics-service")
public void handleFlashcardProgress(FlashcardProgressEvent event) {
    // Aktualizuj DeckStatistics
    // Aktualizuj licznik opanowanych fiszek
}
```

### 2. Agregowane tabele (przykład):

```sql
-- Statystyki dzienne użytkownika
CREATE TABLE user_daily_statistics (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    date DATE NOT NULL,
    total_sessions INT DEFAULT 0,
    total_flashcards INT DEFAULT 0,
    total_correct_answers INT DEFAULT 0,
    total_wrong_answers INT DEFAULT 0,
    total_study_time_seconds BIGINT DEFAULT 0,
    current_streak INT DEFAULT 0,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE(user_id, date)
);

-- Statystyki dla każdej talii
CREATE TABLE deck_statistics (
    id VARCHAR(36) PRIMARY KEY,
    deck_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    total_flashcards INT DEFAULT 0,
    learned_flashcards INT DEFAULT 0,
    average_accuracy DECIMAL(5,2) DEFAULT 0,
    total_sessions INT DEFAULT 0,
    last_studied_at TIMESTAMP,
    total_study_time_seconds BIGINT DEFAULT 0,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE(deck_id, user_id)
);
```

---

## 🎯 Flow diagram

```
User odpowiada na flashcard
         │
         ▼
SessionStatisticsHelper.recordFlashcardAnswer()
         │
         ├──> Aktualizuje Session (DB)
         ├──> Aktualizuje Flashcard (DB)
         └──> Wysyła FlashcardAnsweredEvent (Kafka)
                       │
                       ▼
              Statistics Service (Consumer)
                       │
                       └──> Aktualizuje UserDailyStatistics (DB)
```

---

## 🔒 Ważne uwagi:

### 1. **Error Handling**

Publisher używa `try-catch` - błędy Kafka NIE zatrzymają sesji nauki.

### 2. **Idempotencja**

Statistics Service MUSI sprawdzać czy event już był przetworzony:

```java
if (eventAlreadyProcessed(event.getEventId())) {
    return; // Skip
}
```

### 3. **Monitoring**

Monitoruj:

- Kafka consumer lag
- Event processing time
- Dead Letter Queue

### 4. **Testy**

Testuj z `@EmbeddedKafka`:

```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"flashcard.answered"})
class StatisticsEventPublisherTest {
    // ...
}
```

---

## 📋 TODO:

- [ ] Dodać migracje Liquibase/Flyway dla nowych pól w Session
- [ ] Zaimplementować Statistics Service consumers
- [ ] Dodać monitoring (Prometheus metrics)
- [ ] Utworzyć DLQ dla failed events
- [ ] Dodać testy jednostkowe i integracyjne
- [ ] Zdecydować czy events powinny mieć `eventId` dla idempotencji

---

## 🤔 Pytania do rozważenia:

1. **Event ID**: Czy dodać UUID do każdego eventu dla deduplikacji?
2. **Retention**: Jak długo przechowywać raw events w Kafka?
3. **Partitioning**: Partycjonować po `userId` czy `deckId`?
4. **Batch Processing**: Czy Statistics Service powinien przetwarzać w batchach?

---

**Status**: ✅ Gotowe do użycia po dodaniu dependency na Spring Kafka!
