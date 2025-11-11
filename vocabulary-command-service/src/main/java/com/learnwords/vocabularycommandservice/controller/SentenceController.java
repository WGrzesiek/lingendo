package com.learnwords.vocabularycommandservice.controller;

import com.learnwords.vocabularycommandservice.dto.SendSentenceDto;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.service.SentenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Kontroler REST API dla operacji tworzenia przykładowych zdań (Command Side - CQRS).
 * 
 * <p>Udostępnia endpointy do tworzenia nowych przykładowych zdań wraz z tłumaczeniami.
 * Zdania mogą być tworzone w kontekście konkretnego decka i używane jako materiał 
 * do nauki słownictwa.
 * 
 * <p>Dostępne endpointy:
 * <ul>
 *   <li>POST /api/v1/sentences/create/{deckId} - tworzy nowe zdanie dla konkretnego decka</li>
 * </ul>
 * 
 * <p>Wszystkie operacje używają wzorca Outbox Pattern do zapewnienia eventual consistency.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see SentenceService
 * @see CreateSentenceDto
 * @see SendSentenceDto
 */
@RestController
@RequestMapping("/api/v1/sentences")
@Tag(name = "Sentences Command", description = "Operacje tworzenia przykładowych zdań (CQRS Write Side)")
public class SentenceController {

    private final SentenceService sentenceService;

    public SentenceController(SentenceService sentenceService){
        this.sentenceService=sentenceService;
    }

    /**
     * Tworzy nowe przykładowe zdanie dla konkretnego decka.
     * 
     * <p>Zdanie może być później wykorzystane jako przykład użycia słownictwa
     * z danego decka. Zawiera zdanie w języku źródłowym i tłumaczenie.
     * 
     * <p>Przykład użycia:
     * <pre>
     * {
     *   "sentence": "Hello, how are you?",
     *   "translation": "Cześć, jak się masz?"
     * }
     * </pre>
     * 
     * @param deckId ID decka, do którego zostanie przypisane zdanie
     * @param sentenceDto dane nowego zdania (zdanie i tłumaczenie)
     * @return ResponseEntity z danymi utworzonego zdania (201 CREATED)
     * @throws IllegalArgumentException gdy deckId jest null/pusty lub dane zdania nieprawidłowe
     */
    @Operation(
        summary = "Utwórz zdanie dla decka", 
        description = "Tworzy nowe przykładowe zdanie wraz z tłumaczeniem i przypisuje do wskazanego decka"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Zdanie utworzone pomyślnie",
            content = @Content(schema = @Schema(implementation = SendSentenceDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane lub brak deckId"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
        @ApiResponse(responseCode = "404", description = "Deck nie istnieje"),
        @ApiResponse(responseCode = "500", description = "Błąd serwera podczas zapisu")
    })
    @PostMapping("/create/{deckId}")
    public ResponseEntity<SendSentenceDto> createSentence(
            @Parameter(description = "ID decka", required = true, example = "deck-123")
            @PathVariable String deckId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dane nowego zdania (zdanie i tłumaczenie)",
                required = true,
                content = @Content(schema = @Schema(implementation = CreateSentenceDto.class))
            )
            @Valid @RequestBody CreateSentenceDto sentenceDto) {
        SendSentenceDto savedSentence = sentenceService.createSentence(sentenceDto, deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSentence);
    }
}
