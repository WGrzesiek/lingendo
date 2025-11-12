package com.learnwords.deckservice.dto;

import com.learnwords.deckservice.entity.Session;

/**
 * DTO zawierające statystyki sesji nauki.
 * 
 * <p>Agreguje dane statystyczne o sesji w bardziej przystępnej formie
 * niż surowe liczby. Zawiera procenty, wskaźniki accuracy, postęp itp.
 * 
 * <p>Używane do:
 * <ul>
 *   <li>Wyświetlania podsumowania sesji po ukończeniu</li>
 *   <li>Monitorowania postępu podczas sesji</li>
 *   <li>Raportów i analiz nauki użytkownika</li>
 * </ul>
 * 
 * @param sessionId ID sesji
 * @param totalFlashcards łączna liczba fiszek w sesji
 * @param answeredFlashcards liczba fiszek na które udzielono odpowiedzi
 * @param correctAnswers liczba poprawnych odpowiedzi
 * @param wrongAnswers liczba błędnych odpowiedzi
 * @param skipped liczba pominiętych fiszek
 * @param accuracyPercentage procent poprawnych odpowiedzi (0-100)
 * @param progressPercentage procent ukończenia sesji (0-100)
 * @param averageTimePerFlashcard średni czas na fiszkę w sekundach (opcjonalne)
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 * @see SessionDto
 * @see Session
 */
public record SessionStatsDto(
        String sessionId,
        int totalFlashcards,
        int answeredFlashcards,
        int correctAnswers,
        int wrongAnswers,
        int skipped,
        double accuracyPercentage,
        double progressPercentage,
        Double averageTimePerFlashcard
) {
    
    /**
     * Tworzy SessionStatsDto z encji Session.
     * 
     * <p>Oblicza procenty i statystyki na podstawie surowych liczników.
     * 
     * @param session encja sesji z bazy danych
     * @return DTO ze statystykami sesji
     */
    public static SessionStatsDto from(Session session) {
        int answered = session.getCorrectAnswers() + session.getWrongAnswers();
        int total = session.getTotalFlashcards();
        
        double accuracy = answered > 0 
                ? (double) session.getCorrectAnswers() / answered * 100 
                : 0.0;
        
        double progress = total > 0 
                ? (double) answered / total * 100 
                : 0.0;
        
        Double avgTime = null;
        if (session.getDurationSeconds() != null && answered > 0) {
            avgTime = (double) session.getDurationSeconds() / answered;
        }
        
        return new SessionStatsDto(
                session.getId(),
                total,
                answered,
                session.getCorrectAnswers(),
                session.getWrongAnswers(),
                session.getSkipped(),
                Math.round(accuracy * 100.0) / 100.0,
                Math.round(progress * 100.0) / 100.0,
                avgTime
        );
    }
}
