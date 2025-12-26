package com.learnwords.userservice.controller;

import com.learnwords.userservice.dtos.friendship.*;
import com.learnwords.userservice.service.FriendshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler REST do zarządzania relacjami przyjaźni między użytkownikami
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/friends")
@Tag(name = "Znajomi", description = "API do zarządzania znajomymi i zaproszeniami do znajomych")
public class FriendshipController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    // ==================== ZAPROSZENIA DO ZNAJOMYCH ====================

    @PostMapping("/requests")
    @Operation(
            summary = "Wyślij zaproszenie do znajomych",
            description = "Wysyła zaproszenie do znajomych do wskazanego użytkownika.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Zaproszenie zostało wysłane",
                            content = @Content(schema = @Schema(implementation = FriendRequestResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane lub próba wysłania do siebie"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono użytkownika"),
                    @ApiResponse(responseCode = "409", description = "Relacja już istnieje")
            }
    )
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(
            @Parameter(description = "ID użytkownika wysyłającego", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @Valid @RequestBody SendFriendRequestDto request) {
        log.info("Wysyłanie zaproszenia: {} -> {}", userId, request.targetUserId());
        FriendRequestResponse response = friendshipService.sendFriendRequest(userId, request.targetUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/requests/received")
    @Operation(
            summary = "Pobierz otrzymane zaproszenia",
            description = "Pobiera listę oczekujących zaproszeń do znajomych.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista oczekujących zaproszeń")
            }
    )
    public ResponseEntity<Page<FriendRequestResponse>> getPendingRequests(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Pobieranie oczekujących zaproszeń dla: {}", userId);
        Page<FriendRequestResponse> requests = friendshipService.getPendingRequests(userId, page, size);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/requests/sent")
    @Operation(
            summary = "Pobierz wysłane zaproszenia",
            description = "Pobiera listę wysłanych zaproszeń do znajomych (oczekujących na akceptację).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista wysłanych zaproszeń")
            }
    )
    public ResponseEntity<Page<FriendRequestResponse>> getSentRequests(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Pobieranie wysłanych zaproszeń przez: {}", userId);
        Page<FriendRequestResponse> requests = friendshipService.getSentRequests(userId, page, size);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/requests/{friendshipId}/accept")
    @Operation(
            summary = "Akceptuj zaproszenie",
            description = "Akceptuje zaproszenie do znajomych.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Zaproszenie zaakceptowane",
                            content = @Content(schema = @Schema(implementation = FriendResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono zaproszenia"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień do akceptacji")
            }
    )
    public ResponseEntity<FriendResponse> acceptFriendRequest(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "ID relacji/zaproszenia", required = true)
            @PathVariable String friendshipId) {
        log.info("Akceptowanie zaproszenia: {} przez: {}", friendshipId, userId);
        FriendResponse response = friendshipService.acceptFriendRequest(userId, friendshipId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/requests/{friendshipId}/reject")
    @Operation(
            summary = "Odrzuć zaproszenie",
            description = "Odrzuca zaproszenie do znajomych.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Zaproszenie odrzucone"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono zaproszenia"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień do odrzucenia")
            }
    )
    public ResponseEntity<Void> rejectFriendRequest(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "ID relacji/zaproszenia", required = true)
            @PathVariable String friendshipId) {
        log.info("Odrzucanie zaproszenia: {} przez: {}", friendshipId, userId);
        friendshipService.rejectFriendRequest(userId, friendshipId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/requests/{friendshipId}")
    @Operation(
            summary = "Anuluj wysłane zaproszenie",
            description = "Anuluje własne wysłane zaproszenie do znajomych.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Zaproszenie anulowane"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono zaproszenia"),
                    @ApiResponse(responseCode = "403", description = "Możesz anulować tylko własne zaproszenia")
            }
    )
    public ResponseEntity<Void> cancelFriendRequest(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "ID relacji/zaproszenia", required = true)
            @PathVariable String friendshipId) {
        log.info("Anulowanie zaproszenia: {} przez: {}", friendshipId, userId);
        friendshipService.cancelFriendRequest(userId, friendshipId);
        return ResponseEntity.noContent().build();
    }

    // ==================== ZARZĄDZANIE ZNAJOMYMI ====================

    @GetMapping
    @Operation(
            summary = "Pobierz listę znajomych",
            description = "Pobiera listę wszystkich znajomych użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista znajomych")
            }
    )
    public ResponseEntity<Page<FriendResponse>> getFriends(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Pobieranie znajomych użytkownika: {}", userId);
        Page<FriendResponse> friends = friendshipService.getFriends(userId, page, size);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Pobierz wszystkich znajomych",
            description = "Pobiera listę wszystkich znajomych użytkownika bez paginacji.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista wszystkich znajomych")
            }
    )
    public ResponseEntity<List<FriendResponse>> getAllFriends(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie wszystkich znajomych użytkownika: {}", userId);
        List<FriendResponse> friends = friendshipService.getAllFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @DeleteMapping("/{friendId}")
    @Operation(
            summary = "Usuń znajomego",
            description = "Usuwa użytkownika z listy znajomych.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Znajomy usunięty"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono relacji")
            }
    )
    public ResponseEntity<Void> removeFriend(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "ID znajomego do usunięcia", required = true)
            @PathVariable String friendId) {
        log.info("Usuwanie znajomego: {} przez: {}", friendId, userId);
        friendshipService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    // ==================== BLOKOWANIE ====================

    @PostMapping("/block/{userToBlockId}")
    @Operation(
            summary = "Zablokuj użytkownika",
            description = "Blokuje użytkownika - nie będzie mógł wysyłać zaproszeń ani widzieć Twoich kursów.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Użytkownik zablokowany"),
                    @ApiResponse(responseCode = "400", description = "Nie możesz zablokować siebie"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono użytkownika")
            }
    )
    public ResponseEntity<Void> blockUser(
            @Parameter(description = "ID użytkownika blokującego", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "ID użytkownika do zablokowania", required = true)
            @PathVariable String userToBlockId) {
        log.info("Blokowanie użytkownika: {} przez: {}", userToBlockId, userId);
        friendshipService.blockUser(userId, userToBlockId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/unblock/{userToUnblockId}")
    @Operation(
            summary = "Odblokuj użytkownika",
            description = "Odblokowuje wcześniej zablokowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Użytkownik odblokowany"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik nie jest zablokowany"),
                    @ApiResponse(responseCode = "403", description = "Nie możesz odblokować tego użytkownika")
            }
    )
    public ResponseEntity<Void> unblockUser(
            @Parameter(description = "ID użytkownika odblokowującego", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "ID użytkownika do odblokowania", required = true)
            @PathVariable String userToUnblockId) {
        log.info("Odblokowanie użytkownika: {} przez: {}", userToUnblockId, userId);
        friendshipService.unblockUser(userId, userToUnblockId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blocked")
    @Operation(
            summary = "Pobierz zablokowanych użytkowników",
            description = "Pobiera listę zablokowanych przez Ciebie użytkowników.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista zablokowanych użytkowników")
            }
    )
    public ResponseEntity<Page<FriendResponse>> getBlockedUsers(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Pobieranie zablokowanych użytkowników przez: {}", userId);
        Page<FriendResponse> blocked = friendshipService.getBlockedUsers(userId, page, size);
        return ResponseEntity.ok(blocked);
    }

    // ==================== WYSZUKIWANIE I STATYSTYKI ====================

    @GetMapping("/search")
    @Operation(
            summary = "Wyszukaj użytkowników",
            description = "Wyszukuje użytkowników do dodania do znajomych po nazwie użytkownika, emailu, imieniu lub nazwisku.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Wyniki wyszukiwania")
            }
    )
    public ResponseEntity<Page<UserSearchResponse>> searchUsers(
            @Parameter(description = "ID użytkownika wyszukującego", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "Fraza wyszukiwania", required = true)
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Wyszukiwanie użytkowników: '{}' przez: {}", query, userId);
        Page<UserSearchResponse> results = friendshipService.searchUsers(userId, query, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Pobierz statystyki znajomych",
            description = "Pobiera statystyki dotyczące znajomych i zaproszeń użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Statystyki znajomych")
            }
    )
    public ResponseEntity<FriendshipStatsResponse> getFriendshipStats(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId) {
        log.debug("Pobieranie statystyk znajomych dla: {}", userId);
        FriendshipStatsResponse stats = friendshipService.getFriendshipStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/check/{otherUserId}")
    @Operation(
            summary = "Sprawdź czy są znajomymi",
            description = "Sprawdza czy dwóch użytkowników jest znajomymi.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Wynik sprawdzenia")
            }
    )
    public ResponseEntity<Boolean> areFriends(
            @Parameter(description = "ID pierwszego użytkownika", required = true)
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "ID drugiego użytkownika", required = true)
            @PathVariable String otherUserId) {
        log.debug("Sprawdzanie czy {} i {} są znajomymi", userId, otherUserId);
        boolean areFriends = friendshipService.areFriends(userId, otherUserId);
        return ResponseEntity.ok(areFriends);
    }
}
