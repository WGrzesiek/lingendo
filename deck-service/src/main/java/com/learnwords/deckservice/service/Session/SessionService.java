package com.learnwords.deckservice.service.Session;

import com.learnwords.deckservice.dto.SessionDetailDto;
import com.learnwords.deckservice.dto.SessionDto;
import com.learnwords.deckservice.dto.SessionStatsDto;
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
 *   <li>PAUSED - sesja wstrzymana</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 * @see SessionDto
 * @see SessionDetailDto
 * @see SessionStatsDto
 * @see FlashcardFetchStrategy
 */
public interface SessionService {
    String initializeSession(String deckId, FlashcardFetchStrategy flashcardFetchStrategy, String userId);
    void completeSession(String sessionId, String userId);
    void abandonSession(String sessionId, String userId);
    void pauseSession(String sessionId, String userId);
    void resumeSession(String sessionId, String userId);
    SessionDetailDto getSessionById(String sessionId, String userId);
    void recordAnswer(String sessionId, String flashcardId, boolean isCorrect, String userId);
    List<SessionDto> getSessionsByUserId(String userId);
    List<SessionDto> getSessionsByDeckId(String deckId, String userId);
    Optional<SessionDto> getActiveSessionByUserAndDeck(String userId, String deckId);
    SessionStatsDto getSessionStats(String sessionId, String userId);
    double getSessionProgress(String sessionId, String userId);
}
