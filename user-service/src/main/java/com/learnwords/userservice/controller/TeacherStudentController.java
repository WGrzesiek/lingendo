package com.learnwords.userservice.controller;

import com.learnwords.userservice.dtos.teacher.*;
import com.learnwords.userservice.service.TeacherStudentService;
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

/**
 * Kontroler REST do zarządzania relacjami nauczyciel-uczeń
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/teacher-student")
@Tag(name = "Relacje Nauczyciel-Uczeń", description = "API do zarządzania relacjami między nauczycielami a uczniami")
public class TeacherStudentController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final TeacherStudentService teacherStudentService;

    public TeacherStudentController(TeacherStudentService teacherStudentService) {
        this.teacherStudentService = teacherStudentService;
    }

    // ==================== OPERACJE NAUCZYCIELA - ZAPROSZENIA ====================

    @PostMapping("/invitations")
    @Operation(
            summary = "Utwórz zaproszenie",
            description = "Tworzy nowe zaproszenie, które nauczyciel może wysłać uczniom. " +
                    "Zaproszenie zawiera unikalny kod, który uczniowie mogą wykorzystać do dołączenia.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Zaproszenie zostało utworzone",
                            content = @Content(schema = @Schema(implementation = InvitationResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
                    @ApiResponse(responseCode = "401", description = "Brak autoryzacji")
            }
    )
    public ResponseEntity<InvitationResponse> createInvitation(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Valid @RequestBody CreateInvitationRequest request) {
        log.info("Tworzenie zaproszenia przez nauczyciela: {}", teacherId);
        InvitationResponse response = teacherStudentService.createInvitation(teacherId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/invitations")
    @Operation(
            summary = "Pobierz zaproszenia nauczyciela",
            description = "Pobiera listę wszystkich zaproszeń utworzonych przez nauczyciela.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista zaproszeń")
            }
    )
    public ResponseEntity<Page<InvitationResponse>> getTeacherInvitations(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "Numer strony (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Rozmiar strony", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Pobieranie zaproszeń nauczyciela: {}", teacherId);
        Page<InvitationResponse> invitations = teacherStudentService.getTeacherInvitations(teacherId, page, size);
        return ResponseEntity.ok(invitations);
    }

    @PatchMapping("/invitations/{invitationId}/deactivate")
    @Operation(
            summary = "Dezaktywuj zaproszenie",
            description = "Dezaktywuje zaproszenie - nie będzie można go już użyć.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Zaproszenie dezaktywowane"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono zaproszenia"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień")
            }
    )
    public ResponseEntity<Void> deactivateInvitation(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID zaproszenia", required = true)
            @PathVariable String invitationId) {
        log.info("Dezaktywacja zaproszenia: {} przez nauczyciela: {}", invitationId, teacherId);
        teacherStudentService.deactivateInvitation(teacherId, invitationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/invitations/{invitationId}")
    @Operation(
            summary = "Usuń zaproszenie",
            description = "Trwale usuwa zaproszenie.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Zaproszenie usunięte"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono zaproszenia"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień")
            }
    )
    public ResponseEntity<Void> deleteInvitation(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID zaproszenia", required = true)
            @PathVariable String invitationId) {
        log.info("Usuwanie zaproszenia: {} przez nauczyciela: {}", invitationId, teacherId);
        teacherStudentService.deleteInvitation(teacherId, invitationId);
        return ResponseEntity.noContent().build();
    }

    // ==================== OPERACJE NAUCZYCIELA - UCZNIOWIE ====================

    @GetMapping("/students")
    @Operation(
            summary = "Pobierz uczniów",
            description = "Pobiera listę wszystkich uczniów przypisanych do nauczyciela.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista uczniów")
            }
    )
    public ResponseEntity<Page<StudentResponse>> getStudents(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "Numer strony (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Rozmiar strony", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Pobieranie uczniów nauczyciela: {}", teacherId);
        Page<StudentResponse> students = teacherStudentService.getStudents(teacherId, page, size);
        return ResponseEntity.ok(students);
    }

    @DeleteMapping("/students/{studentId}")
    @Operation(
            summary = "Usuń ucznia",
            description = "Usuwa ucznia z listy nauczyciela.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Uczeń usunięty"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono ucznia")
            }
    )
    public ResponseEntity<Void> removeStudent(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID ucznia", required = true)
            @PathVariable String studentId) {
        log.info("Usuwanie ucznia: {} przez nauczyciela: {}", studentId, teacherId);
        teacherStudentService.removeStudent(teacherId, studentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/students/{studentId}/block")
    @Operation(
            summary = "Zablokuj ucznia",
            description = "Blokuje ucznia - nie będzie mógł korzystać z materiałów nauczyciela.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Uczeń zablokowany"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono ucznia")
            }
    )
    public ResponseEntity<Void> blockStudent(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID ucznia", required = true)
            @PathVariable String studentId) {
        log.info("Blokowanie ucznia: {} przez nauczyciela: {}", studentId, teacherId);
        teacherStudentService.blockStudent(teacherId, studentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/students/{studentId}/unblock")
    @Operation(
            summary = "Odblokuj ucznia",
            description = "Odblokowuje wcześniej zablokowanego ucznia.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Uczeń odblokowany"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono ucznia")
            }
    )
    public ResponseEntity<Void> unblockStudent(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "ID ucznia", required = true)
            @PathVariable String studentId) {
        log.info("Odblokowanie ucznia: {} przez nauczyciela: {}", studentId, teacherId);
        teacherStudentService.unblockStudent(teacherId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Pobierz statystyki nauczyciela",
            description = "Pobiera statystyki dotyczące uczniów i zaproszeń nauczyciela.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Statystyki nauczyciela")
            }
    )
    public ResponseEntity<TeacherStatsResponse> getTeacherStats(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId) {
        log.debug("Pobieranie statystyk nauczyciela: {}", teacherId);
        TeacherStatsResponse stats = teacherStudentService.getTeacherStats(teacherId);
        return ResponseEntity.ok(stats);
    }

    // ==================== OPERACJE UCZNIA ====================

    @PostMapping("/join")
    @Operation(
            summary = "Dołącz do nauczyciela",
            description = "Dołącza ucznia do nauczyciela za pomocą kodu zaproszenia.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Pomyślnie dołączono do nauczyciela",
                            content = @Content(schema = @Schema(implementation = TeacherResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowy kod zaproszenia"),
                    @ApiResponse(responseCode = "409", description = "Relacja już istnieje")
            }
    )
    public ResponseEntity<TeacherResponse> joinTeacher(
            @Parameter(description = "ID ucznia", required = true)
            @RequestHeader(USER_ID_HEADER) String studentId,
            @Valid @RequestBody JoinTeacherRequest request) {
        log.info("Uczeń: {} próbuje dołączyć za pomocą kodu: {}", studentId, request.invitationCode());
        TeacherResponse response = teacherStudentService.joinTeacher(studentId, request.invitationCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-teachers")
    @Operation(
            summary = "Pobierz moich nauczycieli",
            description = "Pobiera listę nauczycieli, do których uczeń jest przypisany.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista nauczycieli")
            }
    )
    public ResponseEntity<Page<TeacherResponse>> getMyTeachers(
            @Parameter(description = "ID ucznia", required = true)
            @RequestHeader(USER_ID_HEADER) String studentId,
            @Parameter(description = "Numer strony (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Rozmiar strony", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Pobieranie nauczycieli ucznia: {}", studentId);
        Page<TeacherResponse> teachers = teacherStudentService.getMyTeachers(studentId, page, size);
        return ResponseEntity.ok(teachers);
    }

    @DeleteMapping("/my-teachers/{teacherId}")
    @Operation(
            summary = "Opuść nauczyciela",
            description = "Usuwa relację ucznia z nauczycielem.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Pomyślnie opuszczono nauczyciela"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono relacji")
            }
    )
    public ResponseEntity<Void> leaveTeacher(
            @Parameter(description = "ID ucznia", required = true)
            @RequestHeader(USER_ID_HEADER) String studentId,
            @Parameter(description = "ID nauczyciela", required = true)
            @PathVariable String teacherId) {
        log.info("Uczeń: {} opuszcza nauczyciela: {}", studentId, teacherId);
        teacherStudentService.leaveTeacher(studentId, teacherId);
        return ResponseEntity.noContent().build();
    }

    // ==================== PUBLICZNE ENDPOINTY ====================

    @GetMapping("/invitations/{code}/info")
    @Operation(
            summary = "Pobierz informacje o zaproszeniu",
            description = "Pobiera publiczne informacje o zaproszeniu (do wyświetlenia przed dołączeniem).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Informacje o zaproszeniu",
                            content = @Content(schema = @Schema(implementation = InvitationResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono zaproszenia")
            }
    )
    public ResponseEntity<InvitationResponse> getInvitationInfo(
            @Parameter(description = "Kod zaproszenia", required = true)
            @PathVariable String code) {
        log.debug("Pobieranie informacji o zaproszeniu: {}", code);
        InvitationResponse response = teacherStudentService.getInvitationInfo(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{teacherId}/{studentId}")
    @Operation(
            summary = "Sprawdź relację",
            description = "Sprawdza czy istnieje aktywna relacja nauczyciel-uczeń.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Wynik sprawdzenia")
            }
    )
    public ResponseEntity<Boolean> isTeacherOf(
            @Parameter(description = "ID nauczyciela", required = true)
            @PathVariable String teacherId,
            @Parameter(description = "ID ucznia", required = true)
            @PathVariable String studentId) {
        log.debug("Sprawdzanie relacji: {} -> {}", teacherId, studentId);
        boolean isTeacher = teacherStudentService.isTeacherOf(teacherId, studentId);
        return ResponseEntity.ok(isTeacher);
    }
}
