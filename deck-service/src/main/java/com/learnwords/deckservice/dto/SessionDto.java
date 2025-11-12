package com.learnwords.deckservice.dto;

import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.enums.SessionStatus;
import com.learnwords.deckservice.enums.SessionType;

import java.time.Instant;

/**
 * DTO reprezentujące sesję nauki.
 * 
 * <p>Zawiera informacje o sesji nauki użytkownika, w tym:
 * <ul>
 *   <li>Identyfikatory sesji, talii i użytkownika</li>
 *   <li>Statystyki (poprawne/błędne odpowiedzi, pominięte)</li>
 *   <li>Status sesji (IN_PROGRESS, COMPLETED, ABANDONED)</li>
 *   <li>Typ sesji (REGULAR, REVIEW, EXAM itp.)</li>
 *   <li>Timestampy (utworzenia, ukończenia)</li>
 * </ul>
 * 
 * @param id ID sesji
 * @param deckId ID talii, z której pochodzi sesja
 * @param userId ID użytkownika prowadzącego sesję
 * @param totalFlashcards łączna liczba fiszek w sesji
 * @param correctAnswers liczba poprawnych odpowiedzi
 * @param wrongAnswers liczba błędnych odpowiedzi
 * @param skipped liczba pominiętych fiszek
 * @param durationSeconds czas trwania sesji w sekundach
 * @param completedAt czas ukończenia sesji (null jeśli w toku)
 * @param status status sesji (IN_PROGRESS, COMPLETED, ABANDONED)
 * @param type typ sesji (REGULAR, REVIEW, EXAM)
 * @param createdAt czas utworzenia sesji
 * @param updatedAt czas ostatniej aktualizacji
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 * @see Session
 * @see SessionStatsDto
 */
public record SessionDto(
        String id,
        String deckId,
        String userId,
        int totalFlashcards,
        int correctAnswers,
        int wrongAnswers,
        int skipped,
        Long durationSeconds,
        Instant completedAt,
        SessionStatus status,
        SessionType type,
        Instant createdAt,
        Instant updatedAt
) {
    
    /**
     * Tworzy SessionDto z encji Session.
     * 
     * @param session encja sesji z bazy danych
     * @return DTO z danymi sesji
     */
    public static SessionDto from(Session session) {
        return new SessionDto(
                session.getId(),
                session.getDeck().getId(),
                session.getUserId(),
                session.getTotalFlashcards(),
                session.getCorrectAnswers(),
                session.getWrongAnswers(),
                session.getSkipped(),
                session.getDurationSeconds(),
                session.getCompletedAt(),
                session.getStatus(),
                session.getType(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
