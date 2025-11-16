package com.learnwords.vocabularycommandservice.controller;

import com.learnwords.common.dto.SendWordFromKafkaDto;
import com.learnwords.vocabularycommandservice.dto.CreateWordDto;
import com.learnwords.vocabularycommandservice.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
 * <h2>Główne funkcjonalności:</h2>
 * <ul>
 *   <li><b>Tworzenie pojedynczych słów:</b>
 *     <ul>
 *       <li>Tworzenie słówka standalone (bez przypisania do decka)</li>
 *       <li>Tworzenie słówka przypisanego do konkretnego decka</li>
 *       <li>Automatyczne tworzenie powiązanych zdań przykładowych</li>
 *     </ul>
 *   </li>
 *   <li><b>Operacje batch:</b>
 *     <ul>
 *       <li>Tworzenie wielu słów jednocześnie</li>
 *       <li>Tworzenie wielu słów dla decka (batch)</li>
 *       <li>Mechanizm fail-safe - błędy pojedynczych słów nie blokują pozostałych</li>
 *     </ul>
 *   </li>
 *   <li><b>Eventual Consistency:</b>
 *     <ul>
 *       <li>Używanie wzorca Outbox Pattern do publikacji eventów</li>
 *       <li>Gwarancja dostarczenia eventów do message brokera</li>
 *       <li>Synchronizacja między Command i Read Side</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h2>Endpointy:</h2>
 * <ul>
 *   <li>POST /api/v1/vocabulary/create - Utwórz słówko (standalone)</li>
 *   <li>POST /api/v1/vocabulary/deck/{deckId}/create - Utwórz słówko dla decka</li>
 *   <li>POST /api/v1/vocabulary/create-batch - Utwórz wiele słówek (batch)</li>
 *   <li>POST /api/v1/vocabulary/deck/{deckId}/create-batch - Utwórz wiele słówek dla decka (batch)</li>
 * </ul>
 * 
 * <h2>Autoryzacja:</h2>
 * <p>Wszystkie endpointy wymagają autoryzacji użytkownika.
 * 
 * <h2>Obsługa błędów:</h2>
 * <p>Kontroler korzysta z globalnej obsługi wyjątków. Możliwe kody odpowiedzi:
 * <ul>
 *   <li><b>201 Created:</b> Słówko/słówka utworzone pomyślnie</li>
 *   <li><b>400 Bad Request:</b> Błędne dane wejściowe lub walidacja niepomyślna</li>
 *   <li><b>401 Unauthorized:</b> Brak autoryzacji</li>
 *   <li><b>404 Not Found:</b> Deck nie istnieje</li>
 *   <li><b>500 Internal Server Error:</b> Nieoczekiwany błąd serwera</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see VocabularyService
 * @see CreateWordDto
 * @see SendWordFromKafkaDto
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/vocabulary")
@Tag(name = "Vocabulary Management", description = "API do zarządzania słownictwem")
public class VocabularyController {
    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService){
        this.vocabularyService = vocabularyService;
    }

    /**
     * Tworzy nowe słówko bez przypisania do decka.
     * 
     * @param createWordDto dane nowego słówka
     * @return dane utworzonego słówka
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
        @ApiResponse(responseCode = "500", description = "Błąd serwera")
    })
    @PostMapping("/create")
    public ResponseEntity<SendWordFromKafkaDto> createVocabulary(
            @Parameter(description = "Dane nowego słówka", required = true)
            @Valid @RequestBody CreateWordDto createWordDto) {
        log.debug("Tworzenie słówka - słowo: '{}'", createWordDto.getWord());
        SendWordFromKafkaDto saveVocabulary = vocabularyService.createVocabulary(createWordDto);
        log.info("Słówko utworzone - wordId: '{}', słowo: '{}'", saveVocabulary.id(), saveVocabulary.word());
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabulary);
    }

    /**
     * Tworzy nowe słówko przypisane do decka.
     * 
     * @param deckId ID decka
     * @param createWordDto dane nowego słówka
     * @return dane utworzonego słówka
     */
    @Operation(
        summary = "Utwórz słówko dla decka", 
        description = "Tworzy nowe słówko i przypisuje do wskazanego decka"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Słówko utworzone i przypisane do decka",
            content = @Content(schema = @Schema(implementation = SendWordFromKafkaDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane lub brak deckId"),
        @ApiResponse(responseCode = "404", description = "Deck nie istnieje"),
        @ApiResponse(responseCode = "500", description = "Błąd serwera")
    })
    @PostMapping("/deck/{deckId}/create")
    public ResponseEntity<SendWordFromKafkaDto> createVocabularyForDeck(
            @Parameter(description = "ID decka", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "Dane nowego słówka", required = true)
            @Valid @RequestBody CreateWordDto createWordDto) {
        log.debug("Tworzenie słówka dla decka - deckId: '{}', słowo: '{}'", deckId, createWordDto.getWord());
        SendWordFromKafkaDto saveVocabulary = vocabularyService.createVocabularyForDeck(createWordDto, deckId);
        log.info("Słówko utworzone dla decka - wordId: '{}', deckId: '{}'", saveVocabulary.id(), deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabulary);
    }

    /**
     * Tworzy wiele słówek jednocześnie (batch).
     * 
     * @param createWordDtos lista danych nowych słówek
     * @return lista utworzonych słówek
     */
    @Operation(
        summary = "Utwórz wiele słówek (batch)", 
        description = "Tworzy wiele słówek jednocześnie. Operacja kontynuuje mimo błędów pojedynczych elementów."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Słówka utworzone pomyślnie",
            content = @Content(schema = @Schema(implementation = SendWordFromKafkaDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Pusta lista lub nieprawidłowe dane"),
        @ApiResponse(responseCode = "500", description = "Błąd serwera")
    })
    @PostMapping("/create-batch")
    public ResponseEntity<List<SendWordFromKafkaDto>> createVocabularies(
            @Parameter(description = "Lista danych nowych słówek", required = true)
            @Valid @RequestBody List<CreateWordDto> createWordDtos) {
        log.debug("Tworzenie słówek batch - liczba: {}", createWordDtos.size());
        List<SendWordFromKafkaDto> saveVocabularies = vocabularyService.createVocabularies(createWordDtos);
        log.info("Słówka batch utworzone - liczba: {}", saveVocabularies.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabularies);
    }

    /**
     * Tworzy wiele słówek dla decka jednocześnie (batch).
     * 
     * @param deckId ID decka
     * @param createWordDtos lista danych nowych słówek
     * @return lista utworzonych słówek
     */
    @Operation(
        summary = "Utwórz wiele słówek dla decka (batch)", 
        description = "Tworzy wiele słówek jednocześnie i przypisuje wszystkie do decka. Operacja kontynuuje mimo błędów pojedynczych elementów."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Słówka utworzone i przypisane do decka",
            content = @Content(schema = @Schema(implementation = SendWordFromKafkaDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Brak deckId, pusta lista lub nieprawidłowe dane"),
        @ApiResponse(responseCode = "404", description = "Deck nie istnieje"),
        @ApiResponse(responseCode = "500", description = "Błąd serwera")
    })
    @PostMapping("/deck/{deckId}/create-batch")
    public ResponseEntity<List<SendWordFromKafkaDto>> createVocabulariesForDeck(
            @Parameter(description = "ID decka", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "Lista danych nowych słówek", required = true)
            @Valid @RequestBody List<CreateWordDto> createWordDtos) {
        log.debug("Tworzenie słówek batch dla decka - deckId: '{}', liczba: {}", deckId, createWordDtos.size());
        List<SendWordFromKafkaDto> saveVocabularies = vocabularyService.createVocabulariesForDeck(createWordDtos, deckId);
        log.info("Słówka batch utworzone dla decka - liczba: {}, deckId: '{}'", saveVocabularies.size(), deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabularies);
    }
}
