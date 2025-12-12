package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.evaluationService.AnswerResultDto;
import com.learnwords.deckservice.dto.learningStrategy.NextFlashcardRecommendation;
import com.learnwords.deckservice.service.evaluationService.TextAnswer;
import com.learnwords.deckservice.service.reviewsStrategy.ReviewsStrategy;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/decks/reviews")
@Tag(
        name = "Reviews Flow",
        description = "API zarządzające przepływem powtórek: następna fiszka do powtórki, sprawdzanie odpowiedzi w trybie powtórek"
)
public class ReviewsFlowController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final ReviewsStrategy reviewsStrategy;

    public ReviewsFlowController(ReviewsStrategy reviewsStrategy) {
        this.reviewsStrategy = reviewsStrategy;
    }

    /**
     * Pobiera kolejną rekomendowaną fiszkę do powtórki w ramach enrolmentu.
     */
    @GetMapping("/enrollments/{enrollmentId}/next")
    public ResponseEntity<NextFlashcardRecommendation> getNextReviewFlashcard(
            @Parameter(description = "ID enrolmentu (DeckEnrollment)", required = true, example = "enrollment-123")
            @PathVariable String enrollmentId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie następnej fiszki do powtórki: enrollmentId={}, userId={}",
                enrollmentId, userId);

        Optional<NextFlashcardRecommendation> recommendationOpt =
                reviewsStrategy.recommendNext(userId, enrollmentId);

        if (recommendationOpt.isEmpty()) {
            log.info("Brak fiszek do powtórki dla enrollmentId={} (userId={})",
                    enrollmentId, userId);
            return ResponseEntity.noContent().build(); // 204 – nic do powtórek
        }

        NextFlashcardRecommendation recommendation = recommendationOpt.get();

        log.info("Zwrócono rekomendację fiszki do powtórki dla enrollmentId={} (userId={})",
                enrollmentId, userId);

        return ResponseEntity.ok(recommendation);
    }

    /**
     * Wysłanie odpowiedzi użytkownika dla fiszki w trybie powtórek.
     */
    @PostMapping("/flashcards/{flashcardId}/answer")
    public ResponseEntity<AnswerResultDto> submitReviewAnswer(
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-789")
            @PathVariable String flashcardId,
            @Parameter(description = "Odpowiedź użytkownika w trybie powtórki", required = true)
            @RequestBody TextAnswer answer,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Odebrano odpowiedź w powtórce: flashcardId={}, userId={}, answer={}",
                flashcardId, userId, answer);

        AnswerResultDto result =
                reviewsStrategy.registerReviewResult(flashcardId, answer, userId);

        log.info("Przetworzono odpowiedź w powtórce dla fiszki {} dla userId={})",
                flashcardId, userId);

        return ResponseEntity.ok(result);
    }
}
