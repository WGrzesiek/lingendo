package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.facade.review.ReviewHeader;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.facade.ReviewViewFacade;
import com.learnwords.deckservice.service.UserProgressService;
import com.learnwords.deckservice.service.evaluationService.responseResult.AlgorithmResult;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1")
@Tag(
        name = "User Progress Management",
        description = "API do zarządzania postępami użytkownika na fiszkach"
)
public class UserProgressController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final UserProgressService userProgressService;
    private final ReviewViewFacade reviewViewFacade;

    public UserProgressController(UserProgressService userProgressService, ReviewViewFacade reviewViewFacade) {
        this.reviewViewFacade = reviewViewFacade;
        this.userProgressService = userProgressService;
    }

    /**
     * Zresetowanie postępu dla konkretnej fiszki użytkownika.
     */
    @PutMapping("/flashcards/{flashcardId}/progress/reset")
    public ResponseEntity<Void> resetFlashcardProgress(
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-123")
            @PathVariable String flashcardId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Reset postępu dla fiszki {} (userId: {})", flashcardId, userId);

        userProgressService.resetFlashcardProgress(flashcardId, userId);

        log.info("Zresetowano postęp dla fiszki {} (userId: {})", flashcardId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Oznaczenie fiszki jako nauczonej / nienauczonej.
     */
    @PutMapping("/flashcards/{flashcardId}/progress/learned")
    public ResponseEntity<Void> markAsLearned(
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-123")
            @PathVariable String flashcardId,
            @Parameter(description = "Czy fiszka jest nauczona", required = true, example = "true")
            @RequestParam boolean learned,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Ustawianie flagi learned={} dla fiszki {} (userId: {})",
                learned, flashcardId, userId);

        userProgressService.markAsLearned(flashcardId, learned, userId);

        log.info("Ustawiono learned={} dla fiszki {} (userId: {})",
                learned, flashcardId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Oznaczenie fiszki jako pominiętej / niepominiętej.
     */
    @PutMapping("/flashcards/{flashcardId}/progress/skipped")
    public ResponseEntity<Void> markAsSkipped(
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-123")
            @PathVariable String flashcardId,
            @Parameter(description = "Czy fiszka jest pominięta", required = true, example = "true")
            @RequestParam boolean skipped,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Ustawianie flagi skipped={} dla fiszki {} (userId: {})",
                skipped, flashcardId, userId);

        userProgressService.markAsSkipped(flashcardId, skipped, userId);

        log.info("Ustawiono skipped={} dla fiszki {} (userId: {})",
                skipped, flashcardId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Pobranie postępu użytkownika dla wszystkich fiszek w danej talii.
     */
    @GetMapping("/decks/{deckId}/progress")
    public ResponseEntity<List<UserFlashcardProgressDto>> getProgressForDeck(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie postępu dla talii {} (userId: {})", deckId, userId);

        List<UserFlashcardProgressDto> progressList =
                userProgressService.getProgressForDeck(deckId, userId);

        log.info("Pobrano {} wpisów postępu dla talii {} (userId: {})",
                progressList.size(), deckId, userId);
        return ResponseEntity.ok(progressList);
    }

    /**
     * Pobranie fiszek do powtórki (due) dla danego zapisu (enrollment).
     */
    @GetMapping("/enrollments/{enrollmentId}/due-flashcards")
    public ResponseEntity<List<UserFlashcardProgressDto>> getDueFlashcards(
            @Parameter(description = "ID zapisu na talię (enrollment)", required = true, example = "enrollment-123")
            @PathVariable String enrollmentId,
            @Parameter(description = "Maksymalna liczba fiszek do zwrócenia", example = "20")
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "ID użytkownika z nagłówka (do logów / weryfikacji)", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie fiszek do powtórki (due) dla enrollment {} z limitem {} (userId: {})",
                enrollmentId, limit, userId);

        List<UserFlashcardProgressDto> dueFlashcards =
                userProgressService.getDueFlashcards(enrollmentId, limit);

        log.info("Pobrano {} fiszek do powtórki dla enrollment {} (userId: {})",
                dueFlashcards.size(), enrollmentId, userId);
        return ResponseEntity.ok(dueFlashcards);
    }

    /**
     * Pobranie postępu dla konkretnej fiszki.
     */
    @GetMapping("/flashcards/{flashcardId}/progress")
    public ResponseEntity<UserFlashcardProgressDto> getFlashcardProgress(
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-123")
            @PathVariable String flashcardId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie postępu dla fiszki {} (userId: {})", flashcardId, userId);

        UserFlashcardProgressDto progress =
                userProgressService.getFlashcardProgress(flashcardId, userId);

        log.info("Pobrano postęp dla fiszki {} (userId: {})", flashcardId, userId);
        return ResponseEntity.ok(progress);
    }

    /**
     * Aktualizacja postępu fiszki na podstawie wyniku algorytmu.
     *
     * Uwaga: flashcardId w ścieżce jest głównie do logów / walidacji spójności
     * z tym, co jest w samym DTO.
     */
    @PutMapping("/flashcards/{flashcardId}/progress")
    public ResponseEntity<Void> updateProgress(
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-123")
            @PathVariable String flashcardId,
            @Parameter(description = "Dane postępu fiszki", required = true)
            @RequestBody UserFlashcardProgressDto progressDto,
            @Parameter(description = "Wynik algorytmu nauki", required = true, example = "PROMOTE")
            @RequestParam AlgorithmResult result,
            @Parameter(description = "Czy odpowiedź użytkownika była poprawna", required = true, example = "true")
            @RequestParam boolean isCorrect,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Aktualizacja postępu fiszki {} (userId: {}, result: {}, isCorrect: {}, dto: {})",
                flashcardId, userId, result, isCorrect, progressDto);

        userProgressService.updateProgress(progressDto, result, isCorrect);

        log.info("Zaktualizowano postęp fiszki {} (userId: {}, result: {}, isCorrect: {})",
                flashcardId, userId, result, isCorrect);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/enrollments/{enrollmentId}/review-header")
    public ResponseEntity<ReviewHeader> getReviewHeader(
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId,
        @Parameter(description = "ID zapisu na talię (enrollment)", required = true, example = "enrollment-123")
        @PathVariable String enrollmentId)
    {
        log.info("Pobieranie nagłówka powtórki dla enrollment {} (userId: {})",
                enrollmentId, userId);
        ReviewHeader reviewHeader = reviewViewFacade.getReviewHeader(enrollmentId, userId);
        log.info("Pobrano nagłówek powtórki dla enrollment {} (userId: {})",
                enrollmentId, userId);
        return ResponseEntity.ok(reviewHeader);
    }
}
