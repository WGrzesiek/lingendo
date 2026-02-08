package com.learnwords.statisticsservice.controller;

import com.learnwords.statisticsservice.dto.group.*;
import com.learnwords.statisticsservice.service.GroupStatisticsService;
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
 * Kontroler odpowiedzialny za zwracanie statystyk grup.
 * Zapewnia endpointy do pobierania dashboardu grupy, rankingów, postępów członków itp.
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/stats/groups")
@Tag(name = "Statystyki grup", description = "API do pobierania statystyk grup uczniów")
@RequiredArgsConstructor
public class GroupStatisticsController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final GroupStatisticsService groupStatisticsService;

    @Operation(
            summary = "Pobierz listę grup nauczyciela",
            description = "Zwraca listę wszystkich grup nauczyciela z podstawowymi statystykami."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista grup pobrana poprawnie",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupInfoDto.class)))
            )
    })
    @GetMapping
    public ResponseEntity<List<GroupInfoDto>> getTeacherGroups(
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie grup dla nauczyciela: {}", teacherId);
        return ResponseEntity.ok(groupStatisticsService.getTeacherGroups(teacherId));
    }

    @Operation(
            summary = "Pobierz kompletny dashboard grupy",
            description = "Zwraca wszystkie dane potrzebne do wyświetlenia dashboardu grupy: statystyki, najlepszych członków, kursy i aktywność."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dane dashboardu grupy zostały poprawnie pobrane",
                    content = @Content(schema = @Schema(implementation = GroupDashboardDto.class))
            )
    })
    @GetMapping("/{groupId}/dashboard")
    public ResponseEntity<GroupDashboardDto> getGroupDashboard(
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie dashboardu dla grupy: {} przez nauczyciela: {}", groupId, teacherId);
        // NOTE: Dodać walidację, czy nauczyciel jest właścicielem grupy
        return ResponseEntity.ok(groupStatisticsService.getGroupDashboard(groupId));
    }


    @Operation(
            summary = "Pobierz statystyki grupy",
            description = "Zwraca statystyki: członkowie, aktywni członkowie, kursy, ukończone lekcje, średni postęp z trendami."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Statystyki pobrane poprawnie",
                    content = @Content(schema = @Schema(implementation = GroupStatsDto.class))
            )
    })
    @GetMapping("/{groupId}/stats")
    public ResponseEntity<GroupStatsDto> getGroupStats(
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie statystyk dla grupy: {}", groupId);
        return ResponseEntity.ok(groupStatisticsService.getGroupStats(groupId));
    }

    @Operation(
            summary = "Pobierz najlepszych członków grupy",
            description = "Zwraca listę najlepszych członków grupy posortowaną według punktów i postępu."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista członków pobrana poprawnie",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupMemberDto.class)))
            )
    })
    @GetMapping("/{groupId}/members/top")
    public ResponseEntity<List<GroupMemberDto>> getTopMembers(
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @Parameter(description = "Limit wyników", example = "5")
            @RequestParam(defaultValue = "5") int limit,
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie top {} członków dla grupy: {}", limit, groupId);
        return ResponseEntity.ok(groupStatisticsService.getTopMembers(groupId, limit));
    }

    @Operation(
            summary = "Pobierz wszystkich członków grupy",
            description = "Zwraca listę wszystkich członków grupy z ich statystykami."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista członków pobrana poprawnie"
            )
    })
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberDto>> getAllMembers(
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie wszystkich członków dla grupy: {}", groupId);
        return ResponseEntity.ok(groupStatisticsService.getAllMembers(groupId));
    }

    @Operation(
            summary = "Pobierz kursy udostępnione grupie",
            description = "Zwraca listę kursów udostępnionych grupie z postępami członków."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista kursów pobrana poprawnie",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupCourseDto.class)))
            )
    })
    @GetMapping("/{groupId}/courses")
    public ResponseEntity<List<GroupCourseDto>> getSharedCourses(
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @Parameter(description = "Limit wyników", example = "10")
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie kursów dla grupy: {}", groupId);
        return ResponseEntity.ok(groupStatisticsService.getSharedCourses(groupId, limit));
    }

    @Operation(
            summary = "Pobierz feed aktywności grupy",
            description = "Zwraca chronologiczną listę aktywności członków grupy (ukończone lekcje, kursy itp.)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Feed aktywności pobrany poprawnie",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupActivityItemDto.class)))
            )
    })
    @GetMapping("/{groupId}/activity")
    public ResponseEntity<List<GroupActivityItemDto>> getActivityFeed(
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @Parameter(description = "Limit wyników", example = "10")
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie aktywności dla grupy: {} limit: {}", groupId, limit);
        return ResponseEntity.ok(groupStatisticsService.getActivityFeed(groupId, limit));
    }

    @Operation(
            summary = "Pobierz ranking grupy",
            description = "Zwraca ranking członków grupy posortowany według punktów za określony okres."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ranking pobrany poprawnie",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupLeaderboardEntryDto.class)))
            )
    })
    @GetMapping("/{groupId}/leaderboard")
    public ResponseEntity<List<GroupLeaderboardEntryDto>> getLeaderboard(
            @Parameter(description = "ID grupy", required = true)
            @PathVariable String groupId,
            @Parameter(description = "Liczba dni wstecz", example = "30")
            @RequestParam(defaultValue = "30") int days,
            @Parameter(description = "Limit wyników", example = "10")
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "ID nauczyciela", required = true)
            @RequestHeader(USER_ID_HEADER) String teacherId
    ) {
        log.debug("Pobieranie rankingu dla grupy: {} za {} dni", groupId, days);
        return ResponseEntity.ok(groupStatisticsService.getLeaderboard(groupId, days, limit));
    }
}
