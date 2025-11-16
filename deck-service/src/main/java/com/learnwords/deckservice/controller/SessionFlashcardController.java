package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.ApiErrorResponse;
import com.learnwords.deckservice.dto.SessionFlashcardDto;
import com.learnwords.deckservice.exception.exceptions.FlashcardNotFoundException;
import com.learnwords.deckservice.exception.exceptions.SessionNotFoundException;
import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
import com.learnwords.deckservice.service.Session.SessionFlashcardService;
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

    /**
     * Pobiera wszystkie fiszki przypisane do sesji nauki.
     * 
     * <p>Zwraca kompletną listę fiszek w sesji z pełnymi danymi słówek
     * pobranymi przez gRPC z Vocabulary Read Service.
     * 
     * <p>Każda fiszka zawiera:
     * <ul>
     *   <li><b>Dane podstawowe:</b> ID, wordDto (word, translation, sentences)</li>
     *   <li><b>Statystyki globalne:</b> correctAnswers, totalAttempts, isLearned, isSkipped</li>
     *   <li><b>Kontekst sesji:</b> answeredInSession, wasCorrect, addedToSessionAt</li>
     * </ul>
     * 
     * <p>Fiszki zwracane są w kolejności wybranej przez algorytm nauki
     * (ALPHABETICAL, RANDOM, GRZESIEK_ALGORITHM itp.) podczas tworzenia sesji.
     * 
     * <p>Przydatne do:
     * <ul>
     *   <li>Wyświetlania wszystkich fiszek w sesji użytkownikowi</li>
     *   <li>Sprawdzania postępu w sesji (ile fiszek, ile odpowiedzi)</li>
     *   <li>Eksportu danych sesji</li>
     * </ul>
     * 
     * @param sessionId ID sesji nauki
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return lista wszystkich fiszek w sesji z pełnymi danymi
     * @throws IllegalArgumentException jeśli sessionId jest pusty
     * @throws SessionNotFoundException jeśli sesja o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Pobierz wszystkie fiszki z sesji",
        description = "Pobiera kompletną listę fiszek przypisanych do sesji z pełnymi danymi słówek i statusem w sesji."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista fiszek w sesji pobrana pomyślnie",
            content = @Content(schema = @Schema(implementation = SessionFlashcardDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/{sessionId}/flashcards")
    public ResponseEntity<List<SessionFlashcardDto>> getSessionFlashcards(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie fiszek dla sesji o ID: {} dla użytkownika: {}", sessionId, userId);
        List<SessionFlashcardDto> flashcards = sessionFlashcardService.getSessionFlashcards(sessionId, userId);
        log.info("Znaleziono {} fiszek w sesji {} dla użytkownika {}", flashcards.size(), sessionId, userId);
        return ResponseEntity.ok(flashcards);
    }

    /**
     * Pobiera postęp pojedynczej fiszki w sesji.
     * 
     * <p>Zwraca szczegółowe informacje o fiszce w kontekście konkretnej sesji:
     * <ul>
     *   <li>Pełne dane słówka (word, translation, sentences)</li>
     *   <li>Globalne statystyki fiszki (correctAnswers, totalAttempts)</li>
     *   <li>Status w sesji (answeredInSession, wasCorrect)</li>
     *   <li>Kiedy fiszka została dodana do sesji (addedToSessionAt)</li>
     * </ul>
     * 
     * <p>Przydatne do:
     * <ul>
     *   <li>Sprawdzenia stanu pojedynczej fiszki przed jej pokazaniem</li>
     *   <li>Walidacji czy na fiszkę już odpowiedziano w tej sesji</li>
     *   <li>Pobierania aktualnego wyniku użytkownika dla fiszki</li>
     * </ul>
     * 
     * <p>Zwraca Optional, który jest:
     * <ul>
     *   <li><b>Present</b> - jeśli fiszka jest przypisana do sesji</li>
     *   <li><b>Empty</b> - jeśli fiszka nie należy do tej sesji (HTTP 404)</li>
     * </ul>
     * 
     * @param sessionId ID sesji nauki
     * @param flashcardId ID fiszki
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return postęp fiszki w sesji lub 404 jeśli fiszka nie należy do sesji
     * @throws IllegalArgumentException jeśli sessionId lub flashcardId jest pusty
     * @throws SessionNotFoundException jeśli sesja o podanym ID nie istnieje
     * @throws FlashcardNotFoundException jeśli fiszka nie należy do sesji
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Pobierz postęp fiszki w sesji",
        description = "Pobiera szczegółowe informacje o postępie pojedynczej fiszki w kontekście konkretnej sesji."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Postęp fiszki w sesji pobrany pomyślnie",
            content = @Content(schema = @Schema(implementation = SessionFlashcardDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji lub fiszki",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona lub fiszka nie należy do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/{sessionId}/flashcards/{flashcardId}/progress")
    public ResponseEntity<SessionFlashcardDto> getFlashcardProgress(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-123") @PathVariable String flashcardId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie postępu fiszki {} w sesji {} dla użytkownika: {}", flashcardId, sessionId, userId);
        Optional<SessionFlashcardDto> progress = sessionFlashcardService.getFlashcardProgress(sessionId, flashcardId, userId);
        
        if (progress.isEmpty()) {
            log.info("Fiszka {} nie należy do sesji {} dla użytkownika {}", flashcardId, sessionId, userId);
            return ResponseEntity.notFound().build();
        }
        
        log.info("Postęp fiszki {} w sesji {} pobrany pomyślnie dla użytkownika {}", flashcardId, sessionId, userId);
        return ResponseEntity.ok(progress.get());
    }

    /**
     * Pomija fiszkę w sesji nauki.
     * 
     * <p>Oznacza fiszkę jako pominiętą w kontekście bieżącej sesji.
     * Pominięta fiszka:
     * <ul>
     *   <li>Nie będzie pokazywana użytkownikowi w dalszej części sesji</li>
     *   <li>Nie liczy się do statystyk sesji (correctAnswers, totalAttempts)</li>
     *   <li>Nie wpływa na globalny status fiszki (isLearned, isSkipped pozostają niezmienione)</li>
     *   <li>Może być ponownie dodana do przyszłych sesji</li>
     * </ul>
     * 
     * <p><b>Różnica między skip w sesji a globalny isSkipped:</b>
     * <ul>
     *   <li><b>Skip w sesji</b> - tymczasowe pominięcie tylko w tej jednej sesji</li>
     *   <li><b>Global isSkipped</b> - trwałe pominięcie fiszki we wszystkich przyszłych sesjach</li>
     * </ul>
     * 
     * <p>Przydatne gdy:
     * <ul>
     *   <li>Użytkownik chce pominąć trudną fiszkę i wrócić do niej później</li>
     *   <li>Fiszka nie jest odpowiednia w kontekście obecnej sesji</li>
     *   <li>Użytkownik zna już to słówko i nie chce na nie odpowiadać</li>
     * </ul>
     * 
     * @param sessionId ID sesji nauki
     * @param flashcardId ID fiszki do pominięcia
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie pominięcia fiszki
     * @throws IllegalArgumentException jeśli sessionId lub flashcardId jest pusty
     * @throws SessionNotFoundException jeśli sesja o podanym ID nie istnieje
     * @throws FlashcardNotFoundException jeśli fiszka nie należy do sesji
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Pomiń fiszkę w sesji",
        description = "Oznacza fiszkę jako pominiętą w kontekście bieżącej sesji. Nie wpływa na globalny status fiszki."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Fiszka pominięta pomyślnie"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji lub fiszki",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona lub fiszka nie należy do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{sessionId}/flashcards/{flashcardId}/skip")
    public ResponseEntity<Void> skipFlashcard(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID fiszki do pominięcia", required = true, example = "flashcard-123") @PathVariable String flashcardId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pomijanie fiszki {} w sesji {} dla użytkownika: {}", flashcardId, sessionId, userId);
        sessionFlashcardService.skipFlashcard(sessionId, flashcardId, userId);
        log.info("Fiszka {} pominięta pomyślnie w sesji {} dla użytkownika {}", flashcardId, sessionId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Pobiera całkowitą liczbę fiszek w sesji.
     * 
     * <p>Zwraca liczbę wszystkich fiszek przypisanych do sesji,
     * niezależnie od ich statusu (answered, skipped, etc.).
     * 
     * <p>Ta liczba jest stała przez cały czas trwania sesji - fiszki są
     * wybierane przez algorytm nauki podczas tworzenia sesji i nie zmieniają się.
     * 
     * <p>Przydatne do:
     * <ul>
     *   <li>Wyświetlania paska postępu (np. "5 / 20 fiszek")</li>
     *   <li>Obliczania procentu ukończenia sesji</li>
     *   <li>Walidacji czy sesja ma fiszki do nauki</li>
     * </ul>
     * 
     * @param sessionId ID sesji nauki
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return całkowita liczba fiszek w sesji
     * @throws IllegalArgumentException jeśli sessionId jest pusty
     * @throws SessionNotFoundException jeśli sesja o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Pobierz liczbę fiszek w sesji",
        description = "Zwraca całkowitą liczbę fiszek przypisanych do sesji (niezależnie od statusu)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liczba fiszek pobrana pomyślnie",
            content = @Content(schema = @Schema(implementation = Integer.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/{sessionId}/flashcards/count")
    public ResponseEntity<Integer> getTotalFlashcardsInSession(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie liczby fiszek w sesji {} dla użytkownika: {}", sessionId, userId);
        int totalCount = sessionFlashcardService.getTotalFlashcardsInSession(sessionId, userId);
        log.info("Sesja {} zawiera {} fiszek dla użytkownika {}", sessionId, totalCount, userId);
        return ResponseEntity.ok(totalCount);
    }

    /**
     * Pobiera liczbę fiszek z odpowiedziami w sesji.
     * 
     * <p>Zwraca liczbę fiszek, na które użytkownik już udzielił odpowiedzi
     * w tej sesji (niezależnie czy poprawnej czy nie).
     * 
     * <p>Licznik zwiększa się po każdej odpowiedzi zapisanej przez endpoint
     * {@code PUT /sessions/{sessionId}/answer} z SessionController.
     * 
     * <p>Fiszki pominięte (skipped) NIE są liczone jako "answered".
     * 
     * <p>Przydatne do:
     * <ul>
     *   <li>Obliczania postępu sesji (answered / total * 100%)</li>
     *   <li>Wyświetlania licznika "Odpowiedzi: 5 / 20"</li>
     *   <li>Sprawdzania czy sesja jest ukończona (answered == total)</li>
     *   <li>Blokowania zakończenia sesji przed odpowiedzią na wszystkie fiszki</li>
     * </ul>
     * 
     * @param sessionId ID sesji nauki
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return liczba fiszek z odpowiedziami
     * @throws IllegalArgumentException jeśli sessionId jest pusty
     * @throws SessionNotFoundException jeśli sesja o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do sesji
     */
    @Operation(
        summary = "Pobierz liczbę fiszek z odpowiedziami",
        description = "Zwraca liczbę fiszek, na które użytkownik już odpowiedział w sesji (nie wliczając pominiętych)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liczba fiszek z odpowiedziami pobrana pomyślnie",
            content = @Content(schema = @Schema(implementation = Integer.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do sesji",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sesja nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/{sessionId}/flashcards/answered-count")
    public ResponseEntity<Integer> getAnsweredFlashcardsCount(
            @Parameter(description = "ID sesji", required = true, example = "session-123") @PathVariable String sessionId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie liczby fiszek z odpowiedziami w sesji {} dla użytkownika: {}", sessionId, userId);
        int answeredCount = sessionFlashcardService.getAnsweredFlashcardsCount(sessionId, userId);
        log.info("W sesji {} użytkownik {} odpowiedział na {} fiszek", sessionId, userId, answeredCount);
        return ResponseEntity.ok(answeredCount);
    }
}

