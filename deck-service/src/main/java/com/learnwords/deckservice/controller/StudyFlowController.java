package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.evaluationService.AnswerResultDto;
import com.learnwords.deckservice.dto.learningStrategy.NextFlashcardRecommendation;
import com.learnwords.deckservice.service.StudyFlowService;
import com.learnwords.deckservice.service.evaluationService.UserAnswer;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/decks")
@Tag(
        name = "Study Flow",
        description = "API zarządzające przepływem nauki: następna fiszka, sprawdzanie odpowiedzi"
)
public class StudyFlowController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final StudyFlowService studyFlowService;

    public StudyFlowController(StudyFlowService studyFlowService) {
        this.studyFlowService = studyFlowService;
    }

    /**
     * Pobiera kolejną rekomendowaną fiszkę do nauki w ramach sesji.
     */
    @GetMapping("/sessions/{sessionId}/next")
    public ResponseEntity<NextFlashcardRecommendation> getNextFlashcard(
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie następnej fiszki: sessionId={}, userId={}",
                sessionId, userId);

        NextFlashcardRecommendation recommendation =
                studyFlowService.getNextFlashcard(sessionId, userId);

        log.info("Zwrócono rekomendację następnej fiszki dla sessionId={} (userId={})",
                sessionId, userId);

        return ResponseEntity.ok(recommendation);
    }

    /**
     * Wysłanie odpowiedzi użytkownika dla aktualnej fiszki w sesji.
     */
    @PostMapping("/sessions/{sessionId}/flashcards/{flashcardId}/answer")
    public ResponseEntity<AnswerResultDto> submitAnswer(
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-789")
            @PathVariable String flashcardId,
            @Parameter(description = "Odpowiedź użytkownika", required = true)
            @RequestBody UserAnswer userAnswer,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Odebrano odpowiedź użytkownika: sessionId={}, flashcardId={}, userId={}, answer={}",
                sessionId, flashcardId, userId, userAnswer);

        AnswerResultDto result =
                studyFlowService.submitAnswer(sessionId, flashcardId, userAnswer, userId);

        log.info("Przetworzono odpowiedź dla fiszki {} w sesji {} (userId={})",
                flashcardId, sessionId, userId);

        return ResponseEntity.ok(result);
    }
}
