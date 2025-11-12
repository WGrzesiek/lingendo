package com.learnwords.deckservice.service.Session;

import com.learnwords.deckservice.dto.SessionDto;
import com.learnwords.deckservice.dto.SessionStatsDto;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;

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
