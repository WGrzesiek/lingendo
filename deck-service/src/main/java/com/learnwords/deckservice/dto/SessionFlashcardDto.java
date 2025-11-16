package com.learnwords.deckservice.dto;

import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.SessionFlashcard;

import java.time.Instant;

/**
 * DTO reprezentujące fiszkę w kontekście sesji nauki.
 * Zawiera informacje o fiszce oraz jej stanie podczas konkretnej sesji.
 */
public record SessionFlashcardDto(
        String id,                    // ID relacji SessionFlashcard
        String flashcardId,
        WordDto wordDto,

        int correctAnswers,
        int totalAttempts,
        boolean isLearned,
        boolean isSkipped,

        Boolean answeredInSession,    // Czy już odpowiedział w tej sesji
        Boolean wasCorrect,           // Czy odpowiedź była poprawna (null = nie odpowiedział)

        Instant addedToSessionAt
) {

    /**
     * Tworzy DTO z encji SessionFlashcard (bez danych z vocabulary-service)
     */
    public static SessionFlashcardDto from(SessionFlashcard sessionFlashcard, WordDto wordDto) {
        Flashcard flashcard = sessionFlashcard.getFlashcard();
        return new SessionFlashcardDto(
                sessionFlashcard.getId(),
                flashcard.getId(),
                wordDto,
                flashcard.getCorrectAnswers(),
                flashcard.getTotalAttempts(),
                flashcard.isLearned(),
                flashcard.isSkipped(),
                false,
                null,
                sessionFlashcard.getCreatedAt()
        );
    }
}