package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.*;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.exception.exceptions.DeckNotFoundException;
import com.learnwords.deckservice.exception.exceptions.DeckWithThisNameForThisUserAlreadyExistsException;
import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
import com.learnwords.deckservice.service.DeckService;
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

/**
 * Kontroler REST API do zarządzania taliami fiszek.
 * 
 * <p>Ten kontroler udostępnia kompletny zestaw endpointów do operacji CRUD na taliach,
 * zarządzania ich konfiguracją oraz pobierania statystyk postępów w nauce.
 * 
 * <h2>Główne funkcjonalności:</h2>
 * <ul>
 *   <li><b>Zarządzanie taliami:</b>
 *     <ul>
 *       <li>Tworzenie nowych talii z konfiguracją języków i algorytmów nauki</li>
 *       <li>Pobieranie talii z zaawansowanym filtrowaniem (userId, widoczność, właściciel)</li>
 *       <li>Edycja szczegółów talii (nazwa, opis, ustawienia)</li>
 *       <li>Usuwanie talii wraz z powiązanymi danymi</li>
 *     </ul>
 *   </li>
 *   <li><b>Konfiguracja nauki:</b>
 *     <ul>
 *       <li>Zmiana algorytmu nauki (określa kolejność i sposób prezentacji fiszek)</li>
 *       <li>Ustawienie liczby fiszek na sesję (1-100)</li>
 *       <li>Zarządzanie widocznością (publiczne/prywatne)</li>
 *       <li>Zmiana typu właściciela (USER, ADMIN, SYSTEM)</li>
 *     </ul>
 *   </li>
 *   <li><b>Statystyki i analityka:</b>
 *     <ul>
 *       <li>Statystyki postępów w nauce (fiszki learned/unlearned, procent postępu)</li>
 *       <li>Liczby sesji (całkowite i ukończone)</li>
 *       <li>Liczba talii użytkownika (całkowite, publiczne, prywatne)</li>
 *     </ul>
 *   </li>
 *   <li><b>Walidacja:</b>
 *     <ul>
 *       <li>Sprawdzanie dostępności nazwy talii przed utworzeniem</li>
 *       <li>Walidacja unikalności w ramach talii użytkownika</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h2>Endpointy według kategorii:</h2>
 * 
 * <h3>CRUD Operations:</h3>
 * <ul>
 *   <li>POST /api/v1/decks - Utwórz nową talię</li>
 *   <li>GET /api/v1/decks - Pobierz talie z filtrami</li>
 *   <li>GET /api/v1/decks/{deckId} - Pobierz talię po ID</li>
 *   <li>GET /api/v1/decks/{deckId}/details - Pobierz szczegóły talii</li>
 *   <li>PUT /api/v1/decks/{deckId}/details - Zaktualizuj szczegóły talii</li>
 *   <li>DELETE /api/v1/decks/{deckId} - Usuń talię</li>
 * </ul>
 * 
 * <h3>Zapytania użytkownika:</h3>
 * <ul>
 *   <li>GET /api/v1/decks/user - Wszystkie talie użytkownika</li>
 *   <li>GET /api/v1/decks/user/filter - Talie użytkownika z filtrami</li>
 *   <li>GET /api/v1/decks/user/count - Liczba talii użytkownika</li>
 *   <li>GET /api/v1/decks/public - Wszystkie talie publiczne</li>
 * </ul>
 * 
 * <h3>Aktualizacje cząstkowe:</h3>
 * <ul>
 *   <li>PUT /api/v1/decks/{deckId}/name - Zmień nazwę</li>
 *   <li>PUT /api/v1/decks/{deckId}/visibility - Zmień widoczność</li>
 *   <li>PUT /api/v1/decks/{deckId}/owner - Zmień właściciela</li>
 *   <li>PUT /api/v1/decks/{deckId}/flashcardsPerSession - Zmień liczbę fiszek na sesję</li>
 *   <li>PUT /api/v1/decks/{deckId}/learnAlgorithm - Zmień algorytm nauki</li>
 * </ul>
 * 
 * <h3>Statystyki i walidacja:</h3>
 * <ul>
 *   <li>GET /api/v1/decks/{deckId}/statistics - Pobierz statystyki talii</li>
 *   <li>GET /api/v1/decks/validate-name - Waliduj dostępność nazwy</li>
 * </ul>
 * 
 * <h2>Autoryzacja:</h2>
 * <p>Większość endpointów wymaga nagłówka <code>x-client-id</code> z ID użytkownika.
 * Wyjątek: publiczne endpointy jak GET /public nie wymagają autoryzacji.
 * 
 * <h2>Obsługa błędów:</h2>
 * <p>Kontroler korzysta z {@link com.learnwords.deckservice.exception.GlobalExceptionHandler}
 * do centralnej obsługi wyjątków. Możliwe kody odpowiedzi:
 * <ul>
 *   <li><b>200 OK:</b> Operacja wykonana pomyślnie</li>
 *   <li><b>201 Created:</b> Zasób utworzony (POST /decks)</li>
 *   <li><b>400 Bad Request:</b> Błędne dane wejściowe lub brak wymaganych parametrów</li>
 *   <li><b>404 Not Found:</b> Talia nie znaleziona</li>
 *   <li><b>409 Conflict:</b> Talia o tej nazwie już istnieje dla użytkownika</li>
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
 * @since 2025-11-12
 * @see DeckService
 * @see DeckDto
 * @see DeckDetailsDto
 * @see com.learnwords.deckservice.exception.GlobalExceptionHandler
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/decks")
@Tag(name = "Deck Management", description = "API do zarządzania taliami fiszek")
public class DeckController {
    private static final String USER_ID_HEADER = "X-User-Id";
    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    /**
     * Pobiera listę talii z opcjonalnymi filtrami.
     * 
     * <p>Endpoint umożliwia filtrowanie talii według:
     * <ul>
     *   <li>ID użytkownika - zwraca talie danego użytkownika</li>
     *   <li>Widoczności - zwraca talie publiczne lub prywatne</li>
     *   <li>Właściciela - zwraca talie według typu właściciela (USER, ADMIN, SYSTEM)</li>
     * </ul>
     * 
     * <p>Jeśli nie podano żadnego filtru, zwracane są wszystkie talie.
     * 
     * @param userId ID użytkownika
     * @param isPublic czy talia jest publiczna (opcjonalny)
     * @param owner typ właściciela talii (opcjonalny)
     * @return lista talii spełniających kryteria filtrowania
     */
    @Operation(
        summary = "Pobierz talie z filtrami",
        description = "Pobiera listę talii z opcjonalnymi filtrami: userId, isPublic, owner. Bez filtrów zwraca wszystkie talie."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista talii pobrana pomyślnie",
            content = @Content(schema = @Schema(implementation = DeckDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Błędne parametry zapytania",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping()
    public ResponseEntity<List<DeckDto>> getDecksByFilter(
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "Czy talia jest publiczna", example = "true") @RequestParam(required = false) Boolean isPublic,
            @Parameter(description = "Typ właściciela talii", schema = @Schema(implementation = DeckOwner.class)) @RequestParam(required = false) DeckOwner owner) {
        log.debug("Pobieranie talii z filtrami - userId: {}, isPublic: {}, owner: {}", userId, isPublic, owner);
        List<DeckDto> decks = deckService.getDecksByFilter(userId, isPublic, owner);
        log.info("Znaleziono {} talii spełniających kryteria", decks.size());
        return ResponseEntity.ok(decks);
    }

    /**
     * Tworzy nową talię dla użytkownika.
     * 
     * <p>Endpoint tworzy nową talię z podanymi parametrami:
     * <ul>
     *   <li>Nazwa talii - wymagana, unikalna dla użytkownika</li>
     *   <li>Algorytm nauki - określa sposób prezentacji fiszek</li>
     *   <li>Języki - język źródłowy i docelowy</li>
     *   <li>Ustawienia - liczba fiszek na sesję, widoczność</li>
     * </ul>
     * 
     * <p>Talia jest automatycznie przypisywana do użytkownika z nagłówka x-client-id.
     * 
     * @param userId ID użytkownika z nagłówka x-client-id
     * @param createDeckDto dane nowej talii
     * @return potwierdzenie utworzenia talii
     * @throws IllegalArgumentException jeśli brak userId lub dane są nieprawidłowe
     * @throws DeckWithThisNameForThisUserAlreadyExistsException jeśli użytkownik ma już talię o tej nazwie
     */
    @Operation(
        summary = "Utwórz nową talię",
        description = "Tworzy nową talię fiszek dla użytkownika. Nazwa musi być unikalna w ramach talii użytkownika."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Talia utworzona pomyślnie",
            content = @Content(schema = @Schema(implementation = ResponseDeckDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Błędne dane wejściowe lub brak wymaganego nagłówka",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Talia o tej nazwie już istnieje dla tego użytkownika",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PostMapping()
    public ResponseEntity<ResponseDeckDto> createDeck(@Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId,
                                                      @Parameter(description = "Dane nowej talii", required = true)
                                                      @Valid @RequestBody CreateDeckDto createDeckDto) {
        log.debug("Tworzenie tali o nazwie {}, przez {}", createDeckDto.getDeckName(), userId);
        deckService.createDeck(userId, createDeckDto);
        log.info("Talia {} utworzona pomyślnie dla użytkownika {}", createDeckDto.getDeckName(), userId);
        ResponseDeckDto responseDeckDto = new ResponseDeckDto(
            createDeckDto.getDeckName(), 
            "Talia została pomyślnie utworzona."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDeckDto);
    }

    /**
     * Pobiera podstawowe informacje o talii po jej ID.
     * 
     * <p>Zwraca podstawowe dane talii, takie jak:
     * <ul>
     *   <li>ID i nazwa talii</li>
     *   <li>ID użytkownika właściciela</li>
     *   <li>Widoczność (publiczna/prywatna)</li>
     *   <li>Typ właściciela</li>
     *   <li>Liczba słówek w talii</li>
     * </ul>
     * 
     * <p>Do pobrania pełnych szczegółów użyj endpointu GET /{deckId}/details.
     * 
     * @param deckId ID talii do pobrania
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return podstawowe dane talii
     * @throws IllegalArgumentException jeśli deckId jest pusty
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
        summary = "Pobierz talię po ID",
        description = "Pobiera podstawowe informacje o talii na podstawie jej ID. Dla szczegółowych danych użyj /details."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Talia znaleziona",
            content = @Content(schema = @Schema(implementation = DeckDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID talii",
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
    @GetMapping("/{deckId}")
    public ResponseEntity<DeckDto> getDeckById(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie talii o ID: {} dla użytkownika: {}", deckId, userId);
        DeckDto deckDto = deckService.getDeckById(deckId, userId);
        log.info("Talia {} pobrana pomyślnie dla użytkownika {}", deckId, userId);
        return ResponseEntity.ok(deckDto);
    }

    /**
     * Pobiera szczegółowe informacje o talii.
     * 
     * <p>Zwraca kompletne dane talii, w tym:
     * <ul>
     *   <li>Wszystkie pola z podstawowego DTO (id, nazwa, userId, etc.)</li>
     *   <li>Opis talii</li>
     *   <li>Ustawienia algorytmu nauki</li>
     *   <li>Liczba fiszek na sesję</li>
     *   <li>Języki (źródłowy i docelowy)</li>
     *   <li>Znaczniki czasowe (utworzenie, aktualizacja)</li>
     * </ul>
     * 
     * <p>To DTO jest używane zarówno do odczytu jak i do edycji talii.
     * 
     * @param deckId ID talii
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return szczegółowe dane talii
     * @throws IllegalArgumentException jeśli deckId jest pusty
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
        summary = "Pobierz szczegóły talii",
        description = "Pobiera kompletne informacje o talii, w tym ustawienia, języki i metadane."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Szczegóły talii pobrane pomyślnie",
            content = @Content(schema = @Schema(implementation = DeckDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID talii",
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
    @GetMapping("/{deckId}/details")
    public ResponseEntity<DeckDetailsDto> getDeckDetails(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie szczegółów talii o ID: {} dla użytkownika: {}", deckId, userId);
        DeckDetailsDto details = deckService.getDeckDetailsById(deckId, userId);
        log.info("Szczegóły talii {} pobrane pomyślnie dla użytkownika {}", deckId, userId);
        return ResponseEntity.ok(details);
    }

    /**
     * Aktualizuje szczegóły talii.
     * 
     * <p>Umożliwia edycję wszystkich edytowalnych pól talii:
     * <ul>
     *   <li>Nazwa i opis</li>
     *   <li>Widoczność (publiczna/prywatna)</li>
     *   <li>Typ właściciela</li>
     *   <li>Algorytm nauki</li>
     *   <li>Liczba fiszek na sesję</li>
     *   <li>Języki (źródłowy i docelowy)</li>
     * </ul>
     * 
     * <p>Pola tylko do odczytu (id, userId, wordCount, timestamps) są ignorowane.
     * 
     * @param deckId ID talii do aktualizacji
     * @param deckDetailsDto nowe dane talii z walidacją
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return zaktualizowane szczegóły talii
     * @throws IllegalArgumentException jeśli deckId jest pusty lub dane są nieprawidłowe
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
        summary = "Aktualizuj szczegóły talii",
        description = "Aktualizuje edytowalne pola talii. Pola tylko do odczytu (id, userId, wordCount, timestamps) są ignorowane."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Talia zaktualizowana pomyślnie",
            content = @Content(schema = @Schema(implementation = DeckDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Błędne dane wejściowe",
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
    @PutMapping("/{deckId}/details")
    public ResponseEntity<DeckDetailsDto> updateDeckDetails(
            @Parameter(description = "ID talii do aktualizacji", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "Nowe dane talii", required = true) @Valid @RequestBody DeckDetailsDto deckDetailsDto,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Aktualizacja szczegółów talii o ID: {} dla użytkownika: {}", deckId, userId);
        DeckDetailsDto updatedDetails = deckService.editDeckDetails(deckId, deckDetailsDto, userId);
        log.info("Talia {} zaktualizowana pomyślnie dla użytkownika {}", deckId, userId);
        return ResponseEntity.ok(updatedDetails);
    }

    /**
     * Pobiera talie użytkownika z opcjonalnymi filtrami.
     * 
     * <p>Zwraca talie należące do konkretnego użytkownika z możliwością filtrowania według:
     * <ul>
     *   <li>Widoczności (publiczne/prywatne)</li>
     *   <li>Typu właściciela (USER, ADMIN, SYSTEM)</li>
     * </ul>
     * 
     * <p>ID użytkownika jest pobierane z nagłówka x-client-id (automatyczna autoryzacja).
     * Bez filtrów zwraca wszystkie talie użytkownika.
     * 
     * @param userId ID użytkownika z nagłówka x-client-id
     * @param isPublic czy talia jest publiczna (opcjonalny)
     * @param owner typ właściciela talii (opcjonalny)
     * @return lista talii użytkownika spełniających kryteria
     * @throws IllegalArgumentException jeśli brak nagłówka x-client-id
     */
    @Operation(
        summary = "Pobierz talie użytkownika z filtrami",
        description = "Pobiera talie konkretnego użytkownika z opcjonalnymi filtrami widoczności i właściciela. UserId jest pobierany automatycznie z nagłówka."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista talii użytkownika",
            content = @Content(schema = @Schema(implementation = DeckDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Brak wymaganego nagłówka x-client-id",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/user/filter")
    public ResponseEntity<List<DeckDto>> filterUserDecks(
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "Czy talia jest publiczna", example = "true") @RequestParam(required = false) Boolean isPublic,
            @Parameter(description = "Typ właściciela talii", schema = @Schema(implementation = DeckOwner.class)) @RequestParam(required = false) DeckOwner owner) {
        log.debug("Filtrowanie talii użytkownika {} - isPublic: {}, owner: {}", userId, isPublic, owner);
        List<DeckDto> decks = deckService.getDecksByFilter(userId, isPublic, owner);
        log.info("Znaleziono {} talii użytkownika {} spełniających kryteria", decks.size(), userId);
        return ResponseEntity.ok(decks);
    }

    /**
     * Pobiera wszystkie talie użytkownika.
     * 
     * <p>Zwraca kompletną listę wszystkich talii należących do użytkownika,
     * bez żadnych filtrów. Obejmuje zarówno talie publiczne jak i prywatne.
     * 
     * <p>ID użytkownika jest automatycznie pobierane z nagłówka x-client-id,
     * co zapewnia, że użytkownik może zobaczyć tylko własne talie.
     * 
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return lista wszystkich talii użytkownika
     * @throws IllegalArgumentException jeśli brak nagłówka x-client-id
     */
    @Operation(
        summary = "Pobierz wszystkie talie użytkownika",
        description = "Pobiera kompletną listę wszystkich talii użytkownika (publicznych i prywatnych) bez filtrowania."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista wszystkich talii użytkownika",
            content = @Content(schema = @Schema(implementation = DeckDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Brak wymaganego nagłówka x-client-id",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/user")
    public ResponseEntity<List<DeckDto>> getUserDecks(
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie wszystkich talii użytkownika {}", userId);
        List<DeckDto> decks = deckService.getDecksByFilter(userId);
        log.info("Znaleziono {} talii użytkownika {}", decks.size(), userId);
        return ResponseEntity.ok(decks);
    }

    /**
     * Pobiera wszystkie talie publiczne.
     * 
     * <p>Zwraca listę wszystkich talii oznaczonych jako publiczne.
     * Endpoint nie wymaga autoryzacji - każdy może przeglądać publiczne talie.
     * 
     * <p>Publiczne talie mogą być:
     * <ul>
     *   <li>Utworzone przez użytkowników i udostępnione publicznie</li>
     *   <li>Talie systemowe dostępne dla wszystkich</li>
     *   <li>Talie administracyjne jako materiały edukacyjne</li>
     * </ul>
     * 
     * @return lista wszystkich publicznych talii
     */
    @Operation(
        summary = "Pobierz wszystkie talie publiczne",
        description = "Pobiera listę wszystkich talii oznaczonych jako publiczne. Nie wymaga autoryzacji."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista publicznych talii",
            content = @Content(schema = @Schema(implementation = DeckDto.class))
        )
    })
    @GetMapping("/public")
    public ResponseEntity<List<DeckDto>> getPublicDecks() {
        log.debug("Pobieranie wszystkich talii publicznych");
        List<DeckDto> decks = deckService.getPublicDecks();
        log.info("Znaleziono {} talii publicznych", decks.size());
        return ResponseEntity.ok(decks);
    }

    /**
     * Zmienia nazwę talii.
     * 
     * <p>Aktualizuje nazwę istniejącej talii. Nazwa musi spełniać warunki:
     * <ul>
     *   <li>Nie może być pusta</li>
     *   <li>Musi mieć długość od 1 do 100 znaków</li>
     *   <li>Musi być unikalna w ramach talii użytkownika</li>
     * </ul>
     * 
     * <p>Endpoint aktualizuje tylko nazwę - do zmiany innych właściwości
     * użyj PUT /{deckId}/details.
     * 
     * @param deckId ID talii do zaktualizowania
     * @param userId ID użytkownika z nagłówka x-client-id
     * @param request obiekt zawierający nową nazwę z walidacją
     * @return zaktualizowana nazwa talii
     * @throws IllegalArgumentException jeśli deckId jest pusty lub nazwa nieprawidłowa
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws DeckWithThisNameForThisUserAlreadyExistsException jeśli nazwa nie jest unikalna dla użytkownika
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
        summary = "Zmień nazwę talii",
        description = "Aktualizuje nazwę talii. Nazwa musi być unikalna w ramach talii użytkownika i mieć 1-100 znaków."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Nazwa talii zmieniona pomyślnie",
            content = @Content(schema = @Schema(implementation = UpdateDeckNameRequest.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowa nazwa lub ID talii",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Talia nie znaleziona",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Talia o tej nazwie już istnieje dla tego użytkownika",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Brak dostępu do talii",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{deckId}/name")
    public ResponseEntity<UpdateDeckNameRequest> updateDeckName(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "Nowa nazwa talii", required = true) @Valid @RequestBody UpdateDeckNameRequest request,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Aktualizacja nazwy talii o ID: {} dla użytkownika: {} na '{}'", deckId, userId, request.deckName());
        String updatedName = deckService.renameDeck(deckId, request.deckName(), userId);
        log.info("Nazwa talii {} zaktualizowana pomyślnie dla użytkownika {} na '{}'", deckId, userId, updatedName);
        return ResponseEntity.ok(request);
    }

    /**
     * Zmienia widoczność talii.
     * 
     * <p>Przełącza talię między trybem publicznym a prywatnym:
     * <ul>
     *   <li><b>Publiczna (true):</b> Talia jest widoczna dla wszystkich użytkowników
     *       i może być używana przez innych</li>
     *   <li><b>Prywatna (false):</b> Talia jest widoczna tylko dla właściciela</li>
     * </ul>
     * 
     * <p>Zmiana widoczności nie wpływa na zawartość talii ani jej ustawienia.
     * 
     * @param deckId ID talii do zaktualizowania
     * @param request obiekt zawierający nową wartość widoczności
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return zaktualizowana wartość widoczności
     * @throws IllegalArgumentException jeśli deckId jest pusty
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
        summary = "Zmień widoczność talii",
        description = "Przełącza talię między trybem publicznym (widoczna dla wszystkich) a prywatnym (tylko dla właściciela)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Widoczność talii zmieniona pomyślnie",
            content = @Content(schema = @Schema(implementation = UpdateDeckVisibilityRequest.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID talii",
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
    @PutMapping("/{deckId}/visibility")
    public ResponseEntity<UpdateDeckVisibilityRequest> updateDeckVisibility(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "Nowa widoczność talii", required = true) @Valid @RequestBody UpdateDeckVisibilityRequest request,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Aktualizacja widoczności talii o ID: {} dla użytkownika: {} na '{}'", deckId, userId, request.isPublic());
        boolean result = deckService.changeDeckVisibility(deckId, userId, request.isPublic());
        log.info("Widzoczność talii {} zaktualizowana pomyślnie dla użytkownika {} na '{}'", deckId, userId, result);
        return ResponseEntity.ok(request);
    }

    /**
     * Zmienia właściciela talii.
     * 
     * <p>Aktualizuje typ właściciela talii. Dostępne typy:
     * <ul>
     *   <li><b>USER:</b> Talia należy do zwykłego użytkownika</li>
     *   <li><b>ADMIN:</b> Talia administracyjna (np. materiały edukacyjne)</li>
     *   <li><b>SYSTEM:</b> Talia systemowa (np. wbudowane zestawy słówek)</li>
     * </ul>
     * 
     * <p>Zmiana typu właściciela może wpływać na uprawnienia edycji i widoczność.
     * Zwykle używane przez administratorów do zarządzania taliami.
     * 
     * @param deckId ID talii do zaktualizowania
     * @param request obiekt zawierający nowy typ właściciela
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return zaktualizowany typ właściciela
     * @throws IllegalArgumentException jeśli deckId jest pusty
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing
     */
    @Operation(
        summary = "Zmień właściciela talii",
        description = "Aktualizuje typ właściciela talii (USER, ADMIN, SYSTEM). Zwykle używane przez administratorów."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Właściciel talii zmieniony pomyślnie",
            content = @Content(schema = @Schema(implementation = UpdateDeckOwnerRequest.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID talii lub typ właściciela",
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
    @PutMapping("/{deckId}/owner")
    public ResponseEntity<UpdateDeckOwnerRequest> updateDeckOwner(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "Nowy typ właściciela talii", required = true) @Valid @RequestBody UpdateDeckOwnerRequest request,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Aktualizacja właściciela talii o ID: {} dla użytkownika: {} na '{}'", deckId, userId, request.newOwner());
        DeckOwner updatedOwner = deckService.changeDeckOwner(deckId, userId, request.newOwner());
        log.info("Właściciel talii {} zaktualizowany pomyślnie dla użytkownika {} na '{}'", deckId, userId, updatedOwner);
        return ResponseEntity.ok(request);
    }

    /**
     * Zmienia liczbę fiszek na sesję nauki.
     * 
     * <p>Określa ile fiszek zostanie wybranych do jednej sesji nauki.
     * Wartość musi być w zakresie od 1 do 100.
     * 
     * <p>Mniejsza liczba:
     * <ul>
     *   <li>Krótsza sesja nauki (lepsze dla początkujących)</li>
     *   <li>Szybsze postępy widoczne w statystykach</li>
     *   <li>Mniejsze zmęczenie kognitywne</li>
     * </ul>
     * 
     * <p>Większa liczba:
     * <ul>
     *   <li>Intensywniejsza sesja (dla zaawansowanych)</li>
     *   <li>Więcej materiału w jednym podejściu</li>
     *   <li>Szybsze przejście przez całą talię</li>
     * </ul>
     * 
     * @param deckId ID talii do zaktualizowania
     * @param request obiekt zawierający nową liczbę fiszek (1-100)
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return zaktualizowana liczba fiszek na sesję
     * @throws IllegalArgumentException jeśli deckId jest pusty lub liczba poza zakresem
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
        summary = "Zmień liczbę fiszek na sesję",
        description = "Ustawia ile fiszek będzie wyświetlanych w jednej sesji nauki (zakres: 1-100)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liczba fiszek na sesję zmieniona pomyślnie",
            content = @Content(schema = @Schema(implementation = UpdateFlashcardsPerSessionRequest.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowa liczba fiszek lub ID talii",
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
    @PutMapping("/{deckId}/flashcardsPerSession")
    public ResponseEntity<UpdateFlashcardsPerSessionRequest> updateFlashcardsPerSession(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "Liczba fiszek na sesję (1-100)", required = true) @Valid @RequestBody UpdateFlashcardsPerSessionRequest request,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Aktualizacja liczby fiszek na sesję dla talii o ID: {} dla użytkownika: {} na '{}'", deckId, userId, request.flashcardsPerSession());
        deckService.updateFlashcardsPerSession(deckId, request.flashcardsPerSession(), userId);
        log.info("Liczba fiszek na sesję dla talii {} zaktualizowana pomyślnie dla użytkownika {} na '{}'", deckId, userId, request.flashcardsPerSession());
        return ResponseEntity.ok(request);
    }

    /**
     * Zmienia algorytm nauki dla talii.
     * 
     * <p>Określa sposób wybierania i kolejności prezentacji fiszek podczas sesji nauki.
     * Dostępne algorytmy mają różne strategie i są dostosowane do różnych stylów uczenia.
     * 
     * <p>Zmiana algorytmu wpływa na:
     * <ul>
     *   <li>Kolejność prezentacji fiszek w nowych sesjach</li>
     *   <li>Sposób zarządzania powtórkami</li>
     *   <li>Interwały między powtórzeniami</li>
     *   <li>Adaptację do postępów użytkownika</li>
     * </ul>
     * 
     * <p>Zmiana nie wpływa na już rozpoczęte sesje ani historię nauki.
     * 
     * @param deckId ID talii do zaktualizowania
     * @param request obiekt zawierający nowy algorytm nauki
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return zaktualizowany algorytm nauki
     * @throws IllegalArgumentException jeśli deckId jest pusty lub algorytm nieprawidłowy
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     */
    @Operation(
        summary = "Zmień algorytm nauki",
        description = "Ustawia algorytm określający sposób wyboru i kolejności fiszek podczas nauki."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Algorytm nauki zmieniony pomyślnie",
            content = @Content(schema = @Schema(implementation = UpdateLearnAlgorithmRequest.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowy algorytm lub ID talii",
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
    @PutMapping("/{deckId}/learnAlgorithm")
    public ResponseEntity<UpdateLearnAlgorithmRequest> updateLearnAlgorithm(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "Nowy algorytm nauki", required = true) @Valid @RequestBody UpdateLearnAlgorithmRequest request,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Aktualizacja algorytmu nauki dla talii o ID: {} dla użytkownika: {} na '{}'", deckId, userId, request.learnAlgorithm());
        deckService.updateLearnAlgorithm(deckId, request.learnAlgorithm(), userId);
        log.info("Algorytm nauki dla talii {} zaktualizowany pomyślnie dla użytkownika {} na '{}'", deckId, userId, request.learnAlgorithm());
        return ResponseEntity.ok(request);
    }

    /**
     * Usuwa talię.
     * 
     * <p>Trwale usuwa talię wraz z powiązanymi danymi:
     * <ul>
     *   <li>Wszystkie fiszki należące do talii</li>
     *   <li>Historie sesji nauki dla tej talii</li>
     *   <li>Statystyki postępów</li>
     *   <li>Metadane talii</li>
     * </ul>
     * 
     * <p><b>UWAGA:</b> Operacja jest nieodwracalna! Wszystkie dane zostaną
     * permanentnie usunięte z systemu. Zalecane jest wcześniejsze
     * potwierdzenie tej akcji z użytkownikiem.
     * 
     * <p>Samo słownictwo (Word entities) nie jest usuwane - pozostaje
     * w bazie Vocabulary Service i może być użyte w innych taliach.
     * 
     * @param deckId ID talii do usunięcia
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return potwierdzenie usunięcia talii
     * @throws IllegalArgumentException jeśli deckId jest pusty
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
        summary = "Usuń talię",
        description = "Trwale usuwa talię wraz z fiszekami, historiami sesji i statystykami. Operacja nieodwracalna!"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Talia usunięta pomyślnie",
            content = @Content(schema = @Schema(implementation = ResponseDeckDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID talii",
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
    @DeleteMapping("/{deckId}")
    public ResponseEntity<ResponseDeckDto> deleteDeck(
            @Parameter(description = "ID talii do usunięcia", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Usuwanie talii o ID: {} dla użytkownika: {}", deckId, userId);
        deckService.deleteDeck(deckId, userId);
        log.info("Talia {} usunięta pomyślnie dla użytkownika {}", deckId, userId);
        ResponseDeckDto response = ResponseDeckDto.builder()
            .message("Talia o ID " + deckId + " została pomyślnie usunięta.")
            .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Pobiera liczbę talii użytkownika.
     * 
     * <p>Zwraca statystyki dotyczące liczby talii użytkownika z podziałem na:
     * <ul>
     *   <li><b>totalDecks:</b> Całkowita liczba wszystkich talii użytkownika</li>
     *   <li><b>publicDecks:</b> Liczba talii publicznych (widocznych dla innych)</li>
     *   <li><b>privateDecks:</b> Liczba talii prywatnych (tylko dla właściciela)</li>
     * </ul>
     * 
     * <p>Przydatne do:
     * <ul>
     *   <li>Wyświetlania podsumowania na dashboardzie</li>
     *   <li>Walidacji limitów (np. max liczba talii)</li>
     *   <li>Statystyk użytkowania</li>
     * </ul>
     * 
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return liczby talii z podziałem na typy
     * @throws IllegalArgumentException jeśli brak nagłówka x-client-id
     */
    @Operation(
        summary = "Pobierz liczbę talii użytkownika",
        description = "Zwraca statystyki liczby talii użytkownika: całkowita, publiczne, prywatne."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liczba talii pobrana pomyślnie",
            content = @Content(schema = @Schema(implementation = UserDeckCountDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Brak wymaganego nagłówka x-client-id",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/user/count")
    public ResponseEntity<UserDeckCountDto> getUserDeckCount(
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie liczby talii użytkownika {}", userId);
        UserDeckCountDto count = deckService.getUserDeckCount(userId);
        log.info("Liczba talii użytkownika {}: total={}, public={}, private={}",
            userId, count.totalDecks(), count.publicDecks(), count.privateDecks());
        return ResponseEntity.ok(count);
    }

    /**
     * Pobiera statystyki talii.
     * 
     * <p>Zwraca szczegółowe statystyki dotyczące postępów w nauce dla konkretnej talii:
     * <ul>
     *   <li><b>Fiszki:</b>
     *     <ul>
     *       <li>totalFlashcards - całkowita liczba fiszek w talii</li>
     *       <li>learnedFlashcards - liczba opanowanych fiszek</li>
     *       <li>unlearnedFlashcards - liczba fiszek do nauczenia</li>
     *       <li>progressPercentage - procent postępu (learned/total * 100)</li>
     *     </ul>
     *   </li>
     *   <li><b>Sesje:</b>
     *     <ul>
     *       <li>totalSessions - całkowita liczba wszystkich sesji</li>
     *       <li>completedSessions - liczba ukończonych sesji</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * <p>Przydatne do:
     * <ul>
     *   <li>Wyświetlania postępów użytkownika</li>
     *   <li>Tworzenia wykresów i dashboardów</li>
     *   <li>Motywowania do kontynuowania nauki</li>
     *   <li>Analizy efektywności nauki</li>
     * </ul>
     * 
     * @param deckId ID talii
     * @param userId ID użytkownika z nagłówka x-client-id
     * @return szczegółowe statystyki talii
     * @throws IllegalArgumentException jeśli deckId jest pusty
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma dostępu do talii
     */
    @Operation(
        summary = "Pobierz statystyki talii",
        description = "Zwraca szczegółowe statystyki postępów: liczby fiszek (learned/unlearned), procent postępu, liczby sesji."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statystyki talii pobrane pomyślnie",
            content = @Content(schema = @Schema(implementation = DeckStatisticsDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Nieprawidłowe ID talii",
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
    @GetMapping("/{deckId}/statistics")
    public ResponseEntity<DeckStatisticsDto> getDeckStatistics(
            @Parameter(description = "ID talii", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie statystyk talii o ID: {} dla użytkownika: {}", deckId, userId);
        DeckStatisticsDto statistics = deckService.getDeckStatistics(deckId, userId);
        log.info("Statystyki talii {} dla użytkownika {} pobrane pomyślnie", deckId, userId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Waliduje dostępność nazwy talii dla użytkownika.
     * 
     * <p>Sprawdza, czy podana nazwa talii jest dostępna (nie jest już używana)
     * dla konkretnego użytkownika. Każdy użytkownik może mieć tylko jedną
     * talię o danej nazwie.
     * 
     * <p>Endpoint przydatny do:
     * <ul>
     *   <li>Walidacji w czasie rzeczywistym podczas tworzenia talii</li>
     *   <li>Sprawdzania przed zapisem formularza</li>
     *   <li>Wyświetlania komunikatu "Nazwa już zajęta"</li>
     *   <li>Zapobiegania błędom walidacji po stronie serwera</li>
     * </ul>
     * 
     * <p>Zwraca:
     * <ul>
     *   <li><b>true</b> - nazwa jest dostępna, można jej użyć</li>
     *   <li><b>false</b> - nazwa jest zajęta, użytkownik ma już talię o tej nazwie</li>
     * </ul>
     * 
     * @param userId ID użytkownika z nagłówka x-client-id
     * @param deckName nazwa talii do sprawdzenia
     * @return true jeśli nazwa jest dostępna, false jeśli zajęta
     * @throws IllegalArgumentException jeśli brak userId lub deckName jest pusty
     * @throws DeckWithThisNameForThisUserAlreadyExistsException
     */
    @Operation(
        summary = "Waliduj dostępność nazwy talii",
        description = "Sprawdza czy podana nazwa talii jest dostępna dla użytkownika. Zwraca true jeśli nazwa wolna, false jeśli zajęta."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Wynik walidacji (true = dostępna, false = zajęta)",
            content = @Content(schema = @Schema(implementation = Boolean.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Brak wymaganego nagłówka lub parametru",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Talia o tej nazwie już istnieje dla tego użytkownika",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @GetMapping("/validate-name")
    public ResponseEntity<Boolean> validateDeckName(
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123") @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "Nazwa talii do walidacji", required = true, example = "English Words") @RequestParam String deckName) {
        log.debug("Walidacja nazwy talii '{}' dla użytkownika {}", deckName, userId);
        boolean isTaken = deckService.isDeckNameTaken(userId, deckName);
        boolean isAvailable = !isTaken;
        log.info("Nazwa talii '{}' dla użytkownika {} jest dostępna: {}", deckName, userId, isAvailable);
        return ResponseEntity.ok(isAvailable);
    }
}
