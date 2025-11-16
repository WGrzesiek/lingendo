package com.learnwords.deckservice.controller;

import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.ApiErrorResponse;
import com.learnwords.deckservice.dto.FlashcardDto;
import com.learnwords.deckservice.exception.exceptions.DeckNotFoundException;
import com.learnwords.deckservice.exception.exceptions.FlashcardNotFoundException;
import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
import com.learnwords.deckservice.service.FlashcardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler REST API do zarządzania fiszkami.
 * 
 * <p>Ten kontroler udostępnia kompletny zestaw endpointów do operacji na fiszkach
 * w ramach talii, zarządzania postępem nauki oraz aktualizacji słówek.
 * 
 * <h2>Główne funkcjonalności:</h2>
 * <ul>
 *   <li><b>Zarządzanie fiszkami:</b>
 *     <ul>
 *       <li>Pobieranie wszystkich fiszek z talii z pełnymi danymi słówek (przez gRPC)</li>
 *       <li>Filtrowanie fiszek według statusu nauki (learned/unlearned)</li>
 *       <li>Filtrowanie fiszek pominiętych (skipped)</li>
 *       <li>Aktualizacja słówka przypisanego do fiszki</li>
 *     </ul>
 *   </li>
 *   <li><b>Postęp nauki:</b>
 *     <ul>
 *       <li>Oznaczanie fiszek jako nauczone (learned)</li>
 *       <li>Oznaczanie fiszek jako pominięte (skipped)</li>
 *       <li>Resetowanie postępu nauki (correctAnswers, totalAttempts)</li>
 *       <li>Automatyczne śledzenie statystyk (poprawne/wszystkie odpowiedzi)</li>
 *     </ul>
 *   </li>
 *   <li><b>Integracje:</b>
 *     <ul>
 *       <li>Kafka - fiszki tworzone przez zdarzenia z Vocabulary Service (Outbox Pattern)</li>
 *       <li>gRPC - pobieranie pełnych danych słówek z Vocabulary Read Service</li>
 *       <li>Algorytmy nauki - automatyczna inicjalizacja stanu algorytmu dla nowych fiszek</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h2>Endpointy według kategorii:</h2>
 * 
 * <h3>Pobieranie fiszek:</h3>
 * <ul>
 *   <li>GET /api/v1/flashcards/deck/{deckId} - Wszystkie fiszki z talii</li>
 *   <li>GET /api/v1/flashcards/deck/{deckId}/filter - Fiszki z talii z filtrami</li>
 * </ul>
 * 
 * <h3>Aktualizacje fiszek:</h3>
 * <ul>
 *   <li>PUT /api/v1/flashcards/{flashcardId}/word - Zaktualizuj słówko fiszki</li>
 *   <li>PUT /api/v1/flashcards/{flashcardId}/learned - Oznacz jako nauczone</li>
 *   <li>PUT /api/v1/flashcards/{flashcardId}/skipped - Oznacz jako pominięte</li>
 *   <li>PUT /api/v1/flashcards/{flashcardId}/reset - Resetuj postęp nauki</li>
 * </ul>
 * 
 * <h2>Autoryzacja:</h2>
 * <p>Wszystkie endpointy wymagają nagłówka <code>X-User-Id</code> z ID użytkownika.
 * Użytkownik może operować tylko na fiszkach z własnych talii.
 * 
 * <h2>Obsługa błędów:</h2>
 * <p>Kontroler korzysta z {@link com.learnwords.deckservice.exception.GlobalExceptionHandler}
 * do centralnej obsługi wyjątków. Możliwe kody odpowiedzi:
 * <ul>
 *   <li><b>200 OK:</b> Operacja wykonana pomyślnie</li>
 *   <li><b>400 Bad Request:</b> Błędne dane wejściowe lub brak wymaganych parametrów</li>
 *   <li><b>403 Forbidden:</b> Brak dostępu do talii/fiszki</li>
 *   <li><b>404 Not Found:</b> Talia lub fiszka nie znaleziona</li>
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
 * @since 2025-11-16
 * @see FlashcardService
 * @see FlashcardDto
 * @see com.learnwords.deckservice.exception.GlobalExceptionHandler
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/flashcards")
@Tag(name = "Flashcard Management", description = "API do zarządzania fiszkami")
public class FlashcardController {
    private static final String USER_ID_HEADER = "X-User-Id";
    private final FlashcardService flashcardService;

    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    /**
     * Pobiera wszystkie fiszki z talii.
     * 
     * <p>Zwraca kompletną listę wszystkich fiszek należących do talii,
     * z pełnymi danymi słówek pobranymi przez gRPC z Vocabulary Read Service.
     * 
     * <p>Każda fiszka zawiera:
     * <ul>
     *   <li>ID fiszki</li>
     *   <li>Kompletne dane słówka (word, translation, sentences, etc.)</li>
     *   <li>Statystyki nauki (correctAnswers, totalAttempts)</li>
     *   <li>Statusy (isLearned, isSkipped)</li>
     *   <li>Znaczniki czasowe (createdAt, updatedAt)</li>
     * </ul>
     * 
     * @param deckId ID talii
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return lista wszystkich fiszek z talii
     * @throws IllegalArgumentException jeśli deckId jest pusty
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
        summary = "Pobierz wszystkie fiszki z talii",
        description = "Pobiera kompletną listę wszystkich fiszek z talii z pełnymi danymi słówek (przez gRPC)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista fiszek pobrana pomyślnie",
            content = @Content(schema = @Schema(implementation = FlashcardDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID talii",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do talii",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Talia nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/deck/{deckId}")
    public ResponseEntity<List<FlashcardDto>> getAllFlashcardsFromDeck(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie wszystkich fiszek z talii o ID: {} dla użytkownika: {}", deckId, userId);
        List<FlashcardDto> flashcards = flashcardService.getAllFlashcardsFromDeck(deckId, userId);
        log.info("Znaleziono {} fiszek w talii {} dla użytkownika {}", flashcards.size(), deckId, userId);
        return ResponseEntity.ok(flashcards);
    }

    /**
     * Pobiera fiszki z talii z opcjonalnymi filtrami.
     * 
     * <p>Umożliwia filtrowanie fiszek według statusów:
     * <ul>
     *   <li><b>isLearned</b> - zwraca fiszki nauczone (true) lub nienauczone (false)</li>
     *   <li><b>isSkipped</b> - zwraca fiszki pominięte (true) lub niepominięte (false)</li>
     * </ul>
     * 
     * <p>Oba parametry są opcjonalne i mogą być używane równocześnie.
     * Jeśli nie podano filtrów, zwracane są wszystkie fiszki (analogicznie jak getAllFlashcardsFromDeck).
     * 
     * <p>Przykłady użycia:
     * <ul>
     *   <li>isLearned=false - fiszki do nauki</li>
     *   <li>isLearned=true - fiszki opanowane</li>
     *   <li>isSkipped=true - fiszki pominięte</li>
     *   <li>isLearned=false&isSkipped=false - aktywne fiszki do nauki</li>
     * </ul>
     * 
     * @param deckId ID talii
     * @param isLearned filtr statusu nauczenia (opcjonalny)
     * @param isSkipped filtr statusu pominięcia (opcjonalny)
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return lista fiszek spełniających kryteria filtrowania
     * @throws IllegalArgumentException jeśli deckId jest pusty
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
        summary = "Pobierz fiszki z talii z filtrami",
        description = "Pobiera fiszki z talii z opcjonalnymi filtrami statusu nauczenia (isLearned) i pominięcia (isSkipped)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista fiszek spełniających kryteria",
            content = @Content(schema = @Schema(implementation = FlashcardDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID talii lub parametry",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do talii",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Talia nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/deck/{deckId}/filter")
    public ResponseEntity<List<FlashcardDto>> getFlashcardsFromDeckByFilter(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "Czy fiszka jest nauczona", example = "false") @RequestParam(required = false) Boolean isLearned,
            @Parameter(description = "Czy fiszka jest pominięta", example = "false") @RequestParam(required = false) Boolean isSkipped,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie fiszek z talii {} z filtrami - isLearned: {}, isSkipped: {}, userId: {}", deckId, isLearned, isSkipped, userId);
        
        // Jeśli nie podano filtrów, pobierz wszystkie fiszki
        if (isLearned == null && isSkipped == null) {
            List<FlashcardDto> flashcards = flashcardService.getAllFlashcardsFromDeck(deckId, userId);
            log.info("Znaleziono {} fiszek w talii {} (bez filtrów) dla użytkownika {}", flashcards.size(), deckId, userId);
            return ResponseEntity.ok(flashcards);
        }
        
        // Użyj wartości domyślnych false, jeśli nie podano
        boolean learned = isLearned != null ? isLearned : false;
        boolean skipped = isSkipped != null ? isSkipped : false;
        
        List<FlashcardDto> flashcards = flashcardService.getFlashcardsFromDeckByFilter(deckId, learned, skipped, userId);
        log.info("Znaleziono {} fiszek w talii {} spełniających kryteria dla użytkownika {}", flashcards.size(), deckId, userId);
        return ResponseEntity.ok(flashcards);
    }

    /**
     * Aktualizuje słówko przypisane do fiszki.
     * 
     * <p>Endpoint umożliwia zmianę słówka związanego z fiszką bez usuwania
     * postępu nauki. Przydatne gdy:
     * <ul>
     *   <li>Poprawiono błąd w słówku w Vocabulary Service</li>
     *   <li>Zaktualizowano tłumaczenie lub zdania przykładowe</li>
     *   <li>Dodano nowe formy lub wymowę</li>
     * </ul>
     * 
     * <p>Statystyki nauki (correctAnswers, totalAttempts) i statusy (learned, skipped)
     * pozostają niezmienione - aktualizowane jest tylko słówko.
     * 
     * <p>Dane słówka muszą zawierać wszystkie wymagane pola zgodnie z WordDto.
     * 
     * @param flashcardId ID fiszki do zaktualizowania
     * @param newWord nowe dane słówka z walidacją
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie aktualizacji
     * @throws IllegalArgumentException jeśli flashcardId jest pusty lub dane słówka nieprawidłowe
     * @throws FlashcardNotFoundException jeśli fiszka o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do fiszki
     */
    @Operation(
        summary = "Zaktualizuj słówko fiszki",
        description = "Aktualizuje dane słówka przypisanego do fiszki. Zachowuje postęp nauki (correctAnswers, totalAttempts, learned, skipped)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Słówko fiszki zaktualizowane pomyślnie"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID fiszki lub dane słówka",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do fiszki",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Fiszka nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{flashcardId}/word")
    public ResponseEntity<Void> updateFlashcard(
            @Parameter(description = "ID fiszki do zaktualizowania", required = true, example = "flashcard-123") @PathVariable String flashcardId,
            @Parameter(description = "Nowe dane słówka", required = true) @Valid @RequestBody WordDto newWord,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Aktualizacja słówka fiszki o ID: {} dla użytkownika: {}", flashcardId, userId);
        flashcardService.updateFlashcard(flashcardId, newWord, userId);
        log.info("Słówko fiszki {} zaktualizowane pomyślnie dla użytkownika {}", flashcardId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Resetuje postęp nauki fiszki.
     * 
     * <p>Usuwa całą historię nauki fiszki, resetując:
     * <ul>
     *   <li>correctAnswers - liczba poprawnych odpowiedzi (→ 0)</li>
     *   <li>totalAttempts - całkowita liczba prób (→ 0)</li>
     *   <li>isLearned - status nauczenia (→ false)</li>
     *   <li>Stan algorytmu nauki - inicjalizowany od nowa</li>
     * </ul>
     * 
     * <p><b>UWAGA:</b> Słówko (wordId) oraz status isSkipped pozostają niezmienione.
     * 
     * <p>Przydatne gdy:
     * <ul>
     *   <li>Użytkownik chce powtórzyć naukę od początku</li>
     *   <li>Zmieniono algorytm nauki talii</li>
     *   <li>Fiszka była błędnie oznaczona jako nauczona</li>
     * </ul>
     * 
     * @param flashcardId ID fiszki do zresetowania
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie resetu
     * @throws IllegalArgumentException jeśli flashcardId jest pusty
     * @throws FlashcardNotFoundException jeśli fiszka o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do fiszki
     */
    @Operation(
        summary = "Resetuj postęp nauki fiszki",
        description = "Resetuje statystyki nauki fiszki (correctAnswers, totalAttempts, isLearned) i inicjalizuje algorytm od nowa. Słówko i isSkipped pozostają niezmienione."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Postęp nauki zresetowany pomyślnie"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID fiszki",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do fiszki",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Fiszka nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{flashcardId}/reset")
    public ResponseEntity<Void> resetFlashcardProgress(
            @Parameter(description = "ID fiszki do zresetowania", required = true, example = "flashcard-123") @PathVariable String flashcardId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Resetowanie postępu nauki fiszki o ID: {} dla użytkownika: {}", flashcardId, userId);
        flashcardService.resetFlashcardProgress(flashcardId, userId);
        log.info("Postęp nauki fiszki {} zresetowany pomyślnie dla użytkownika {}", flashcardId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Oznacza fiszkę jako nauczoną lub nienauczoną.
     * 
     * <p>Zmienia status nauczenia fiszki, co wpływa na:
     * <ul>
     *   <li>Wyświetlanie w filtrach (learned/unlearned)</li>
     *   <li>Statystyki talii (liczba nauczonych fiszek)</li>
     *   <li>Wybór fiszek do sesji nauki przez algorytmy</li>
     *   <li>Wyświetlanie postępu użytkownika</li>
     * </ul>
     * 
     * <p><b>learned=true:</b> Fiszka oznaczana jako opanowana
     * <ul>
     *   <li>Może nie być wybierana do sesji nauki (zależy od algorytmu)</li>
     *   <li>Liczy się do statystyk nauczonych fiszek</li>
     *   <li>Może być filtrowana jako "completed"</li>
     * </ul>
     * 
     * <p><b>learned=false:</b> Fiszka wraca do nauki
     * <ul>
     *   <li>Będzie wybierana do sesji nauki</li>
     *   <li>Statystyki (correctAnswers) pozostają, ale status się zmienia</li>
     *   <li>Przydatne do ponownej nauki "zapomnianych" słówek</li>
     * </ul>
     * 
     * @param flashcardId ID fiszki
     * @param learned nowy status nauczenia (true = nauczone, false = nienauczone)
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie zmiany statusu
     * @throws IllegalArgumentException jeśli flashcardId jest pusty
     * @throws FlashcardNotFoundException jeśli fiszka o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do fiszki
     */
    @Operation(
        summary = "Oznacz fiszkę jako nauczoną/nienauczoną",
        description = "Zmienia status nauczenia fiszki (isLearned). Wpływa na filtry, statystyki i wybór do sesji nauki."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Status nauczenia zmieniony pomyślnie"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID fiszki lub parametr",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do fiszki",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Fiszka nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{flashcardId}/learned")
    public ResponseEntity<Void> markAsLearned(
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-123") @PathVariable String flashcardId,
            @Parameter(description = "Status nauczenia (true = nauczone, false = nienauczone)", required = true, example = "true") @RequestParam boolean learned,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Oznaczanie fiszki o ID: {} jako learned={} dla użytkownika: {}", flashcardId, learned, userId);
        flashcardService.markAsLearned(flashcardId, learned, userId);
        log.info("Fiszka {} oznaczona jako learned={} pomyślnie dla użytkownika {}", flashcardId, learned, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Oznacza fiszkę jako pominiętą lub niepominiętą.
     * 
     * <p>Zmienia status pominięcia fiszki, co umożliwia tymczasowe
     * ukrywanie fiszek z sesji nauki bez usuwania ich z talii.
     * 
     * <p><b>skipped=true:</b> Fiszka pominięta
     * <ul>
     *   <li>Nie jest wybierana do sesji nauki przez algorytmy</li>
     *   <li>Można ją filtrować osobno (lista pominiętych)</li>
     *   <li>Przydatne dla słówek, które użytkownik już zna lub nie chce uczyć się w tej chwili</li>
     *   <li>Statystyki nauki (correctAnswers) pozostają niezmienione</li>
     * </ul>
     * 
     * <p><b>skipped=false:</b> Fiszka wraca do aktywnej nauki
     * <ul>
     *   <li>Będzie ponownie wybierana do sesji nauki</li>
     *   <li>Wszystkie statystyki i stan algorytmu pozostają zachowane</li>
     *   <li>Użytkownik może wrócić do nauki słówka w dowolnym momencie</li>
     * </ul>
     * 
     * <p>Różnica między skipped a learned:
     * <ul>
     *   <li><b>skipped</b> - użytkownik celowo pomija słówko (może nie być zainteresowany)</li>
     *   <li><b>learned</b> - użytkownik opanował słówko (pozytywny postęp)</li>
     * </ul>
     * 
     * @param flashcardId ID fiszki
     * @param skipped nowy status pominięcia (true = pominięte, false = niepominięte)
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie zmiany statusu
     * @throws IllegalArgumentException jeśli flashcardId jest pusty
     * @throws FlashcardNotFoundException jeśli fiszka o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do fiszki
     */
    @Operation(
        summary = "Oznacz fiszkę jako pominiętą/niepominiętą",
        description = "Zmienia status pominięcia fiszki (isSkipped). Pominięte fiszki nie są wybierane do sesji nauki."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Status pominięcia zmieniony pomyślnie"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID fiszki lub parametr",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do fiszki",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Fiszka nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{flashcardId}/skipped")
    public ResponseEntity<Void> markAsSkipped(
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-123") @PathVariable String flashcardId,
            @Parameter(description = "Status pominięcia (true = pominięte, false = niepominięte)", required = true, example = "false") @RequestParam boolean skipped,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Oznaczanie fiszki o ID: {} jako skipped={} dla użytkownika: {}", flashcardId, skipped, userId);
        flashcardService.markAsSkipped(flashcardId, skipped, userId);
        log.info("Fiszka {} oznaczona jako skipped={} pomyślnie dla użytkownika {}", flashcardId, skipped, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Inicjalizuje stan algorytmu nauki dla wszystkich fiszek w talii.
     *
     * <p>Przydatne gdy:
     * <ul>
     *   <li>Zmieniono algorytm nauki talii</li>
     *   <li>Dodano nowe fiszki i trzeba je zainicjalizować</li>
     *   <li>Użytkownik zaczyna naukę talii po raz pierwszy</li>
     * </ul>
     *
     * <p>Dla każdej fiszki inicjalizowany jest stan zgodnie z algorytmem talii.
     *
     * @param deckId ID talii
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie inicjalizacji
     * @throws IllegalArgumentException jeśli deckId jest pusty
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
            summary = "Inicjalizuj stan fiszek dla całej talii",
            description = "Inicjalizuje stan algorytmu nauki dla wszystkich fiszek w talii."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stan fiszek zainicjalizowany pomyślnie"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nieprawidłowe ID talii",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Brak dostępu do talii",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Talia nie znaleziona",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping("/deck/{deckId}/initialize")
    public ResponseEntity<Void> initializeDeckFlashcardsState(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Inicjalizacja stanu fiszek dla talii o ID: {} dla użytkownika: {}", deckId, userId);
        flashcardService.initializeDeckFlashcardsState(deckId, userId);
        log.info("Stan fiszek dla talii {} zainicjalizowany pomyślnie dla użytkownika {}", deckId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Inicjalizuje stan algorytmu nauki dla fiszek w sesji.
     *
     * <p>Endpoint używany podczas rozpoczynania sesji nauki.
     * Inicjalizuje stan tylko dla fiszek wybranych do danej sesji.
     *
     * <p>Lista flashcardIds może być:
     * <ul>
     *   <li>Wynikiem algorytmu wyboru fiszek do sesji</li>
     *   <li>Zestawem fiszek wybranych przez użytkownika</li>
     *   <li>Fiszkami spełniającymi konkretne kryteria</li>
     * </ul>
     *
     * @param deckId ID talii
     * @param flashcardIds lista ID fiszek do zainicjalizowania
     * @param userId ID użytkownika z nagłówka X-User-Id
     * @return potwierdzenie inicjalizacji
     * @throws IllegalArgumentException jeśli deckId jest pusty lub lista fiszek jest pusta
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
            summary = "Inicjalizuj stan fiszek dla sesji nauki",
            description = "Inicjalizuje stan algorytmu nauki dla wybranego zestawu fiszek (np. przed rozpoczęciem sesji)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stan fiszek sesji zainicjalizowany pomyślnie"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nieprawidłowe ID talii lub pusta lista fiszek",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Brak dostępu do talii",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Talia nie znaleziona",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping("/deck/{deckId}/session/initialize")
    public ResponseEntity<Void> initializeSessionFlashcardsState(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "Lista ID fiszek do zainicjalizowania", required = true) @Valid @RequestBody List<String> flashcardIds,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Inicjalizacja stanu {} fiszek dla sesji w talii {} dla użytkownika: {}", flashcardIds.size(), deckId, userId);
        flashcardService.initializeSessionFlashcardsState(deckId, flashcardIds, userId);
        log.info("Stan {} fiszek sesji w talii {} zainicjalizowany pomyślnie dla użytkownika {}", flashcardIds.size(), deckId, userId);
        return ResponseEntity.ok().build();
    }

}
