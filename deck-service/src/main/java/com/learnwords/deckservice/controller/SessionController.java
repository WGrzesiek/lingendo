package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.*;
import com.learnwords.deckservice.exception.exceptions.InvalidFlashcardIdException;
import com.learnwords.deckservice.exception.exceptions.InvalidSessionIdException;
import com.learnwords.deckservice.exception.exceptions.SessionNotActiveException;
import com.learnwords.deckservice.exception.exceptions.SessionNotFoundException;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.Session.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Kontroler REST API do zarządzania sesjami nauki.
 * 
 * <p>Ten kontroler udostępnia kompletny zestaw endpointów do zarządzania pełnym cyklem życia
 * sesji nauki, od inicjalizacji przez rejestrację odpowiedzi do ukończenia lub porzucenia sesji.
 * 
 * <h2>Główne funkcjonalności:</h2>
 * <ul>
 *   <li><b>Zarządzanie cyklem życia sesji:</b>
 *     <ul>
 *       <li>Inicjalizacja nowej sesji nauki z wybraną strategią fiszek</li>
 *       <li>Ukończenie sesji z zapisem czasu trwania i statystyk</li>
 *       <li>Porzucenie sesji przed ukończeniem</li>
 *       <li>Wstrzymanie i wznowienie sesji</li>
 *     </ul>
 *   </li>
 *   <li><b>Rejestracja postępów:</b>
 *     <ul>
 *       <li>Rejestracja odpowiedzi użytkownika (poprawne/błędne)</li>
 *       <li>Automatyczna aktualizacja statystyk sesji i fiszek</li>
 *       <li>Śledzenie postępu w czasie rzeczywistym</li>
 *     </ul>
 *   </li>
 *   <li><b>Pobieranie danych:</b>
 *     <ul>
 *       <li>Szczegóły pojedynczej sesji</li>
 *       <li>Historia sesji użytkownika</li>
 *       <li>Sesje dla konkretnej talii</li>
 *       <li>Aktywna sesja użytkownika dla talii</li>
 *     </ul>
 *   </li>
 *   <li><b>Statystyki i analityka:</b>
 *     <ul>
 *       <li>Szczegółowe statystyki sesji (correct/wrong answers, accuracy)</li>
 *       <li>Procent ukończenia sesji</li>
 *       <li>Czas trwania sesji</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h2>Endpointy według kategorii:</h2>
 * 
 * <h3>Cykl życia sesji:</h3>
 * <ul>
 *   <li>POST /api/v1/sessions/{deckId}/initialize - Zainicjuj nową sesję</li>
 *   <li>PUT /api/v1/sessions/{sessionId}/complete - Ukończ sesję</li>
 *   <li>PUT /api/v1/sessions/{sessionId}/abandon - Porzuć sesję</li>
 *   <li>PUT /api/v1/sessions/{sessionId}/pause - Wstrzymaj sesję</li>
 *   <li>PUT /api/v1/sessions/{sessionId}/resume - Wznów sesję</li>
 * </ul>
 * 
 * <h3>Odpowiedzi użytkownika:</h3>
 * <ul>
 *   <li>POST /api/v1/sessions/{sessionId}/answer - Zarejestruj odpowiedź na fiszkę</li>
 * </ul>
 * 
 * <h3>Pobieranie sesji:</h3>
 * <ul>
 *   <li>GET /api/v1/sessions/{sessionId} - Pobierz szczegóły sesji</li>
 *   <li>GET /api/v1/sessions/user - Pobierz wszystkie sesje użytkownika</li>
 *   <li>GET /api/v1/sessions/deck/{deckId} - Pobierz sesje dla talii</li>
 *   <li>GET /api/v1/sessions/active - Pobierz aktywną sesję użytkownika dla talii</li>
 * </ul>
 * 
 * <h3>Statystyki:</h3>
 * <ul>
 *   <li>GET /api/v1/sessions/{sessionId}/stats - Pobierz statystyki sesji</li>
 *   <li>GET /api/v1/sessions/{sessionId}/progress - Pobierz postęp sesji</li>
 * </ul>
 * 
 * <h2>Statusy sesji:</h2>
 * <ul>
 *   <li><b>IN_PROGRESS:</b> Sesja aktywna, użytkownik odpowiada na fiszki</li>
 *   <li><b>COMPLETED:</b> Sesja zakończona pomyślnie</li>
 *   <li><b>ABANDONED:</b> Sesja porzucona przed ukończeniem</li>
 *   <li><b>PAUSED:</b> Sesja wstrzymana, można wznowić</li>
 * </ul>
 * 
 * <h2>Autoryzacja:</h2>
 * <p>Wszystkie endpointy wymagają nagłówka <code>X-User-Id</code> z ID użytkownika.
 * Użytkownik może wykonywać operacje tylko na swoich sesjach.
 * 
 * <h2>Obsługa błędów:</h2>
 * <p>Kontroler korzysta z {@link com.learnwords.deckservice.exception.GlobalExceptionHandler}
 * do centralnej obsługi wyjątków. Możliwe kody odpowiedzi:
 * <ul>
 *   <li><b>200 OK:</b> Operacja wykonana pomyślnie</li>
 *   <li><b>201 Created:</b> Sesja utworzona (POST /initialize)</li>
 *   <li><b>400 Bad Request:</b> Błędne dane wejściowe lub brak wymaganych parametrów</li>
 *   <li><b>403 Forbidden:</b> Brak uprawnień do sesji</li>
 *   <li><b>404 Not Found:</b> Sesja nie znaleziona</li>
 *   <li><b>409 Conflict:</b> Sesja nie jest w odpowiednim statusie dla danej operacji</li>
 *   <li><b>500 Internal Server Error:</b> Nieoczekiwany błąd serwera</li>
 * </ul>
 * 
 * <h2>Walidacja:</h2>
 * <p>Wszystkie DTO używane w requestach są walidowane za pomocą Jakarta Bean Validation.
 * Błędy walidacji są zwracane jako szczegółowa mapa pól z komunikatami błędów.
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
 * @since 2025-11-15
 * @see SessionService
 * @see SessionDto
 * @see SessionDetailDto
 * @see SessionStatsDto
 * @see com.learnwords.deckservice.exception.GlobalExceptionHandler
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sessions")
@Tag(name = "Session Management", description = "API do zarządzania sesjami nauki")
public class SessionController {

    private static final String USER_ID_HEADER = "X-User-Id";
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Inicjalizuje nową sesję nauki dla talii.
     * 
     * <p>Tworzy nową sesję z wybraną strategią pobierania fiszek:
     * <ul>
     *   <li>Wszystkie fiszki z talii</li>
     *   <li>Tylko niewyuczone fiszki</li>
     *   <li>Fiszki do powtórki</li>
     *   <li>Losowe fiszki</li>
     * </ul>
     * 
     * <p>Sesja jest tworzona w statusie IN_PROGRESS i automatycznie przypisana do użytkownika.
     * 
     * @param deckId ID talii, dla której tworzona jest sesja
     * @param flashcardFetchStrategy strategia wyboru fiszek do sesji
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return ID utworzonej sesji
     * @throws IllegalArgumentException jeśli deckId jest pusty lub brak userId
     * @throws com.learnwords.deckservice.exception.exceptions.DeckNotFoundException jeśli talia nie istnieje
     * @throws com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     * @throws com.learnwords.deckservice.exception.exceptions.NoFlashcardsAvailableException jeśli brak dostępnych fiszek do nauki (talia pusta, wszystkie fiszki na max poziomie lub nie pasują do strategii)
     */
    @Operation(
        summary = "Zainicjuj nową sesję nauki",
        description = "Tworzy nową sesję nauki dla talii z wybraną strategią wyboru fiszek. Sesja jest przypisana do użytkownika."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Sesja utworzona pomyślnie",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Błędne dane wejściowe, brak wymaganego nagłówka lub brak dostępnych fiszek do nauki",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Talia nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do talii",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PostMapping("/{deckId}/initialize")
    public ResponseEntity<String> initializeSession(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "Strategia wyboru fiszek", required = true) @Valid @RequestBody FlashcardFetchStrategy flashcardFetchStrategy,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Inicjalizacja sesji - deckId: '{}', userId: '{}', strategy: '{}'", 
                deckId, userId, flashcardFetchStrategy.getClass().getSimpleName());
        
        String sessionId = sessionService.initializeSession(deckId, flashcardFetchStrategy, userId);
        
        log.info("Zainicjalizowano sesję - sessionId: '{}', deckId: '{}', userId: '{}'", sessionId, deckId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionId);
    }

    /**
     * Ukończa aktywną sesję nauki.
     * 
     * <p>Zmienia status sesji na COMPLETED i zapisuje:
     * <ul>
     *   <li>Czas ukończenia</li>
     *   <li>Całkowity czas trwania sesji w sekundach</li>
     *   <li>Finalne statystyki odpowiedzi</li>
     * </ul>
     * 
     * <p>Sesja musi być w statusie IN_PROGRESS.
     * 
     * @param sessionId ID sesji do ukończenia
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie ukończenia sesji
     * @throws InvalidSessionIdException jeśli sessionId jest pusty
     * @throws SessionNotFoundException jeśli sesja nie istnieje
     * @throws SessionNotActiveException jeśli sesja nie jest w statusie IN_PROGRESS
     * @throws com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Ukończ sesję nauki",
        description = "Kończy aktywną sesję nauki, zapisując czas ukończenia i statystyki. Sesja musi być w statusie IN_PROGRESS."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sesja ukończona pomyślnie"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Sesja nie jest aktywna",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{sessionId}/complete")
    public ResponseEntity<Void> completeSession(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Ukończanie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        sessionService.completeSession(sessionId, userId);
        
        log.info("Ukończono sesję - sessionId: '{}', userId: '{}'", sessionId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Porzuca aktywną sesję nauki.
     * 
     * <p>Zmienia status sesji na ABANDONED. Statystyki są zachowane, ale sesja
     * nie jest liczona jako pomyślnie ukończona. Przydatne gdy użytkownik
     * przerywa naukę przed ukończeniem wszystkich fiszek.
     * 
     * <p>Sesja musi być w statusie IN_PROGRESS.
     * 
     * @param sessionId ID sesji do porzucenia
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie porzucenia sesji
     * @throws InvalidSessionIdException jeśli sessionId jest pusty
     * @throws SessionNotFoundException jeśli sesja nie istnieje
     * @throws SessionNotActiveException jeśli sesja nie jest w statusie IN_PROGRESS
     * @throws com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Porzuć sesję nauki",
        description = "Przerywa aktywną sesję przed ukończeniem. Statystyki są zachowane, ale sesja nie jest liczona jako ukończona."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sesja porzucona pomyślnie"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Sesja nie jest aktywna",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{sessionId}/abandon")
    public ResponseEntity<Void> abandonSession(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Porzucanie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        sessionService.abandonSession(sessionId, userId);
        
        log.info("Porzucono sesję - sessionId: '{}', userId: '{}'", sessionId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Wstrzymuje aktywną sesję nauki.
     * 
     * <p>Zmienia status sesji z IN_PROGRESS na PAUSED. Użytkownik może
     * wznowić sesję później za pomocą endpointu /resume. Przydatne gdy
     * użytkownik chce zrobić przerwę bez porzucania postępu.
     * 
     * @param sessionId ID sesji do wstrzymania
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie wstrzymania sesji
     * @throws InvalidSessionIdException jeśli sessionId jest pusty
     * @throws SessionNotFoundException jeśli sesja nie istnieje
     * @throws com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Wstrzymaj sesję nauki",
        description = "Wstrzymuje aktywną sesję. Można ją wznowić później za pomocą /resume."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sesja wstrzymana pomyślnie"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{sessionId}/pause")
    public ResponseEntity<Void> pauseSession(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Wstrzymywanie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        sessionService.pauseSession(sessionId, userId);
        
        log.info("Wstrzymano sesję - sessionId: '{}', userId: '{}'", sessionId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Wznawia wstrzymaną sesję nauki.
     * 
     * <p>Zmienia status sesji z PAUSED z powrotem na IN_PROGRESS,
     * umożliwiając kontynuację nauki od miejsca, w którym została przerwana.
     * 
     * @param sessionId ID sesji do wznowienia
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie wznowienia sesji
     * @throws InvalidSessionIdException jeśli sessionId jest pusty
     * @throws SessionNotFoundException jeśli sesja nie istnieje
     * @throws com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Wznów sesję nauki",
        description = "Wznawia wstrzymaną sesję, umożliwiając kontynuację nauki."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sesja wznowiona pomyślnie"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{sessionId}/resume")
    public ResponseEntity<Void> resumeSession(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Wznawianie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        sessionService.resumeSession(sessionId, userId);
        
        log.info("Wznowiono sesję - sessionId: '{}', userId: '{}'", sessionId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Rejestruje odpowiedź użytkownika na fiszkę w sesji.
     * 
     * <p>Zapisuje czy odpowiedź była poprawna i aktualizuje:
     * <ul>
     *   <li>Statystyki sesji (correctAnswers/wrongAnswers)</li>
     *   <li>Statystyki fiszki (totalAttempts, correctAnswers)</li>
     *   <li>Stan algorytmu nauki fiszki</li>
     * </ul>
     * 
     * <p>Sesja musi być w statusie IN_PROGRESS.
     * 
     * @param sessionId ID sesji
     * @param request request zawierający ID fiszki i informację o poprawności odpowiedzi
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie rejestracji odpowiedzi
     * @throws InvalidSessionIdException jeśli sessionId jest pusty
     * @throws InvalidFlashcardIdException jeśli flashcardId jest pusty
     * @throws SessionNotFoundException jeśli sesja nie istnieje
     * @throws com.learnwords.deckservice.exception.exceptions.FlashcardNotFoundException jeśli fiszka nie istnieje
     * @throws SessionNotActiveException jeśli sesja nie jest w statusie IN_PROGRESS
     * @throws com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Zarejestruj odpowiedź na fiszkę",
        description = "Zapisuje odpowiedź użytkownika (poprawna/błędna) i aktualizuje statystyki sesji oraz fiszki."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Odpowiedź zarejestrowana pomyślnie"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji lub fiszki",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja lub fiszka nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Sesja nie jest aktywna",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<Void> recordAnswer(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "Request zawierający ID fiszki i informację o poprawności odpowiedzi", required = true) @Valid @RequestBody RecordAnswerRequest request,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Rejestrowanie odpowiedzi - sessionId: '{}', flashcardId: '{}', isCorrect: {}, userId: '{}'",
                sessionId, request.flashcardId(), request.isCorrect(), userId);

        sessionService.recordAnswer(sessionId, request.flashcardId(), request.isCorrect(), userId);

        log.info("Zarejestrowano odpowiedź - sessionId: '{}', flashcardId: '{}', isCorrect: {}",
                sessionId, request.flashcardId(), request.isCorrect());
        return ResponseEntity.ok().build();
    }

    /**
     * Pobiera szczegółowe informacje o sesji.
     * 
     * <p>Zwraca kompletne dane sesji, w tym:
     * <ul>
     *   <li>ID sesji i użytkownika</li>
     *   <li>ID powiązanej talii</li>
     *   <li>Status sesji (IN_PROGRESS, COMPLETED, ABANDONED, PAUSED)</li>
     *   <li>Typ sesji (LEARNING, REVIEW, TEST)</li>
     *   <li>Statystyki odpowiedzi</li>
     *   <li>Znaczniki czasowe (utworzenie, ukończenie)</li>
     *   <li>Czas trwania sesji</li>
     * </ul>
     * 
     * @param sessionId ID sesji
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return szczegółowe dane sesji
     * @throws InvalidSessionIdException jeśli sessionId jest pusty
     * @throws SessionNotFoundException jeśli sesja nie istnieje
     * @throws com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Pobierz szczegóły sesji",
        description = "Pobiera kompletne informacje o sesji, w tym status, statystyki i metadane."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Szczegóły sesji pobrane pomyślnie",
            content = @Content(schema = @Schema(implementation = SessionDetailDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDetailDto> getSessionById(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie szczegółów sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        SessionDetailDto session = sessionService.getSessionById(sessionId, userId);
        
        log.info("Pobrano szczegóły sesji - sessionId: '{}', status: '{}'", sessionId, session.status());
        return ResponseEntity.ok(session);
    }

    /**
     * Pobiera wszystkie sesje użytkownika.
     * 
     * <p>Zwraca listę wszystkich sesji utworzonych przez użytkownika,
     * niezależnie od statusu i talii. Lista jest sortowana według daty utworzenia.
     * 
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return lista sesji użytkownika (może być pusta)
     * @throws IllegalArgumentException jeśli userId jest pusty
     */
    @Operation(
        summary = "Pobierz sesje użytkownika",
        description = "Pobiera wszystkie sesje użytkownika, niezależnie od statusu i talii."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista sesji pobrana pomyślnie",
            content = @Content(schema = @Schema(implementation = SessionDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Brak wymaganego nagłówka",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/user")
    public ResponseEntity<List<SessionDto>> getSessionsByUserId(
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie sesji użytkownika - userId: '{}'", userId);
        
        List<SessionDto> sessions = sessionService.getSessionsByUserId(userId);
        
        log.info("Pobrano sesje użytkownika - userId: '{}', count: {}", userId, sessions.size());
        return ResponseEntity.ok(sessions);
    }

    /**
     * Pobiera wszystkie sesje dla talii danego użytkownika.
     * 
     * <p>Zwraca listę wszystkich sesji utworzonych dla danej talii przez użytkownika,
     * niezależnie od statusu. Lista jest sortowana według daty utworzenia.
     * 
     * @param deckId ID talii
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return lista sesji dla talii (może być pusta)
     * @throws IllegalArgumentException jeśli deckId jest pusty
     */
    @Operation(
        summary = "Pobierz sesje dla talii",
        description = "Pobiera wszystkie sesje użytkownika dla konkretnej talii."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista sesji pobrana pomyślnie",
            content = @Content(schema = @Schema(implementation = SessionDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID talii lub brak nagłówka",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/deck/{deckId}")
    public ResponseEntity<List<SessionDto>> getSessionsByDeckId(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie sesji talii - deckId: '{}', userId: '{}'", deckId, userId);
        
        List<SessionDto> sessions = sessionService.getSessionsByDeckId(deckId, userId);
        
        log.info("Pobrano sesje talii - deckId: '{}', userId: '{}', count: {}", deckId, userId, sessions.size());
        return ResponseEntity.ok(sessions);
    }

    /**
     * Pobiera aktywną sesję użytkownika dla talii.
     * 
     * <p>Wyszukuje sesję w statusie IN_PROGRESS dla danego użytkownika i talii.
     * Jeśli taka sesja istnieje, użytkownik może ją kontynuować zamiast tworzyć nową.
     * 
     * <p>Zwraca 200 z pustym Optional jeśli brak aktywnej sesji.
     * 
     * @param deckId ID talii
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return Optional z aktywną sesją jeśli istnieje, pusty Optional w przeciwnym razie
     * @throws IllegalArgumentException jeśli deckId jest pusty
     */
    @Operation(
        summary = "Pobierz aktywną sesję użytkownika dla talii",
        description = "Wyszukuje aktywną sesję (IN_PROGRESS) użytkownika dla danej talii. Zwraca pustą odpowiedź jeśli brak."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Aktywna sesja znaleziona lub brak aktywnej sesji",
            content = @Content(schema = @Schema(implementation = SessionDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID talii lub brak nagłówka",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/active")
    public ResponseEntity<Optional<SessionDto>> getActiveSession(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @RequestParam String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Sprawdzanie aktywnej sesji - deckId: '{}', userId: '{}'", deckId, userId);
        
        Optional<SessionDto> activeSession = sessionService.getActiveSessionByUserAndDeck(userId, deckId);
        
        if (activeSession.isPresent()) {
            log.info("Znaleziono aktywną sesję - deckId: '{}', userId: '{}', sessionId: '{}'", 
                    deckId, userId, activeSession.get().id());
        } else {
            log.debug("Brak aktywnej sesji - deckId: '{}', userId: '{}'", deckId, userId);
        }
        
        return ResponseEntity.ok(activeSession);
    }

    /**
     * Pobiera statystyki sesji.
     * 
     * <p>Zwraca szczegółowe statystyki sesji:
     * <ul>
     *   <li>Liczba poprawnych i błędnych odpowiedzi</li>
     *   <li>Liczba pominiętych fiszek</li>
     *   <li>Procent poprawności (accuracy)</li>
     *   <li>Procent ukończenia sesji</li>
     *   <li>Całkowita liczba fiszek w sesji</li>
     * </ul>
     * 
     * @param sessionId ID sesji
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return statystyki sesji
     * @throws InvalidSessionIdException jeśli sessionId jest pusty
     * @throws SessionNotFoundException jeśli sesja nie istnieje
     * @throws com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Pobierz statystyki sesji",
        description = "Pobiera szczegółowe statystyki sesji: poprawne/błędne odpowiedzi, accuracy, progress."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statystyki pobrane pomyślnie",
            content = @Content(schema = @Schema(implementation = SessionStatsDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/{sessionId}/stats")
    public ResponseEntity<SessionStatsDto> getSessionStats(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie statystyk sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        SessionStatsDto stats = sessionService.getSessionStats(sessionId, userId);
        
        log.info("Pobrano statystyki sesji - sessionId: '{}', correct: {}, wrong: {}", 
                sessionId, stats.correctAnswers(), stats.wrongAnswers());
        return ResponseEntity.ok(stats);
    }

    /**
     * Pobiera postęp sesji jako procent ukończenia.
     * 
     * <p>Oblicza procent ukończenia sesji na podstawie liczby odpowiedzi:
     * {@code (correctAnswers + wrongAnswers) / totalFlashcards * 100}
     * 
     * <p>Wartość jest zaokrąglona do dwóch miejsc po przecinku.
     * 
     * @param sessionId ID sesji
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return procent ukończenia sesji (0.0 - 100.0)
     * @throws InvalidSessionIdException jeśli sessionId jest pusty
     * @throws SessionNotFoundException jeśli sesja nie istnieje
     * @throws com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Pobierz postęp sesji",
        description = "Pobiera procent ukończenia sesji (0.0 - 100.0) na podstawie liczby odpowiedzi."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Postęp sesji pobrany pomyślnie",
            content = @Content(schema = @Schema(implementation = Double.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/{sessionId}/progress")
    public ResponseEntity<Double> getSessionProgress(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie postępu sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        double progress = sessionService.getSessionProgress(sessionId, userId);
        
        log.info("Pobrano postęp sesji - sessionId: '{}', progress: {}%", sessionId, progress);
        return ResponseEntity.ok(progress);
    }
}
