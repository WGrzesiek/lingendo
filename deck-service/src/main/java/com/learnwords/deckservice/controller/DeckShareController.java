package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.share.*;
import com.learnwords.deckservice.enums.ShareTargetType;
import com.learnwords.deckservice.service.DeckShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler REST API do zarządzania udostępnianiem talii fiszek.
 * 
 * <p>Ten kontroler udostępnia endpointy do:
 * <ul>
 *   <li>Udostępniania talii grupom, wszystkim uczniom, wszystkim znajomym lub konkretnemu użytkownikowi</li>
 *   <li>Wycofywania udostępnień</li>
 *   <li>Pobierania listy udostępnionych talii</li>
 *   <li>Pobierania talii udostępnionych aktualnie zalogowanemu użytkownikowi</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/decks-share")
@RequiredArgsConstructor
@Tag(name = "Udostępnianie talii", description = "Endpointy do zarządzania udostępnianiem talii")
public class DeckShareController {

    private final DeckShareService deckShareService;

    @PostMapping("/{deckId}/share")
    @Operation(
            summary = "Udostępnij talię",
            description = """
                    Udostępnia talię do wybranego celu.
                    
                    Typy celów:
                    - GROUP - udostępnienie do konkretnej grupy (wymagane targetId)
                    - ALL_STUDENTS - udostępnienie wszystkim uczniom nauczyciela
                    - ALL_FRIENDS - udostępnienie wszystkim znajomym
                    - USER - udostępnienie konkretnemu użytkownikowi (wymagane targetId)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Udostępnienie utworzone pomyślnie",
                    content = @Content(schema = @Schema(implementation = DeckShareResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do udostępnienia talii"),
            @ApiResponse(responseCode = "404", description = "Talia nie została znaleziona")
    })
    public ResponseEntity<DeckShareResponse> shareDeck(
            @Parameter(description = "ID talii do udostępnienia", required = true)
            @PathVariable String deckId,
            @Parameter(description = "ID użytkownika wykonującego akcję", required = true)
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody ShareDeckRequestBody body) {

        log.info("Żądanie udostępnienia talii {} przez użytkownika {} do {}: {}",
                deckId, userId, body.targetType(), body.targetId());

        ShareDeckRequest request = ShareDeckRequest.builder()
                .deckId(deckId)
                .targetType(body.targetType())
                .targetId(body.targetId())
                .message(body.message())
                .expiresAt(body.expiresAt())
                .build();

        DeckShareResponse response = deckShareService.shareDeck(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{deckId}/share/batch")
    @Operation(
            summary = "Udostępnij talię do wielu celów",
            description = "Udostępnia talię do wielu celów tego samego typu (np. wielu grup)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch udostępnienie przetworzone",
                    content = @Content(schema = @Schema(implementation = BatchShareResponse.class))),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do udostępnienia talii"),
            @ApiResponse(responseCode = "404", description = "Talia nie została znaleziona")
    })
    public ResponseEntity<BatchShareResponse> shareDeckBatch(
            @Parameter(description = "ID talii do udostępnienia", required = true)
            @PathVariable String deckId,
            @Parameter(description = "ID użytkownika wykonującego akcję", required = true)
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody BatchShareDeckRequestBody body) {

        log.info("Żądanie batch udostępnienia talii {} przez użytkownika {} do {} celów typu {}",
                deckId, userId, body.targetIds().size(), body.targetType());

        BatchShareDeckRequest request = BatchShareDeckRequest.builder()
                .deckId(deckId)
                .targetType(body.targetType())
                .targetIds(body.targetIds())
                .message(body.message())
                .expiresAt(body.expiresAt())
                .build();

        BatchShareResponse response = deckShareService.shareDeckBatch(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{deckId}/share/students")
    @Operation(
            summary = "Udostępnij talię wszystkim uczniom",
            description = "Udostępnia talię wszystkim uczniom nauczyciela"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Udostępnienie utworzone pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień"),
            @ApiResponse(responseCode = "404", description = "Talia nie została znaleziona")
    })
    public ResponseEntity<DeckShareResponse> shareDeckWithAllStudents(
            @PathVariable String deckId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody(required = false) ShareMessageBody body) {

        log.info("Żądanie udostępnienia talii {} wszystkim uczniom przez nauczyciela {}", deckId, userId);

        String message = body != null ? body.message() : null;
        DeckShareResponse response = deckShareService.shareDeckWithAllStudents(userId, deckId, message);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{deckId}/share/friends")
    @Operation(
            summary = "Udostępnij talię wszystkim znajomym",
            description = "Udostępnia talię wszystkim znajomym użytkownika"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Udostępnienie utworzone pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień"),
            @ApiResponse(responseCode = "404", description = "Talia nie została znaleziona")
    })
    public ResponseEntity<DeckShareResponse> shareDeckWithAllFriends(
            @PathVariable String deckId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody(required = false) ShareMessageBody body) {

        log.info("Żądanie udostępnienia talii {} wszystkim znajomym przez użytkownika {}", deckId, userId);

        String message = body != null ? body.message() : null;
        DeckShareResponse response = deckShareService.shareDeckWithAllFriends(userId, deckId, message);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{deckId}/share/group/{groupId}")
    @Operation(
            summary = "Udostępnij talię grupie",
            description = "Udostępnia talię konkretnej grupie uczniów"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Udostępnienie utworzone pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do grupy"),
            @ApiResponse(responseCode = "404", description = "Talia lub grupa nie została znaleziona")
    })
    public ResponseEntity<DeckShareResponse> shareDeckWithGroup(
            @PathVariable String deckId,
            @PathVariable String groupId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody(required = false) ShareMessageBody body) {

        log.info("Żądanie udostępnienia talii {} grupie {} przez użytkownika {}", deckId, groupId, userId);

        String message = body != null ? body.message() : null;
        DeckShareResponse response = deckShareService.shareDeckWithGroup(userId, deckId, groupId, message);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{deckId}/share/user/{targetUserId}")
    @Operation(
            summary = "Udostępnij talię użytkownikowi",
            description = "Udostępnia talię konkretnemu użytkownikowi"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Udostępnienie utworzone pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak relacji z użytkownikiem"),
            @ApiResponse(responseCode = "404", description = "Talia lub użytkownik nie został znaleziony")
    })
    public ResponseEntity<DeckShareResponse> shareDeckWithUser(
            @PathVariable String deckId,
            @PathVariable String targetUserId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody(required = false) ShareMessageBody body) {

        log.info("Żądanie udostępnienia talii {} użytkownikowi {} przez użytkownika {}",
                deckId, targetUserId, userId);

        String message = body != null ? body.message() : null;
        DeckShareResponse response = deckShareService.shareDeckWithUser(userId, deckId, targetUserId, message);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/shares/{shareId}")
    @Operation(
            summary = "Wycofaj udostępnienie",
            description = "Wycofuje wcześniej utworzone udostępnienie talii"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Udostępnienie wycofane pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień do wycofania udostępnienia"),
            @ApiResponse(responseCode = "404", description = "Udostępnienie nie zostało znalezione")
    })
    public ResponseEntity<Void> revokeDeckShare(
            @PathVariable String shareId,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Żądanie wycofania udostępnienia {} przez użytkownika {}", shareId, userId);

        deckShareService.revokeDeckShare(userId, shareId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{deckId}/shares")
    @Operation(
            summary = "Wycofaj wszystkie udostępnienia talii",
            description = "Wycofuje wszystkie aktywne udostępnienia dla danej talii"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Wszystkie udostępnienia wycofane pomyślnie"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień"),
            @ApiResponse(responseCode = "404", description = "Talia nie została znaleziona")
    })
    public ResponseEntity<Void> revokeAllDeckShares(
            @PathVariable String deckId,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Żądanie wycofania wszystkich udostępnień talii {} przez użytkownika {}", deckId, userId);

        deckShareService.revokeAllDeckShares(userId, deckId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{deckId}/shares")
    @Operation(
            summary = "Pobierz udostępnienia talii",
            description = "Zwraca listę wszystkich aktywnych udostępnień dla danej talii"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista udostępnień"),
            @ApiResponse(responseCode = "403", description = "Brak uprawnień"),
            @ApiResponse(responseCode = "404", description = "Talia nie została znaleziona")
    })
    public ResponseEntity<List<DeckShareResponse>> getDeckShares(
            @PathVariable String deckId,
            @RequestHeader("X-User-Id") String userId) {

        log.debug("Żądanie pobrania udostępnień talii {} przez użytkownika {}", deckId, userId);

        List<DeckShareResponse> shares = deckShareService.getDeckShares(userId, deckId);
        return ResponseEntity.ok(shares);
    }

    @GetMapping("/my-shares")
    @Operation(
            summary = "Pobierz moje udostępnienia",
            description = "Zwraca stronicowaną listę talii udostępnionych przez aktualnego użytkownika"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista udostępnień użytkownika")
    })
    public ResponseEntity<Page<DeckShareResponse>> getMyShares(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Żądanie pobrania udostępnień użytkownika {}", userId);

        Page<DeckShareResponse> shares = deckShareService.getMyShares(userId, page, size);
        return ResponseEntity.ok(shares);
    }

    @GetMapping("/shared-with-me")
    @Operation(
            summary = "Pobierz talie udostępnione mi",
            description = """
                    Zwraca stronicowaną listę talii udostępnionych aktualnemu użytkownikowi.
                    
                    Uwzględnia talie udostępnione przez:
                    - Bezpośrednie udostępnienie użytkownikowi
                    - Grupy, do których użytkownik należy
                    - Udostępnienie wszystkim uczniom (jeśli użytkownik jest uczniem nauczyciela)
                    - Udostępnienie wszystkim znajomym (jeśli użytkownik jest znajomym)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista udostępnionych talii")
    })
    public ResponseEntity<Page<SharedDeckDto>> getSharedWithMe(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Żądanie pobrania talii udostępnionych użytkownikowi {}", userId);

        Page<SharedDeckDto> decks = deckShareService.getSharedWithMe(userId, page, size);
        return ResponseEntity.ok(decks);
    }

    @GetMapping("/{deckId}/has-share-access")
    @Operation(
            summary = "Sprawdź dostęp do talii przez udostępnienie",
            description = "Sprawdza czy użytkownik ma dostęp do talii przez jakiekolwiek udostępnienie"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wynik sprawdzenia dostępu")
    })
    public ResponseEntity<Boolean> hasAccessToDeck(
            @PathVariable String deckId,
            @RequestHeader("X-User-Id") String userId) {

        log.debug("Sprawdzenie dostępu użytkownika {} do talii {} przez udostępnienie", userId, deckId);

        boolean hasAccess = deckShareService.hasAccessToDeck(userId, deckId);
        return ResponseEntity.ok(hasAccess);
    }


    public record ShareDeckRequestBody(
            @Schema(description = "Typ celu udostępnienia", required = true, 
                    enumAsRef = true, implementation = ShareTargetType.class)
            ShareTargetType targetType,
            
            @Schema(description = "ID celu (grupy lub użytkownika). Wymagane dla GROUP i USER")
            String targetId,
            
            @Schema(description = "Opcjonalna wiadomość dla odbiorców")
            String message,
            
            @Schema(description = "Data wygaśnięcia udostępnienia (opcjonalna)")
            java.time.Instant expiresAt
    ) {}


    public record BatchShareDeckRequestBody(
            @Schema(description = "Typ celu udostępnienia", required = true)
            ShareTargetType targetType,
            
            @Schema(description = "Lista ID celów (grup lub użytkowników)", required = true)
            List<String> targetIds,
            
            @Schema(description = "Opcjonalna wiadomość dla odbiorców")
            String message,
            
            @Schema(description = "Data wygaśnięcia udostępnienia (opcjonalna)")
            java.time.Instant expiresAt
    ) {}


    public record ShareMessageBody(
            @Schema(description = "Opcjonalna wiadomość dla odbiorców")
            String message
    ) {}
}
