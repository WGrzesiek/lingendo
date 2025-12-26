package com.learnwords.userservice.controller;

import com.learnwords.userservice.dtos.group.*;
import com.learnwords.userservice.service.StudentGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Kontroler REST do zarządzania grupami uczniów.
 * Umożliwia nauczycielom tworzenie grup i zarządzanie ich członkami.
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Grupy Uczniów", description = "API do zarządzania grupami uczniów przez nauczycieli")
public class StudentGroupController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final StudentGroupService groupService;


    @PostMapping
    @Operation(
            summary = "Utwórz grupę",
            description = "Tworzy nową grupę uczniów. Dostępne tylko dla nauczycieli.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Grupa została utworzona",
                            content = @Content(schema = @Schema(implementation = GroupResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
                    @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
                    @ApiResponse(responseCode = "403", description = "Użytkownik nie jest nauczycielem")
            }
    )
    public ResponseEntity<GroupResponse> createGroup(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Valid @RequestBody CreateGroupRequest request) {
        log.info("Tworzenie grupy przez nauczyciela: {}", teacherId);
        GroupResponse response = groupService.createGroup(teacherId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Pobierz grupy nauczyciela",
            description = "Pobiera listę wszystkich grup utworzonych przez nauczyciela.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista grup")
            }
    )
    public ResponseEntity<Page<GroupResponse>> getTeacherGroups(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "Czy uwzględnić zarchiwizowane grupy")
            @RequestParam(defaultValue = "false") boolean includeArchived,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Pobieranie grup nauczyciela: {}", teacherId);
        Page<GroupResponse> groups = groupService.getTeacherGroups(teacherId, includeArchived, pageable);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{groupId}")
    @Operation(
            summary = "Pobierz szczegóły grupy",
            description = "Pobiera szczegółowe informacje o grupie.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Szczegóły grupy"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono grupy")
            }
    )
    public ResponseEntity<GroupResponse> getGroup(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId) {
        log.debug("Pobieranie grupy: {} dla nauczyciela: {}", groupId, teacherId);
        GroupResponse group = groupService.getGroup(teacherId, groupId);
        return ResponseEntity.ok(group);
    }

    @PatchMapping("/{groupId}")
    @Operation(
            summary = "Aktualizuj grupę",
            description = "Aktualizuje dane grupy (nazwa, opis, kolor).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Grupa zaktualizowana"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono grupy"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień")
            }
    )
    public ResponseEntity<GroupResponse> updateGroup(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @Valid @RequestBody UpdateGroupRequest request) {
        log.info("Aktualizacja grupy: {} przez nauczyciela: {}", groupId, teacherId);
        GroupResponse group = groupService.updateGroup(teacherId, groupId, request);
        return ResponseEntity.ok(group);
    }

    @PatchMapping("/{groupId}/archive")
    @Operation(
            summary = "Archiwizuj grupę",
            description = "Archiwizuje grupę - członkowie pozostają, ale grupa staje się tylko do odczytu.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Grupa zarchiwizowana"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono grupy")
            }
    )
    public ResponseEntity<Void> archiveGroup(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId) {
        log.info("Archiwizacja grupy: {} przez nauczyciela: {}", groupId, teacherId);
        groupService.archiveGroup(teacherId, groupId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{groupId}/restore")
    @Operation(
            summary = "Przywróć grupę",
            description = "Przywraca zarchiwizowaną grupę do stanu aktywnego.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Grupa przywrócona"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono grupy"),
                    @ApiResponse(responseCode = "400", description = "Grupa nie jest zarchiwizowana")
            }
    )
    public ResponseEntity<Void> restoreGroup(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId) {
        log.info("Przywracanie grupy: {} przez nauczyciela: {}", groupId, teacherId);
        groupService.restoreGroup(teacherId, groupId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}")
    @Operation(
            summary = "Usuń grupę",
            description = "Usuwa grupę (soft delete).",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Grupa usunięta"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono grupy")
            }
    )
    public ResponseEntity<Void> deleteGroup(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId) {
        log.info("Usuwanie grupy: {} przez nauczyciela: {}", groupId, teacherId);
        groupService.deleteGroup(teacherId, groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Pobierz statystyki grup",
            description = "Pobiera statystyki wszystkich grup nauczyciela.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Statystyki grup")
            }
    )
    public ResponseEntity<GroupStatsResponse> getGroupStats(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId) {
        log.debug("Pobieranie statystyk grup dla nauczyciela: {}", teacherId);
        GroupStatsResponse stats = groupService.getGroupStats(teacherId);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{groupId}/members")
    @Operation(
            summary = "Dodaj uczniów do grupy",
            description = "Dodaje wielu uczniów do grupy. Uczniowie muszą być przypisani do nauczyciela.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Wynik operacji batch"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono grupy")
            }
    )
    public ResponseEntity<BatchMemberOperationResponse> addMembers(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @Valid @RequestBody AddMembersRequest request) {
        log.info("Dodawanie uczniów do grupy: {} przez nauczyciela: {}", groupId, teacherId);
        BatchMemberOperationResponse response = groupService.addMembers(teacherId, groupId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}/members")
    @Operation(
            summary = "Usuń uczniów z grupy",
            description = "Usuwa wielu uczniów z grupy.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Wynik operacji batch"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono grupy")
            }
    )
    public ResponseEntity<BatchMemberOperationResponse> removeMembers(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @Valid @RequestBody RemoveMembersRequest request) {
        log.info("Usuwanie uczniów z grupy: {} przez nauczyciela: {}", groupId, teacherId);
        BatchMemberOperationResponse response = groupService.removeMembers(teacherId, groupId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}/members")
    @Operation(
            summary = "Pobierz członków grupy",
            description = "Pobiera listę wszystkich aktywnych członków grupy.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista członków grupy"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono grupy")
            }
    )
    public ResponseEntity<Page<GroupMemberResponse>> getGroupMembers(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Pobieranie członków grupy: {} dla nauczyciela: {}", groupId, teacherId);
        Page<GroupMemberResponse> members = groupService.getGroupMembers(teacherId, groupId, pageable);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/my")
    @Operation(
            summary = "Pobierz moje grupy (uczeń)",
            description = "Pobiera listę grup, do których należy uczeń.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista grup ucznia")
            }
    )
    public ResponseEntity<Page<GroupResponse>> getStudentGroups(
            @Parameter(description = "ID ucznia", required = true)
            @RequestHeader(USER_ID_HEADER) String studentId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Pobieranie grup ucznia: {}", studentId);
        Page<GroupResponse> groups = groupService.getStudentGroups(studentId, pageable);
        return ResponseEntity.ok(groups);
    }
}
