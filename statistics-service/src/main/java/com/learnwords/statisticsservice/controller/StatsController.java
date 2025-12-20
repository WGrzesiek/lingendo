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
            description = "Generuje i pobiera raport PDF z kompletnymi statystykami użytkownika. Możliwość wyboru sekcji do uwzględnienia.",
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
            @RequestBody(required = false) PdfExportOptionsDto options
    ) {
        log.info("Rozpoczęcie eksportu statystyk do PDF dla użytkownika: {} z opcjami: {}", userId, options);

        try {
            PdfExportOptionsDto exportOptions = options != null ? options : PdfExportOptionsDto.createDefault();
            
            byte[] pdfBytes = pdfService.generateStatsPdf(userId, exportOptions);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = String.format("statystyki_learnwords_%s.pdf", timestamp);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            log.info("Pomyślnie wygenerowano PDF dla użytkownika: {} (rozmiar: {} KB)",
                    userId, pdfBytes.length / 1024);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("Błąd podczas eksportu statystyk do PDF dla użytkownika: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
