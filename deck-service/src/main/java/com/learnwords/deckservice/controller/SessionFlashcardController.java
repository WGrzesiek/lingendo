package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.ApiErrorResponse;
import com.learnwords.deckservice.dto.sessionFlashcard.SessionFlashcardDto;
import com.learnwords.deckservice.entity.SessionFlashcard;
import com.learnwords.deckservice.exception.exceptions.FlashcardNotFoundException;
import com.learnwords.deckservice.exception.exceptions.SessionNotFoundException;
import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
import com.learnwords.deckservice.service.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.SessionFlashcardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Kontroler REST API do zarządzania fiszkami w kontekście sesji nauki.
 * 
 * <p>Ten kontroler udostępnia endpointy do operacji na fiszkach w ramach konkretnej
 * sesji nauki, śledzenia postępu i zarządzania pominiętymi fiszkami.
 * 
 * <h2>Główne funkcjonalności:</h2>
 * <ul>
 *   <li><b>Zarządzanie fiszkami sesji:</b>
 *     <ul>
 *       <li>Pobieranie wszystkich fiszek przypisanych do sesji z danymi słówek (przez gRPC)</li>
 *       <li>Pobieranie postępu pojedynczej fiszki w sesji</li>
 *       <li>Pomijanie fiszek podczas sesji (skip)</li>
 *     </ul>
 *   </li>
 *   <li><b>Statystyki sesji:</b>
 *     <ul>
 *       <li>Liczba wszystkich fiszek w sesji</li>
 *       <li>Liczba fiszek, na które udzielono odpowiedzi</li>
 *       <li>Status fiszki w sesji (answeredInSession, wasCorrect)</li>
 *     </ul>
 *   </li>
 *   <li><b>Kontekst sesji:</b>
 *     <ul>
 *       <li>Fiszki mają dodatkowe pola specyficzne dla sesji (answeredInSession, wasCorrect)</li>
 *       <li>Każda fiszka pamięta kiedy została dodana do sesji (addedToSessionAt)</li>
 *       <li>Postęp z sesji wpływa na globalny postęp fiszki (correctAnswers, totalAttempts)</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h2>Endpointy według kategorii:</h2>
 * 
 * <h3>Fiszki sesji:</h3>
 * <ul>
 *   <li>GET /api/v1/sessions/{sessionId}/flashcards - Wszystkie fiszki w sesji</li>
 *   <li>GET /api/v1/sessions/{sessionId}/flashcards/{flashcardId}/progress - Postęp pojedynczej fiszki</li>
 *   <li>PUT /api/v1/sessions/{sessionId}/flashcards/{flashcardId}/skip - Pomiń fiszkę</li>
 * </ul>
 * 
 * <h3>Statystyki sesji:</h3>
 * <ul>
 *   <li>GET /api/v1/sessions/{sessionId}/flashcards/count - Liczba wszystkich fiszek w sesji</li>
 *   <li>GET /api/v1/sessions/{sessionId}/flashcards/answered-count - Liczba fiszek z odpowiedziami</li>
 * </ul>
 * 
 * <h2>Autoryzacja:</h2>
 * <p>Wszystkie endpointy wymagają nagłówka <code>X-User-Id</code> z ID użytkownika.
 * Użytkownik może operować tylko na sesjach własnych talii.
 * 
 * <h2>Obsługa błędów:</h2>
 * <p>Kontroler korzysta z {@link com.learnwords.deckservice.exception.GlobalExceptionHandler}
 * do centralnej obsługi wyjątków. Możliwe kody odpowiedzi:
 * <ul>
 *   <li><b>200 OK:</b> Operacja wykonana pomyślnie</li>
 *   <li><b>400 Bad Request:</b> Błędne dane wejściowe lub brak wymaganych parametrów</li>
 *   <li><b>403 Forbidden:</b> Brak dostępu do sesji/fiszki</li>
 *   <li><b>404 Not Found:</b> Sesja lub fiszka nie znaleziona</li>
 *   <li><b>500 Internal Server Error:</b> Nieoczekiwany błąd serwera</li>
 * </ul>
 * 
 * <h2>Logowanie:</h2>
 * <p>Kontroler loguje:
 * <ul>
 *   <li><b>DEBUG:</b> Parametry wejściowe wszystkich operacji</li>
 *   <li><b>INFO:</b> Udane operacje z kluczowymi informacjami</li>
 *   <li><b>ERROR:</b> Błędy (obsługiwane przez GlobalExceptionHandler)</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-16
 * @see SessionFlashcardService
 * @see SessionFlashcardDto
 * @see com.learnwords.deckservice.exception.GlobalExceptionHandler
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/sessions")
@Tag(name = "Session Flashcard Management", description = "API do zarządzania fiszkami w sesjach nauki")
public class SessionFlashcardController {
    private static final String USER_ID_HEADER = "X-User-Id";
    private final SessionFlashcardService sessionFlashcardService;

    public SessionFlashcardController(SessionFlashcardService sessionFlashcardService) {
        this.sessionFlashcardService = sessionFlashcardService;
    }

    @PostMapping("/{sessionId}/flashcards/populate")
    public ResponseEntity<Void> populateSessionWithFlashcards(
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID zapisu na talię (enrollment)", required = true, example = "enrollment-123")
            @RequestParam String enrollmentId,
            @Parameter(
                    description = "Strategia pobierania fiszek do sesji",
                    required = true,
                    example = "ALL"
            )
            @RequestParam FlashcardFetchStrategy flashcardFetchStrategy,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Wypełnianie sesji {} fiszkami dla enrollmentId {} strategią {} (userId: {})",
                sessionId, enrollmentId, flashcardFetchStrategy, userId);

        sessionFlashcardService.populateSessionWithFlashcards(sessionId, enrollmentId, flashcardFetchStrategy, userId);

        log.info("Sesja {} została wypełniona fiszkami dla enrollmentId {} (userId: {})",
                sessionId, enrollmentId, userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sessionId}/flashcards")
    public ResponseEntity<SessionFlashcardDto> getSessionFlashcards(
            @Parameter(description = "ID sesji", required = true, example = "session-123")
            @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie fiszek dla sesji {} (userId: {})", sessionId, userId);

        SessionFlashcardDto flashcards = sessionFlashcardService.getSessionFlashcardsWithWords(sessionId);


        log.info("Pobrano fiszki dla sesji {} (userId: {})", sessionId, userId);

        return ResponseEntity.ok(flashcards);
    }
}

