package com.learnwords.statisticsservice.controller;

import com.learnwords.statisticsservice.dto.StudentDashboardDto;
import com.learnwords.statisticsservice.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kontroler odpowiedzialny za zwracanie danych do dashboardu użytkownika.
 *
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 * @see DashboardService
 * @see StudentDashboardDto
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/dashboard")
@Tag(name = "Dashboard", description = "API do pobierania danych na potrzeby dashboardu użytkownika")
public class DashboardController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(
            summary = "Pobierz dane dashboardu użytkownika",
            description = "Zwraca podsumowanie aktywności użytkownika: aktywne talie, ukończone lekcje, serię dni nauki oraz punkty.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Dane dashboardu zostały poprawnie pobrane",
                            content = @Content(schema = @Schema(implementation = StudentDashboardDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Nieprawidłowe dane wejściowe",
                            content = @Content
                    )
            }
    )
    @GetMapping("/student")
    public ResponseEntity<StudentDashboardDto> getStudentDashboard(
            @Parameter(
                    description = "ID użytkownika pobierane z nagłówka",
                    required = true,
                    example = "user-123"
            )
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie danych dashboardu dla użytkownika - userId: {}", userId);

        StudentDashboardDto dashboard = dashboardService.getStudentDashboard(userId);

        log.info("Zwrócono dane dashboardu dla użytkownika - userId: {}", userId);
        return ResponseEntity.ok(dashboard);
    }
}
