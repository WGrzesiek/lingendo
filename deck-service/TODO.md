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


Deck-service
package com.learnwords.deckservice.service.session.flashcardFetchStrategy;

import com.learnwords.deckservice.entity.Flashcard;

import java.util.List;

/**
* Serwis strategii pobierania i sortowania fiszek dla sesji nauki.
*
* <p>Odpowiada za implementację różnych strategii wyboru fiszek do sesji:
* <ul>
*   <li>ALPHABETICAL - sortowanie alfabetyczne po słowie</li>
*   <li>RANDOM - losowa kolejność fiszek</li>
*   <li>REVERSE_ALPHABETICAL - sortowanie odwrotnie alfabetyczne</li>
*   <li>UNLEARNED_FIRST - nienauczone fiszki na początku, potem nauczone</li>
* </ul>
*
* <p>Każda strategia może mieć limit liczby fiszek (flashcardsPerSession).
* Jeśli limit jest null, zwracane są wszystkie fiszki.
*
* @author Grzegorz Wawrzeń
* @version 1.0
* @since 2025-11-12
* @see FlashcardFetchStrategy
  */
  public interface FlashcardFetchStrategyService {

  /**
    * Sortuje fiszki według wybranej strategii i opcjonalnie limituje liczbę.
    *
    * <p>Implementacja powinna:
    * <ol>
    *   <li>Zastosować sortowanie według strategii</li>
    *   <li>Jeśli limit jest ustawiony, zwrócić tylko pierwszych N fiszek</li>
    *   <li>Jeśli limit jest null, zwrócić wszystkie posortowane fiszki</li>
    * </ol>
    *
    * <p>Przykład użycia:
    * <pre>
    * List&lt;Flashcard&gt; flashcards = flashcardRepository.findByDeckId(deckId);
    * List&lt;Flashcard&gt; sorted = strategyService.sortFlashcardsByStrategy(
    *     FlashcardFetchStrategy.UNLEARNED_FIRST, 
    *     20L, 
    *     flashcards
    * );
    * // sorted zawiera max 20 fiszek, nienauczone najpierw
    * </pre>
    *
    * @param strategy strategia sortowania (ALPHABETICAL, RANDOM, UNLEARNED_FIRST, REVERSE_ALPHABETICAL)
    * @param limit maksymalna liczba fiszek do zwrócenia (null = bez limitu)
    * @param flashcards lista fiszek do posortowania
    * @return posortowana lista fiszek (z limitem jeśli ustawiony)
    * @throws IllegalArgumentException jeśli strategy jest null lub flashcards jest null
      */
      List<Flashcard> sortFlashcardsByStrategy(FlashcardFetchStrategy strategy, Long limit, List<Flashcard> flashcards);

  /**
    * Pobiera listę wszystkich obsługiwanych strategii.
    *
    * @return tablica wszystkich dostępnych strategii
      */
      default FlashcardFetchStrategy[] getSupportedStrategies() {
      return FlashcardFetchStrategy.values();
      }

  /**
    * Pobiera domyślną strategię pobierania fiszek.
    *
    * <p>Używana gdy użytkownik nie wybierze strategii explicite.
    *
    * @return domyślna strategia (UNLEARNED_FIRST)
      */
      default FlashcardFetchStrategy getDefaultStrategy() {
      return FlashcardFetchStrategy.UNLEARNED_FIRST;
      }
      }
      package com.learnwords.deckservice.service.session;

public interface SessionStatisticsHelper {
public void recordFlashcardAnswer(String sessionId, String flashcardId, boolean isCorrect);
public void markFlashcardAsLearned(String flashcardId);
public void completeSession(String sessionId);
public void abandonSession(String sessionId);
}
package com.learnwords.deckservice.service.session;

import com.learnwords.deckservice.dto.SessionDto;
import com.learnwords.deckservice.dto.SessionStatsDto;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.service.FlashcardFetchStrategy;

import java.util.List;
import java.util.Optional;

/**
* Serwis zarządzania sesjami nauki.
*
* <p>Odpowiada za pełny cykl życia sesji nauki:
* <ul>
*   <li>Inicjalizację nowej sesji z wybraną strategią pobierania fiszek</li>
*   <li>Rejestrację odpowiedzi użytkownika i aktualizację statystyk</li>
*   <li>Ukończenie lub porzucenie sesji</li>
*   <li>Pobieranie historii sesji użytkownika lub talii</li>
*   <li>Statystyki i postęp sesji</li>
* </ul>
*
* <p>Sesja może mieć różne statusy:
* <ul>
*   <li>IN_PROGRESS - sesja w toku, użytkownik odpowiada na fiszki</li>
*   <li>COMPLETED - sesja zakończona pomyślnie</li>
*   <li>ABANDONED - sesja porzucona przed ukończeniem</li>
* </ul>
*
* @author Grzegorz Wawrzeń
* @version 1.0
* @since 2025-11-12
* @see Session
* @see SessionDto
* @see SessionStatsDto
* @see FlashcardFetchStrategy
  */
  public interface SessionService {

  /**
    * Inicjalizuje nową sesję nauki dla wskazanej talii.
    *
    * <p>Tworzy sesję w statusie IN_PROGRESS i dodaje fiszki według wybranej strategii.
    * Liczba fiszek zależy od ustawień talii (flashcardsPerSession).
    *
    * @param deckId ID talii do nauki
    * @param flashcardFetchStrategy strategia wyboru fiszek (ALPHABETICAL, RANDOM, UNLEARNED_FIRST itp.)
    * @return ID utworzonej sesji
    * @throws RuntimeException jeśli talia nie istnieje lub jest pusta
      */
      String initializeSession(String deckId, FlashcardFetchStrategy flashcardFetchStrategy);

  /**
    * Ukończa sesję nauki.
    *
    * <p>Zmienia status sesji na COMPLETED, zapisuje czas ukończenia
    * i oblicza łączny czas trwania sesji.
    *
    * @param sessionId ID sesji do ukończenia
    * @throws RuntimeException jeśli sesja nie istnieje lub jest już ukończona
      */
      void completeSession(String sessionId);

  /**
    * Porzuca sesję nauki przed ukończeniem.
    *
    * <p>Zmienia status sesji na ABANDONED. Statystyki są zachowane,
    * ale sesja nie jest liczona jako ukończona.
    *
    * @param sessionId ID sesji do porzucenia
    * @throws RuntimeException jeśli sesja nie istnieje lub jest już ukończona/porzucona
      */
      void abandonSession(String sessionId);

  /**
    * Wstrzymuje aktywną sesję nauki.
    *
    * <p>Sesja może być później wznowiona metodą {@link #resumeSession(String)}.
    * Czas wstrzymania nie jest liczony do czasu trwania sesji.
    *
    * @param sessionId ID sesji do wstrzymania
    * @throws RuntimeException jeśli sesja nie istnieje lub nie jest IN_PROGRESS
      */
      void pauseSession(String sessionId);

  /**
    * Wznawia wstrzymaną sesję nauki.
    *
    * <p>Przywraca sesję do stanu IN_PROGRESS po wstrzymaniu.
    *
    * @param sessionId ID sesji do wznowienia
    * @throws RuntimeException jeśli sesja nie istnieje lub nie jest wstrzymana
      */
      void resumeSession(String sessionId);

  /**
    * Pobiera encję sesji po ID.
    *
    * <p>Zwraca pełną encję z danymi z bazy. Używaj gdy potrzebujesz
    * dostępu do relacji (deck, flashcards).
    *
    * @param sessionId ID sesji
    * @return encja sesji
    * @throws RuntimeException jeśli sesja nie istnieje
      */
      Session getSessionById(String sessionId);

  /**
    * Rejestruje odpowiedź użytkownika na fiszkę w sesji.
    *
    * <p>Aktualizuje statystyki sesji (correctAnswers/wrongAnswers)
    * oraz statystyki samej fiszki (totalAttempts, correctAnswers).
    *
    * @param sessionId ID sesji
    * @param flashcardId ID fiszki, na którą udzielono odpowiedzi
    * @param isCorrect czy odpowiedź była poprawna
    * @throws RuntimeException jeśli sesja/fiszka nie istnieje lub sesja nie jest aktywna
      */
      void recordAnswer(String sessionId, String flashcardId, boolean isCorrect);

  /**
    * Pobiera wszystkie sesje użytkownika.
    *
    * <p>Zwraca historię wszystkich sesji nauki użytkownika,
    * niezależnie od talii. Posortowane od najnowszych.
    *
    * @param userId ID użytkownika
    * @return lista sesji użytkownika (może być pusta)
      */
      List<SessionDto> getSessionsByUserId(String userId);

  /**
    * Pobiera wszystkie sesje dla wybranej talii.
    *
    * <p>Zwraca historię wszystkich sesji dla konkretnej talii,
    * niezależnie od użytkownika. Przydatne do statystyk talii.
    *
    * @param deckId ID talii
    * @return lista sesji talii (może być pusta)
      */
      List<SessionDto> getSessionsByDeckId(String deckId);

  /**
    * Pobiera aktywną (IN_PROGRESS) sesję użytkownika dla talii.
    *
    * <p>Sprawdza czy użytkownik ma już rozpoczętą sesję dla danej talii.
    * Używaj przed rozpoczęciem nowej sesji, żeby uniknąć duplikatów.
    *
    * @param userId ID użytkownika
    * @param deckId ID talii
    * @return aktywna sesja jeśli istnieje, Optional.empty() w przeciwnym razie
      */
      Optional<SessionDto> getActiveSessionByUserAndDeck(String userId, String deckId);

  /**
    * Pobiera statystyki sesji nauki.
    *
    * <p>Zwraca przetworzone statystyki z procentami accuracy,
    * postępu, średnim czasem itp. Użyj do wyświetlania podsumowania.
    *
    * @param sessionId ID sesji
    * @return DTO ze statystykami sesji
    * @throws RuntimeException jeśli sesja nie istnieje
      */
      SessionStatsDto getSessionStats(String sessionId);

  /**
    * Pobiera postęp sesji jako procent ukończenia.
    *
    * <p>Oblicza ile fiszek zostało już przerobione względem całkowitej liczby.
    *
    * @param sessionId ID sesji
    * @return procent ukończenia (0.0 - 100.0)
    * @throws RuntimeException jeśli sesja nie istnieje
      */
      double getSessionProgress(String sessionId);
      }
      package com.learnwords.deckservice.service.session;

import com.learnwords.deckservice.dto.sessionFlashcard.SessionFlashcardDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.service.FlashcardFetchStrategy;

import java.util.List;
import java.util.Optional;

/**
* Serwis zarządzania fiszkami w kontekście sesji nauki.
*
* <p>Odpowiada za:
* <ul>
*   <li>Dodawanie fiszek do sesji według strategii (ALPHABETICAL, RANDOM itp.)</li>
*   <li>Pobieranie fiszek przypisanych do sesji</li>
*   <li>Śledzenie postępu fiszek w sesji (czy odpowiedziano, czy poprawnie)</li>
*   <li>Zarządzanie pominiętymi fiszkami</li>
* </ul>
*
* <p>Fiszki w sesji mają dodatkowy kontekst:
* <ul>
*   <li>Czy już odpowiedziano na fiszkę w tej sesji</li>
*   <li>Czy odpowiedź była poprawna</li>
*   <li>Kiedy fiszka została dodana do sesji</li>
* </ul>
*
* @author Grzegorz Wawrzeń
* @version 1.0
* @since 2025-11-12
* @see SessionFlashcardDto
* @see FlashcardFetchStrategy
  */
  public interface SessionFlashcardService {

  /**
    * Dodaje fiszki do sesji według wybranej strategii.
    *
    * <p>Pobiera fiszki z talii, sortuje je według strategii (ALPHABETICAL, RANDOM, UNLEARNED_FIRST itp.)
    * i dodaje do sesji. Liczba fiszek zależy od ustawienia flashcardsPerSession w talii.
    *
    * <p>Strategie sortowania:
    * <ul>
    *   <li>ALPHABETICAL - alfabetycznie po słowie</li>
    *   <li>RANDOM - losowa kolejność</li>
    *   <li>REVERSE_ALPHABETICAL - odwrotnie alfabetycznie</li>
    *   <li>UNLEARNED_FIRST - nienauczone najpierw, potem reszta</li>
    * </ul>
    *
    * @param session sesja, do której dodawane są fiszki
    * @param deck talia, z której pobierane są fiszki
    * @param flashcardFetchStrategy strategia wyboru i sortowania fiszek
    * @return liczba dodanych fiszek do sesji
    * @throws RuntimeException jeśli talia jest pusta lub wystąpi błąd DB
      */
      String addFlashcardsToSession(Session session, Deck deck, FlashcardFetchStrategy flashcardFetchStrategy);

  /**
    * Pobiera wszystkie fiszki przypisane do sesji z pełnymi danymi słówek.
    *
    * <p>Zwraca listę fiszek w sesji wraz z:
    * <ul>
    *   <li>Pełnymi danymi słówka (przez gRPC z Vocabulary Service)</li>
    *   <li>Stanem fiszki (correctAnswers, totalAttempts, isLearned)</li>
    *   <li>Kontekstem sesji (answeredInSession, wasCorrect)</li>
    * </ul>
    *
    * @param sessionId ID sesji
    * @return lista fiszek w sesji z pełnymi danymi
    * @throws RuntimeException jeśli sesja nie istnieje
      */
      List<SessionFlashcardDto> getSessionFlashcards(String sessionId);

  /**
    * Pobiera postęp pojedynczej fiszki w sesji.
    *
    * <p>Zwraca informacje czy użytkownik już odpowiedział na tę fiszkę
    * w bieżącej sesji i czy odpowiedź była poprawna.
    *
    * @param sessionId ID sesji
    * @param flashcardId ID fiszki
    * @return DTO z postępem fiszki w sesji
    * @throws RuntimeException jeśli sesja/fiszka nie istnieje lub fiszka nie jest w sesji
      */
      Optional<SessionFlashcardDto> getFlashcardProgress(String sessionId, String flashcardId);

  /**
    * Pomija fiszkę w sesji.
    *
    * <p>Oznacza fiszkę jako pominiętą w bieżącej sesji.
    * Inkrementuje licznik skipped w sesji.
    *
    * @param sessionId ID sesji
    * @param flashcardId ID fiszki do pominięcia
    * @throws RuntimeException jeśli sesja/fiszka nie istnieje lub sesja nie jest aktywna
      */
      void skipFlashcard(String sessionId, String flashcardId);

  /**
    * Pobiera łączną liczbę fiszek w sesji.
    *
    * @param sessionId ID sesji
    * @return liczba fiszek w sesji
    * @throws RuntimeException jeśli sesja nie istnieje
      */
      int getTotalFlashcardsInSession(String sessionId);

  /**
    * Pobiera liczbę fiszek na które już odpowiedziano w sesji.
    *
    * @param sessionId ID sesji
    * @return liczba fiszek z odpowiedzią
    * @throws RuntimeException jeśli sesja nie istnieje
      */
      int getAnsweredFlashcardsCount(String sessionId);
      }
      package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.*;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.LearnAlgorithm;

import java.util.List;

public interface DeckService {
public boolean createDeck(String userId, CreateDeckDto createDeckDto);
public boolean deleteDeck(String deckId);
public String renameDeck(String deckId, String newName);
public boolean changeDeckVisibility(String deckId, boolean isPublic);
public DeckOwner changeDeckOwner(String deckId, DeckOwner newOwner);
public DeckDto getDeckById(String deckId);
List<DeckDto> getDecksByFilter(String userId, Boolean isPublic, DeckOwner owner);
default List<DeckDto> getDecksByFilter(String userId, DeckOwner owner) {
return getDecksByFilter(userId, null,  owner);
}
default List<DeckDto> getDecksByFilter(String userId) {
return getDecksByFilter(userId, null, null);
}
default List<DeckDto> getDecksByFilter(boolean isPublic) {
return getDecksByFilter(null, isPublic, null);
}
public DeckDetailsDto getDeckDetailsById(String deckId);
public DeckDetailsDto editDeckDetails(String deckId, DeckDetailsDto deckDetailsDto);
public long getTotalFlashcardsCount(String deckId);
public void updateLearnAlgorithm(String deckId, LearnAlgorithm algorithm);
public void updateFlashcardsPerSession(String deckId, Long count);

}
package com.learnwords.deckservice.service;

import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.FlashcardDto;
import com.learnwords.deckservice.dto.GetWordFromKafkaDto;
import com.learnwords.deckservice.entity.Flashcard;

import java.util.List;


public interface FlashcardService {

    public void processFlashcardCreateFromKafka(GetWordFromKafkaDto getWordFromKafkaDto);
    public void setInitialFlashcardState(String deckId, Flashcard flashcard);
    public List<FlashcardDto> getAllFlashcardsFromDeck(String deckId);
    public List<FlashcardDto> getFlashcardsFromDeckByFilter(String deckId, boolean isLearned, boolean isSkipped);
    public void updateFlashcard(String flashcardId, WordDto newWord);
    public void resetFlashcardProgress(String flashcardId);
    public void markAsLearned(String flashcardId, boolean learned);

}
package com.learnwords.deckservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "session_flashcard")
public class SessionFlashcard {

    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
private String id;

    @ManyToOne
    @JoinColumn(name = "learning_session_id", nullable = false)
    private Session session;

    @ManyToOne
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
package com.learnwords.deckservice.entity;

import com.learnwords.deckservice.enums.SessionStatus;
import com.learnwords.deckservice.enums.SessionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name = "session")
public class Session {
@Id
@Column(nullable = false, unique = true, length = 36)
private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "total_flashcards")
    @Builder.Default
    private int totalFlashcards = 0;

    @Column(name = "correct_answers")
    @Builder.Default
    private int correctAnswers = 0;

    @Column(name = "wrong_answers")
    @Builder.Default
    private int wrongAnswers = 0;

    @Column(name = "skipped")
    @Builder.Default
    private int skipped = 0;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SessionStatus status = SessionStatus.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SessionType type;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
package com.learnwords.deckservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "flashcard")
public class Flashcard {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "word_id", nullable = false, length = 36)
    private String wordId;

    @Column(name = "correct_answers")
    @Builder.Default
    private int correctAnswers = 0;

    @Column(name = "total_attempts")
    @Builder.Default
    private int totalAttempts = 0;

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(name = "is_learned", nullable = false)
    @Builder.Default
    private boolean isLearned = false;

    @Column(name = "is_skipped", nullable = false)
    @Builder.Default
    private boolean isSkipped = false;

    @Column(name = "algorithm_state", columnDefinition = "jsonb", nullable = false)
    @Type(JsonBinaryType.class)
    @Builder.Default
    private String algorithmState = "{}";

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
package com.learnwords.deckservice.entity;


import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.Language;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "deck")
public class Deck {
@Id
@Column(nullable = false, unique = true, length = 36)
private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = true, length = 255)
    private String description;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @OneToMany(mappedBy = "deck",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true  )
    private Set<Flashcard> flashcards = new HashSet<>();

    @OneToMany(mappedBy = "deck", fetch = FetchType.LAZY)
    private Set<Session> sessions = new HashSet<>();

    @Column(name = "how_many_flashcards_for_one_session")
    @Builder.Default
    private Long howManyFlashcardsForOneSession = 20L;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @Column(name = "word_count", nullable = false)
    @Builder.Default
    private int wordCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "learn_algorithm", nullable = false)
    private LearnAlgorithm learnAlgorithm;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_from", nullable = false)
    private Language languageFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_to", nullable = false)
    private Language languageTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner", nullable = false)
    private DeckOwner owner;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}