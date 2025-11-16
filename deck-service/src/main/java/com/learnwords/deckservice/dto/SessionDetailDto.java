package com.learnwords.deckservice.dto;

import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.enums.SessionStatus;
import com.learnwords.deckservice.enums.SessionType;

import java.time.Instant;

/**
 * DTO reprezentujące szczegółowe informacje o sesji nauki.
 * 
 * <p>Rozszerza podstawowe informacje z {@link SessionDto} o dodatkowe szczegóły:
 * <ul>
 *   <li>Informacje o talii (nazwa, opis)</li>
 *   <li>Obliczone metryki (accuracy, progress)</li>
 *   <li>Dodatkowe statystyki sesji</li>
 * </ul>
 * 
 * <p>Używane gdy potrzebne są pełne informacje o sesji, np. w widoku szczegółów sesji.
 * 
 * @param id ID sesji
 * @param deckId ID talii
 * @param deckName nazwa talii
 * @param userId ID użytkownika
 * @param totalFlashcards łączna liczba fiszek w sesji
 * @param correctAnswers liczba poprawnych odpowiedzi
 * @param wrongAnswers liczba błędnych odpowiedzi
 * @param skipped liczba pominiętych fiszek
 * @param durationSeconds czas trwania sesji w sekundach
 * @param accuracy dokładność odpowiedzi (0.0 - 100.0)
 * @param progress postęp sesji (0.0 - 100.0)
 * @param completedAt czas ukończenia sesji
 * @param status status sesji
 * @param type typ sesji
 * @param createdAt czas utworzenia sesji
 * @param updatedAt czas ostatniej aktualizacji
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-15
 * @see Session
 * @see SessionDto
 */
public record SessionDetailDto(
        String id,
        String deckId,
        String deckName,
        String userId,
        int totalFlashcards,
        int correctAnswers,
        int wrongAnswers,
        int skipped,
        Long durationSeconds,
        double accuracy,
        double progress,
        Instant completedAt,
        SessionStatus status,
        SessionType type,
        Instant createdAt,
        Instant updatedAt
) {
    
    /**
     * Tworzy SessionDetailDto z encji Session.
     * 
     * @param session encja sesji z bazy danych
     * @return DTO ze szczegółowymi danymi sesji
     */
    public static SessionDetailDto from(Session session) {
        int totalAnswers = session.getCorrectAnswers() + session.getWrongAnswers();
        double accuracy = totalAnswers > 0 
                ? Math.round((double) session.getCorrectAnswers() / totalAnswers * 10000.0) / 100.0
                : 0.0;
        
        double progress = session.getTotalFlashcards() > 0
                ? Math.round((double) totalAnswers / session.getTotalFlashcards() * 10000.0) / 100.0
                : 0.0;
        
        return new SessionDetailDto(
                session.getId(),
                session.getDeck().getId(),
                session.getDeck().getName(),
                session.getUserId(),
                session.getTotalFlashcards(),
                session.getCorrectAnswers(),
                session.getWrongAnswers(),
                session.getSkipped(),
                session.getDurationSeconds(),
                accuracy,
                progress,
                session.getCompletedAt(),
                session.getStatus(),
                session.getType(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
