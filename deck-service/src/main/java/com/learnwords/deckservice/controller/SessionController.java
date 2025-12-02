package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.session.SessionDto;

import com.learnwords.deckservice.enums.SessionType;
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

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
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
    @PutMapping("/{deckId}/sessions/{sessionId}/complete")
    public ResponseEntity<Void> completeSession(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Zamykanie (complete) sesji {} dla talii {} (userId: {})",
                sessionId, deckId, userId);

        sessionService.completeSession(sessionId, userId, deckId);

        log.info("Sesja {} dla talii {} została oznaczona jako ukończona (userId: {})",
                sessionId, deckId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Przerwanie (porzucenie) sesji.
     */
    @PutMapping("/{deckId}/sessions/{sessionId}/abandon")
    public ResponseEntity<Void> abandonSession(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Porzucanie (abandon) sesji {} dla talii {} (userId: {})",
                sessionId, deckId, userId);

        sessionService.abandonSession(sessionId, userId, deckId);

        log.info("Sesja {} dla talii {} została porzucona (userId: {})",
                sessionId, deckId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Wstrzymanie sesji.
     */
    @PutMapping("/{deckId}/sessions/{sessionId}/pause")
    public ResponseEntity<Void> pauseSession(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pauzowanie sesji {} dla talii {} (userId: {})",
                sessionId, deckId, userId);

        sessionService.pauseSession(sessionId, userId, deckId);

        log.info("Sesja {} dla talii {} została wstrzymana (userId: {})",
                sessionId, deckId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Wznowienie sesji.
     */
    @PutMapping("/{deckId}/sessions/{sessionId}/resume")
    public ResponseEntity<Void> resumeSession(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Wznawianie sesji {} dla talii {} (userId: {})",
                sessionId, deckId, userId);

        sessionService.resumeSession(sessionId, userId, deckId);

        log.info("Sesja {} dla talii {} została wznowiona (userId: {})",
                sessionId, deckId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Pobranie szczegółów sesji.
     */
    @GetMapping("/{deckId}/sessions/{sessionId}")
    public ResponseEntity<SessionDto> getSessionById(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie sesji {} dla talii {} (userId: {})",
                sessionId, deckId, userId);

        SessionDto sessionDto = sessionService.getSessionById(sessionId, userId, deckId);

        log.info("Pobrano sesję {} dla talii {} (userId: {})",
                sessionId, deckId, userId);
        return ResponseEntity.ok(sessionDto);
    }
}
