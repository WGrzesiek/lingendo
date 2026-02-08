package com.learnwords.statisticsservice.controller;

import com.learnwords.statisticsservice.dto.PdfExportOptionsDto;
import com.learnwords.statisticsservice.service.PdfService;
import com.learnwords.statisticsservice.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/stats")
@Tag(name = "Statistics Service", description = "API for statistics service")
public class StatsController {
    private static final String USER_ID_HEADER = "X-User-Id";
    private final StatsService statsService;
    private final PdfService pdfService;

    public StatsController(StatsService statsService, PdfService pdfService) {
        this.statsService = statsService;
        this.pdfService = pdfService;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestHeader(USER_ID_HEADER) String userId
    )
    {
        Map<String, Object> stats = statsService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/export/pdf")
    @Operation(
            summary = "Eksportuj statystyki do PDF",
            description = "Generuje i pobiera raport PDF ze statystykami użytkownika. Możliwość wyboru sekcji i zakresu czasowego.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Raport PDF został wygenerowany pomyślnie",
                            content = @Content(mediaType = "application/pdf")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Nieprawidłowe dane wejściowe",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Błąd podczas generowania raportu",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<byte[]> exportStatsToPdf(
            @Parameter(
                    description = "ID użytkownika pobierane z nagłówka",
                    required = true,
                    example = "user-123"
            )
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestBody(required = false) PdfExportOptionsDto options) {
        
        PdfExportOptionsDto exportOptions = options != null ? options : PdfExportOptionsDto.createDefault();
        
        byte[] pdf = pdfService.generateStatsPdf(userId, exportOptions);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"learnwords-statistics-" + userId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
