package com.learnwords.vocabularycommandservice.controller;

import com.learnwords.common.dto.SendWordFromKafkaDto;
import com.learnwords.vocabularycommandservice.dto.CreateWordDto;
import com.learnwords.vocabularycommandservice.dto.SendWordDto;
import com.learnwords.vocabularycommandservice.service.VocabularyService;
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

import java.util.List;

/**
 * Kontroler REST API dla operacji tworzenia słownictwa (Command Side - CQRS).
 * 
 * <p>Udostępnia endpointy do tworzenia nowych słów wraz z tłumaczeniami 
 * i przykładowymi zdaniami. Implementuje wzorzec CQRS - odpowiada tylko za zapis danych.
 * 
 * <p>Dostępne endpointy:
 * <ul>
 *   <li>POST /api/v1/vocabulary/create - tworzy pojedyncze słówko (standalone)</li>
 *   <li>POST /api/v1/vocabulary/deck/{deckId}/create - tworzy słówko dla konkretnego decka</li>
 *   <li>POST /api/v1/vocabulary/create-batch - tworzy wiele słówek (batch)</li>
 *   <li>POST /api/v1/vocabulary/deck/{deckId}/create-batch - tworzy wiele słówek dla decka (batch)</li>
 * </ul>
 * 
 * <p>Wszystkie operacje używają wzorca Outbox Pattern do zapewnienia eventual consistency.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see VocabularyService
 * @see CreateWordDto
 * @see SendWordDto
 */
@RestController
@RequestMapping("/api/v1/vocabulary")
@Tag(name = "Vocabulary Command", description = "Operacje tworzenia słownictwa (CQRS Write Side)")
public class VocabularyController {
    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService){
        this.vocabularyService = vocabularyService;
    }

    /**
     * Tworzy nowe słówko bez przypisania do konkretnego decka.
     * 
     * <p>Słówko zostanie zapisane jako standalone i może zostać później dodane do decka.
     * Można dołączyć przykładowe zdania w request body.
     * 
     * @param createWordDto dane nowego słówka (słowo, tłumaczenia, opcjonalne zdania)
     * @return ResponseEntity z danymi utworzonego słówka (201 CREATED)
     * @throws IllegalArgumentException gdy słowo lub tłumaczenia są null
     */
    @Operation(
        summary = "Utwórz słówko", 
        description = "Tworzy nowe słówko bez przypisania do decka. Można dołączyć przykładowe zdania."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Słówko utworzone pomyślnie",
            content = @Content(schema = @Schema(implementation = SendWordFromKafkaDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
        @ApiResponse(responseCode = "500", description = "Błąd serwera podczas zapisu")
    })
    @PostMapping("/create")
    public ResponseEntity<SendWordFromKafkaDto> createVocabulary(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dane nowego słówka",
                required = true,
                content = @Content(schema = @Schema(implementation = CreateWordDto.class))
            )
            @Valid @RequestBody CreateWordDto createWordDto) {
        SendWordFromKafkaDto saveVocabulary = vocabularyService.createVocabulary(createWordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabulary);
    }

    /**
     * Tworzy nowe słówko przypisane do konkretnego decka.
     * 
     * <p>Słówko zostanie automatycznie dodane do wskazanego decka.
     * Zalecane gdy wiesz, że słówko ma należeć do konkretnego zestawu.
     * 
     * @param deckId ID decka, do którego zostanie dodane słówko
     * @param createWordDto dane nowego słówka (słowo, tłumaczenia, opcjonalne zdania)
     * @return ResponseEntity z danymi utworzonego słówka (201 CREATED)
     * @throws IllegalArgumentException gdy deckId jest null/pusty lub dane słówka nieprawidłowe
     */
    @Operation(
        summary = "Utwórz słówko dla decka", 
        description = "Tworzy nowe słówko i automatycznie przypisuje do wskazanego decka"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Słówko utworzone i przypisane do decka",
            content = @Content(schema = @Schema(implementation = SendWordDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane lub brak deckId"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
        @ApiResponse(responseCode = "404", description = "Deck nie istnieje"),
        @ApiResponse(responseCode = "500", description = "Błąd serwera podczas zapisu")
    })
    @PostMapping("/deck/{deckId}/create")
    public ResponseEntity<SendWordFromKafkaDto> createVocabularyForDeck(
            @Parameter(description = "ID decka", required = true, example = "deck-123")
            @PathVariable String deckId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dane nowego słówka",
                required = true,
                content = @Content(schema = @Schema(implementation = CreateWordDto.class))
            )
            @Valid @RequestBody CreateWordDto createWordDto) {
        SendWordFromKafkaDto saveVocabulary = vocabularyService.createVocabularyForDeck(createWordDto, deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabulary);
    }

    /**
     * Tworzy wiele słówek jednocześnie (batch operation).
     * 
     * <p>Operacja batch pozwala zaoszczędzić czas przy dodawaniu większej liczby słów.
     * W przypadku błędu przy jednym słówku, pozostałe są nadal zapisywane.
     * 
     * @param createWordDtos lista danych nowych słówek
     * @return ResponseEntity z listą utworzonych słówek (201 CREATED)
     * @throws IllegalArgumentException gdy lista jest null lub pusta
     */
    @Operation(
        summary = "Utwórz wiele słówek (batch)", 
        description = "Tworzy wiele słówek jednocześnie bez przypisania do decka. Operacja kontynuuje mimo błędów pojedynczych elementów."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Słówka utworzone pomyślnie (może być częściowo)",
            content = @Content(schema = @Schema(implementation = SendWordDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Pusta lista lub nieprawidłowe dane"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
        @ApiResponse(responseCode = "500", description = "Błąd serwera podczas zapisu")
    })
    @PostMapping("/create-batch")
    public ResponseEntity<List<SendWordFromKafkaDto>> createVocabularies(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Lista danych nowych słówek",
                required = true
            )
            @Valid @RequestBody List<CreateWordDto> createWordDtos) {
        List<SendWordFromKafkaDto> saveVocabularies = vocabularyService.createVocabularies(createWordDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabularies);
    }

    /**
     * Tworzy wiele słówek jednocześnie i przypisuje je do decka (batch operation).
     * 
     * <p>Najszybszy sposób na dodanie wielu słów do konkretnego decka naraz.
     * W przypadku błędu przy jednym słówku, pozostałe są nadal zapisywane.
     * 
     * @param deckId ID decka, do którego zostaną dodane wszystkie słówka
     * @param createWordDtos lista danych nowych słówek
     * @return ResponseEntity z listą utworzonych słówek (201 CREATED)
     * @throws IllegalArgumentException gdy deckId jest null/pusty lub lista jest pusta
     */
    @Operation(
        summary = "Utwórz wiele słówek dla decka (batch)", 
        description = "Tworzy wiele słówek jednocześnie i przypisuje wszystkie do wskazanego decka. Operacja kontynuuje mimo błędów pojedynczych elementów."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Słówka utworzone i przypisane do decka (może być częściowo)",
            content = @Content(schema = @Schema(implementation = SendWordDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Brak deckId, pusta lista lub nieprawidłowe dane"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
        @ApiResponse(responseCode = "404", description = "Deck nie istnieje"),
        @ApiResponse(responseCode = "500", description = "Błąd serwera podczas zapisu")
    })
    @PostMapping("/deck/{deckId}/create-batch")
    public ResponseEntity<List<SendWordFromKafkaDto>> createVocabulariesForDeck(
            @Parameter(description = "ID decka", required = true, example = "deck-123")
            @PathVariable String deckId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Lista danych nowych słówek",
                required = true
            )
            @Valid @RequestBody List<CreateWordDto> createWordDtos) {
        List<SendWordFromKafkaDto> saveVocabularies = vocabularyService.createVocabulariesForDeck(createWordDtos, deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabularies);
    }
}
