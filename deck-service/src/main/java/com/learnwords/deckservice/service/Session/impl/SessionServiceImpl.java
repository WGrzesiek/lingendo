package com.learnwords.deckservice.service.Session.impl;

import com.learnwords.deckservice.dto.SessionDto;
import com.learnwords.deckservice.dto.SessionStatsDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.enums.SessionStatus;
import com.learnwords.deckservice.enums.SessionType;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.repository.FlashcardRepository;
import com.learnwords.deckservice.repository.SessionRepository;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.Session.SessionFlashcardService;
import com.learnwords.deckservice.service.Session.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementacja serwisu zarządzania sesjami nauki.
 * 
 * <p>Zarządza pełnym cyklem życia sesji od inicjalizacji przez rejestrację
 * odpowiedzi do ukończenia lub porzucenia sesji.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 */
@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final DeckRepository deckRepository;
    private final SessionRepository sessionRepository;
    private final SessionFlashcardService sessionFlashcardService;
    private final FlashcardRepository flashcardRepository;


    public SessionServiceImpl(
            DeckRepository deckRepository, 
            SessionRepository sessionRepository, 
            SessionFlashcardService sessionFlashcardService,
            FlashcardRepository flashcardRepository) {
        this.deckRepository = deckRepository;
        this.sessionRepository = sessionRepository;
        this.sessionFlashcardService = sessionFlashcardService;
        this.flashcardRepository = flashcardRepository;
    }

    /**
     * Inicjalizuje nową sesję nauki dla talii.
     * 
     * <p>Proces inicjalizacji:
     * <ol>
     *   <li>Pobiera talię z bazy danych</li>
     *   <li>Tworzy sesję w statusie IN_PROGRESS</li>
     *   <li>Zapisuje sesję</li>
     *   <li>Dodaje fiszki do sesji według strategii</li>
     * </ol>
     * 
     * @param deckId ID talii
     * @param flashcardFetchStrategy strategia wyboru fiszek
     * @return ID utworzonej sesji
     * @throws RuntimeException jeśli talia nie istnieje lub błąd DB
     */
    @Override
    @Transactional
    public String initializeSession(String deckId, FlashcardFetchStrategy flashcardFetchStrategy) {
        log.info("Inicjalizacja sesji dla talii: {}", deckId);
        
        try {
            if (deckId == null || deckId.isBlank()) {
                log.error("DeckId jest null lub pusty");
                throw new IllegalArgumentException("DeckId nie może być pusty");
            }
            
            Deck deck = deckRepository.findById(deckId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono talii o id: " + deckId));
            
            int flashcardsCount = sessionFlashcardService.getTotalFlashcardsInSession(deckId);
            
            Session session = Session.builder()
                    .id(UUID.randomUUID().toString())
                    .deck(deck)
                    .userId(deck.getUserId())
                    .totalFlashcards(flashcardsCount)
                    .status(SessionStatus.IN_PROGRESS)
                    .type(SessionType.LEARNING)
                    .build();
            
            sessionRepository.save(session);
            log.debug("Utworzono sesję: {}", session.getId());
            
            sessionFlashcardService.addFlashcardsToSession(session, deck, flashcardFetchStrategy);
            log.info("Pomyślnie zainicjalizowano sesję: {}", session.getId());
            
            return session.getId();
            
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do danych podczas inicjalizacji sesji: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas inicjalizacji sesji: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas inicjalizacji sesji: " + e.getMessage(), e);
        }
    }

    /**
     * Ukończa sesję nauki.
     * 
     * <p>Proces ukończenia:
     * <ol>
     *   <li>Pobiera sesję z bazy</li>
     *   <li>Sprawdza czy sesja jest IN_PROGRESS</li>
     *   <li>Zmienia status na COMPLETED</li>
     *   <li>Ustawia completedAt na aktualny czas</li>
     *   <li>Oblicza i zapisuje czas trwania (durationSeconds)</li>
     * </ol>
     * 
     * @param sessionId ID sesji do ukończenia
     * @throws RuntimeException jeśli sesja nie istnieje, nie jest aktywna lub błąd DB
     */
    @Override
    @Transactional
    public void completeSession(String sessionId) {
        log.info("Ukończanie sesji: {}", sessionId);
        
        try {
            if (sessionId == null || sessionId.isBlank()) {
                log.error("SessionId jest null lub pusty");
                throw new IllegalArgumentException("SessionId nie może być pusty");
            }
            
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono sesji: " + sessionId));
            
            if (session.getStatus() != SessionStatus.IN_PROGRESS) {
                log.error("Sesja {} nie jest w toku. Status: {}", sessionId, session.getStatus());
                throw new RuntimeException("Sesja nie jest aktywna");
            }
            
            Instant now = Instant.now();
            session.setCompletedAt(now);
            session.setStatus(SessionStatus.COMPLETED);

            long duration = Duration.between(session.getCreatedAt(), now).getSeconds();
            session.setDurationSeconds(duration);
            
            sessionRepository.save(session);
            log.info("Sesja {} ukończona. Czas trwania: {} sekund", sessionId, duration);
            
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas ukończania sesji: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas ukończania sesji: " + e.getMessage(), e);
        }
    }

    /**
     * Porzuca sesję nauki przed ukończeniem.
     * 
     * <p>Zmienia status sesji na ABANDONED. Statystyki są zachowane
     * ale sesja nie jest liczona jako pomyślnie ukończona.
     * 
     * @param sessionId ID sesji do porzucenia
     * @throws RuntimeException jeśli sesja nie istnieje, nie jest aktywna lub błąd DB
     */
    @Override
    @Transactional
    public void abandonSession(String sessionId) {
        log.info("Porzucanie sesji: {}", sessionId);
        
        try {
            if (sessionId == null || sessionId.isBlank()) {
                log.error("SessionId jest null lub pusty");
                throw new IllegalArgumentException("SessionId nie może być pusty");
            }
            
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono sesji: " + sessionId));
            
            if (session.getStatus() != SessionStatus.IN_PROGRESS) {
                log.error("Sesja {} nie jest w toku. Status: {}", sessionId, session.getStatus());
                throw new RuntimeException("Sesja nie jest aktywna");
            }
            
            session.setStatus(SessionStatus.ABANDONED);
            session.setCompletedAt(Instant.now());
            
            sessionRepository.save(session);
            log.info("Sesja {} porzucona", sessionId);
            
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas porzucania sesji: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas porzucania sesji: " + e.getMessage(), e);
        }
    }

    /**
     * Wstrzymuje aktywną sesję nauki.
     * 
     * <p><strong>Uwaga:</strong> Obecnie brak dedykowanego statusu PAUSED w enum SessionStatus.
     * Ta metoda może być rozszerzona gdy zostanie dodany status PAUSED.
     * 
     * @param sessionId ID sesji do wstrzymania
     * @throws UnsupportedOperationException póki co nie zaimplementowane
     */
    @Override
    public void pauseSession(String sessionId) {
        // TODO: Dodać status PAUSED do SessionStatus enum
        log.warn("pauseSession() nie jest jeszcze w pełni zaimplementowane - brak statusu PAUSED");
        throw new UnsupportedOperationException("Wstrzymywanie sesji wymaga dodania statusu PAUSED");
    }

    /**
     * Wznawia wstrzymaną sesję nauki.
     * 
     * <p><strong>Uwaga:</strong> Obecnie brak dedykowanego statusu PAUSED w enum SessionStatus.
     * Ta metoda może być rozszerzona gdy zostanie dodany status PAUSED.
     * 
     * @param sessionId ID sesji do wznowienia
     * @throws UnsupportedOperationException póki co nie zaimplementowane
     */
    @Override
    public void resumeSession(String sessionId) {
        // TODO: Dodać status PAUSED do SessionStatus enum
        log.warn("resumeSession() nie jest jeszcze w pełni zaimplementowane - brak statusu PAUSED");
        throw new UnsupportedOperationException("Wznawianie sesji wymaga dodania statusu PAUSED");
    }

    /**
     * Pobiera encję sesji po ID.
     * 
     * @param sessionId ID sesji
     * @return encja sesji
     * @throws RuntimeException jeśli sesja nie istnieje lub błąd DB
     */
    @Override
    public Session getSessionById(String sessionId) {
        log.debug("Pobieranie sesji: {}", sessionId);
        
        try {
            if (sessionId == null || sessionId.isBlank()) {
                log.error("SessionId jest null lub pusty");
                throw new IllegalArgumentException("SessionId nie może być pusty");
            }
            
            return sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono sesji: " + sessionId));
                    
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania sesji: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas pobierania sesji: " + e.getMessage(), e);
        }
    }

    /**
     * Rejestruje odpowiedź użytkownika na fiszkę w sesji.
     * 
     * <p>Proces rejestracji:
     * <ol>
     *   <li>Pobiera sesję i fiszkę z bazy</li>
     *   <li>Aktualizuje liczniki sesji (correctAnswers/wrongAnswers)</li>
     *   <li>Aktualizuje statystyki fiszki (totalAttempts, correctAnswers)</li>
     *   <li>Zapisuje oba obiekty w bazie</li>
     * </ol>
     * 
     * <p>Transakcja zapewnia atomowość wszystkich aktualizacji.
     * 
     * @param sessionId ID sesji
     * @param flashcardId ID fiszki
     * @param isCorrect czy odpowiedź była poprawna
     * @throws RuntimeException jeśli sesja/fiszka nie istnieje, sesja nie jest aktywna lub błąd DB
     */
    @Override
    @Transactional
    public void recordAnswer(String sessionId, String flashcardId, boolean isCorrect) {
        log.debug("Rejestrowanie odpowiedzi dla sesji {} i fiszki {}. Poprawna: {}", 
                sessionId, flashcardId, isCorrect);
        
        try {
            if (sessionId == null || sessionId.isBlank() || flashcardId == null || flashcardId.isBlank()) {
                log.error("SessionId lub flashcardId jest null/pusty");
                throw new IllegalArgumentException("SessionId i flashcardId nie mogą być puste");
            }
            
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono sesji: " + sessionId));
            
            if (session.getStatus() != SessionStatus.IN_PROGRESS) {
                log.error("Sesja {} nie jest aktywna. Status: {}", sessionId, session.getStatus());
                throw new RuntimeException("Sesja nie jest aktywna");
            }
            
            Flashcard flashcard = flashcardRepository.findById(flashcardId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono fiszki: " + flashcardId));
            
            if (isCorrect) {
                session.setCorrectAnswers(session.getCorrectAnswers() + 1);
            } else {
                session.setWrongAnswers(session.getWrongAnswers() + 1);
            }
            
            flashcard.setTotalAttempts(flashcard.getTotalAttempts() + 1);
            if (isCorrect) {
                flashcard.setCorrectAnswers(flashcard.getCorrectAnswers() + 1);
            }
            
            sessionRepository.save(session);
            flashcardRepository.save(flashcard);
            
            log.info("Zarejestrowano odpowiedź dla fiszki {} w sesji {}. Poprawne: {}, Błędne: {}", 
                    flashcardId, sessionId, session.getCorrectAnswers(), session.getWrongAnswers());
                    
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas rejestrowania odpowiedzi: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas rejestrowania odpowiedzi: " + e.getMessage(), e);
        }
    }

    /**
     * Pobiera wszystkie sesje użytkownika.
     * 
     * @param userId ID użytkownika
     * @return lista sesji użytkownika (może być pusta)
     * @throws RuntimeException jeśli błąd DB
     */
    @Override
    public List<SessionDto> getSessionsByUserId(String userId) {
        log.debug("Pobieranie sesji dla użytkownika: {}", userId);
        
        try {
            if (userId == null || userId.isBlank()) {
                log.error("UserId jest null lub pusty");
                throw new IllegalArgumentException("UserId nie może być pusty");
            }
            
            List<Session> sessions = sessionRepository.findByUserId(userId);
            log.debug("Znaleziono {} sesji dla użytkownika {}", sessions.size(), userId);
            
            return sessions.stream()
                    .map(SessionDto::from)
                    .toList();
                    
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania sesji użytkownika: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas pobierania sesji użytkownika: " + e.getMessage(), e);
        }
    }

    /**
     * Pobiera wszystkie sesje dla talii.
     * 
     * @param deckId ID talii
     * @return lista sesji talii (może być pusta)
     * @throws RuntimeException jeśli błąd DB
     */
    @Override
    public List<SessionDto> getSessionsByDeckId(String deckId) {
        log.debug("Pobieranie sesji dla talii: {}", deckId);
        
        try {
            if (deckId == null || deckId.isBlank()) {
                log.error("DeckId jest null lub pusty");
                throw new IllegalArgumentException("DeckId nie może być pusty");
            }
            
            List<Session> sessions = sessionRepository.findByDeckId(deckId);
            log.debug("Znaleziono {} sesji dla talii {}", sessions.size(), deckId);
            
            return sessions.stream()
                    .map(SessionDto::from)
                    .toList();
                    
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania sesji talii: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas pobierania sesji talii: " + e.getMessage(), e);
        }
    }

    /**
     * Pobiera aktywną sesję użytkownika dla talii.
     * 
     * @param userId ID użytkownika
     * @param deckId ID talii
     * @return Optional z aktywną sesją jeśli istnieje
     * @throws RuntimeException jeśli błąd DB
     */
    @Override
    public Optional<SessionDto> getActiveSessionByUserAndDeck(String userId, String deckId) {
        log.debug("Sprawdzanie aktywnej sesji dla użytkownika {} i talii {}", userId, deckId);
        
        try {
            if (userId == null || userId.isBlank() || deckId == null || deckId.isBlank()) {
                log.error("UserId lub deckId jest null/pusty");
                throw new IllegalArgumentException("UserId i deckId nie mogą być puste");
            }
            
            Optional<Session> activeSession = sessionRepository
                    .findByUserIdAndDeckIdAndStatus(userId, deckId, SessionStatus.IN_PROGRESS);
            
            if (activeSession.isPresent()) {
                log.debug("Znaleziono aktywną sesję: {}", activeSession.get().getId());
            } else {
                log.debug("Brak aktywnej sesji dla użytkownika {} i talii {}", userId, deckId);
            }
            
            return activeSession.map(SessionDto::from);
                    
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania aktywnej sesji: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas sprawdzania aktywnej sesji: " + e.getMessage(), e);
        }
    }

    /**
     * Pobiera statystyki sesji.
     * 
     * @param sessionId ID sesji
     * @return DTO ze statystykami (accuracy, progress itp.)
     * @throws RuntimeException jeśli sesja nie istnieje lub błąd DB
     */
    @Override
    public SessionStatsDto getSessionStats(String sessionId) {
        log.debug("Pobieranie statystyk sesji: {}", sessionId);
        
        try {
            if (sessionId == null || sessionId.isBlank()) {
                log.error("SessionId jest null lub pusty");
                throw new IllegalArgumentException("SessionId nie może być pusty");
            }
            
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono sesji: " + sessionId));
            
            return SessionStatsDto.from(session);
                    
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania statystyk sesji: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas pobierania statystyk sesji: " + e.getMessage(), e);
        }
    }

    /**
     * Pobiera postęp sesji jako procent ukończenia.
     * 
     * <p>Oblicza: {@code (correctAnswers + wrongAnswers) / totalFlashcards * 100}
     * 
     * @param sessionId ID sesji
     * @return procent ukończenia (0.0 - 100.0)
     * @throws RuntimeException jeśli sesja nie istnieje lub błąd DB
     */
    @Override
    public double getSessionProgress(String sessionId) {
        log.debug("Obliczanie postępu sesji: {}", sessionId);
        
        try {
            if (sessionId == null || sessionId.isBlank()) {
                log.error("SessionId jest null lub pusty");
                throw new IllegalArgumentException("SessionId nie może być pusty");
            }
            
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono sesji: " + sessionId));
            
            int answered = session.getCorrectAnswers() + session.getWrongAnswers();
            int total = session.getTotalFlashcards();
            
            if (total == 0) {
                log.warn("Sesja {} nie zawiera fiszek", sessionId);
                return 0.0;
            }
            
            double progress = (double) answered / total * 100;
            progress = Math.round(progress * 100.0) / 100.0; // Zaokrąglenie do 2 miejsc
            
            log.debug("Postęp sesji {}: {}% ({}/{})", sessionId, progress, answered, total);
            return progress;
                    
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas obliczania postępu sesji: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas obliczania postępu sesji: " + e.getMessage(), e);
        }
    }
}
