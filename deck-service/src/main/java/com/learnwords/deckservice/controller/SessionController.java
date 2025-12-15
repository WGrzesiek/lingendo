package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.facade.course.SessionProgress;
import com.learnwords.deckservice.dto.facade.learn.LearnHeader;
import com.learnwords.deckservice.dto.session.SessionDto;

import com.learnwords.deckservice.enums.SessionType;
import com.learnwords.deckservice.facade.CourseViewFacade;
import com.learnwords.deckservice.facade.LearnViewFacade;
import com.learnwords.deckservice.service.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.SessionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/decks")
@Tag(
        name = "Session Management",
        description = "API do zarządzania sesjami nauki"
)
public class SessionController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final SessionService sessionService;
    private final CourseViewFacade courseViewFacade;
    private final LearnViewFacade learnViewFacade;


    public SessionController(SessionService sessionService, CourseViewFacade courseViewFacade, LearnViewFacade learnViewFacade) {
        this.sessionService = sessionService;
        this.courseViewFacade = courseViewFacade;
        this.learnViewFacade = learnViewFacade;
    }

    /**
     * Inicjalizacja nowej sesji dla danej talii.
     */
    @PostMapping("/{deckId}/sessions")
    public ResponseEntity<String> initializeSession(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "Strategia pobierania fiszek do sesji", required = true, example = "ALL")
            @RequestParam FlashcardFetchStrategy flashcardFetchStrategy,
            @Parameter(description = "Typ sesji", required = true, example = "LEARNING")
            @RequestParam SessionType type,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Inicjalizacja sesji dla talii {} (strategy: {}, type: {}, userId: {})",
                deckId, flashcardFetchStrategy, type, userId);

        String sessionId = sessionService.initializeSession(deckId, flashcardFetchStrategy, type, userId);

        log.info("Zainicjalizowano sesję {} dla talii {} (userId: {})", sessionId, deckId, userId);
        return ResponseEntity.ok(sessionId);
    }

    /**
     * Oznaczenie sesji jako ukończonej.
     */
    @PutMapping("/sessions/{sessionId}/complete")
    public ResponseEntity<Void> completeSession(
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Zamykanie (complete) sesji {} userId: {}",
                sessionId, userId);

        sessionService.completeSession(sessionId, userId);

        log.info("Sesja {} została oznaczona jako ukończona (userId: {})",
                sessionId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Przerwanie (porzucenie) sesji.
     */
    @PutMapping("/sessions/{sessionId}/abandon")
    public ResponseEntity<Void> abandonSession(
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Porzucanie (abandon) sesji {} userId: {}",
                sessionId, userId);

        sessionService.abandonSession(sessionId, userId);

        log.info("Sesja {} została porzucona (userId: {})",
                sessionId,  userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Wstrzymanie sesji.
     */
    @PutMapping("/sessions/{sessionId}/pause")
    public ResponseEntity<Void> pauseSession(

            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pauzowanie sesji {} userId: {}",
                sessionId, userId);

        sessionService.pauseSession(sessionId, userId);

        log.info("Sesja {} dla userId: {}",
                sessionId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Wznowienie sesji.
     */
    @PutMapping("/sessions/{sessionId}/resume")
    public ResponseEntity<Void> resumeSession(
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Wznawianie sesji {} userId: {}",
                sessionId, userId);

        sessionService.resumeSession(sessionId, userId);

        log.info("Sesja {} została wznowiona (userId: {})",
                sessionId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Pobranie szczegółów sesji.
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionDto> getSessionById(
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie sesji {} userId: {}",
                sessionId, userId);

        SessionDto sessionDto = sessionService.getSessionById(sessionId, userId);

        log.info("Pobrano sesję {} userId: {}",
                sessionId, userId);
        return ResponseEntity.ok(sessionDto);
    }

    @GetMapping("/sessions/{enrollmentId}/session-progres")
    public ResponseEntity<SessionProgress> getSessionProgress(
            @Parameter(description = "ID zapisu do talii", required = true, example = "enrollment-123")
            @PathVariable String enrollmentId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie postępu sesji dla enrollmentId: {} userId: {}",
                enrollmentId, userId);

        SessionProgress sessionProgress = courseViewFacade.getSessionProgress(enrollmentId, userId);

        log.info("Pobrano postęp sesji dla enrollmentId: {} userId: {}",
                enrollmentId, userId);
        return ResponseEntity.ok(sessionProgress);
    }

    @GetMapping("/sessions/{sessionId}/learn-header")
    public ResponseEntity<LearnHeader> getLearnHeader(
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie nagłówka nauki dla sesji {}",
                sessionId);

        LearnHeader learnHeader = learnViewFacade.getLearnHeader(userId,sessionId);

        log.info("Pobrano nagłówek nauki dla sesji {}",
                sessionId);
        return ResponseEntity.ok(learnHeader);
    }
}
