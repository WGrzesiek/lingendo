package com.learnwords.statisticsservice.controller;

import com.learnwords.statisticsservice.dto.teacher.*;
import com.learnwords.statisticsservice.service.TeacherDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler odpowiedzialny za zwracanie danych do dashboardu nauczyciela.
 * Zapewnia endpoint do pobierania statystyk, listy uczniów, kursów oraz aktywności.
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/dashboard/teacher")
@Tag(name = "Dashboard nauczyciela", description = "API do pobierania danych na potrzeby dashboardu nauczyciela")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final TeacherDashboardService teacherDashboardService;

    @Operation(
            summary = "Pobierz kompletny dashboard nauczyciela",
            description = "Zwraca wszystkie dane potrzebne do wyświetlenia dashboardu: statystyki, najlepszych uczniów, kursy i aktywność."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dane dashboardu zostały poprawnie pobrane",
                    content = @Content(schema = @Schema(implementation = TeacherDashboardDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Brak nagłówka X-User-Id",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<TeacherDashboardDto> getTeacherDashboard(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie pełnego dashboardu dla nauczyciela: {}", teacherId);

        TeacherDashboardDto dashboard = teacherDashboardService.getTeacherDashboard(teacherId);

        log.info("Zwrócono dashboard dla nauczyciela: {}", teacherId);
        return ResponseEntity.ok(dashboard);
    }

    @Operation(
            summary = "Pobierz statystyki nauczyciela",
            description = "Zwraca statystyki: aktywni uczniowie, kursy, ukończone lekcje, średni postęp z trendami."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Statystyki pobrane poprawnie",
                    content = @Content(schema = @Schema(implementation = TeacherDashboardStatsDto.class))
            )
    })
    @GetMapping("/stats")
    public ResponseEntity<TeacherDashboardStatsDto> getTeacherStats(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie statystyk dla nauczyciela: {}", teacherId);
        return ResponseEntity.ok(teacherDashboardService.getTeacherStats(teacherId));
    }

    @Operation(
            summary = "Pobierz najlepszych uczniów",
            description = "Zwraca listę najlepszych uczniów nauczyciela posortowaną według punktów i postępu."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista uczniów pobrana poprawnie",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TeacherStudentDto.class)))
            )
    })
    @GetMapping("/students/top")
    public ResponseEntity<List<TeacherStudentDto>> getTopStudents(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "Maksymalna liczba uczniów (domyślnie 5)")
            @RequestParam(defaultValue = "5") int limit
    ) {
        log.debug("Pobieranie top {} uczniów dla nauczyciela: {}", limit, teacherId);
        return ResponseEntity.ok(teacherDashboardService.getTopStudents(teacherId, limit));
    }

    @Operation(
            summary = "Pobierz wszystkich uczniów",
            description = "Zwraca listę wszystkich uczniów nauczyciela z ich statystykami."
    )
    @GetMapping("/students")
    public ResponseEntity<List<TeacherStudentDto>> getAllStudents(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie wszystkich uczniów dla nauczyciela: {}", teacherId);
        return ResponseEntity.ok(teacherDashboardService.getAllStudents(teacherId));
    }

    @Operation(
            summary = "Pobierz kursy nauczyciela",
            description = "Zwraca listę kursów nauczyciela z liczbą uczniów i postępem ukończenia."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista kursów pobrana poprawnie",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TeacherCourseDto.class)))
            )
    })
    @GetMapping("/courses")
    public ResponseEntity<List<TeacherCourseDto>> getTeacherCourses(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "Maksymalna liczba kursów (domyślnie 5)")
            @RequestParam(defaultValue = "5") int limit
    ) {
        log.debug("Pobieranie {} kursów dla nauczyciela: {}", limit, teacherId);
        return ResponseEntity.ok(teacherDashboardService.getRecentCourses(teacherId, limit));
    }

    @Operation(
            summary = "Pobierz aktywność uczniów",
            description = "Zwraca feed aktywności uczniów nauczyciela: ukończone lekcje, nowi uczniowie, postępy w kursach."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Aktywność pobrana poprawnie",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TeacherActivityItemDto.class)))
            )
    })
    @GetMapping("/activity")
    public ResponseEntity<List<TeacherActivityItemDto>> getActivityFeed(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId,
            @Parameter(description = "Maksymalna liczba aktywności (domyślnie 10)")
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.debug("Pobieranie {} aktywności dla nauczyciela: {}", limit, teacherId);
        return ResponseEntity.ok(teacherDashboardService.getActivityFeed(teacherId, limit));
    }
}
