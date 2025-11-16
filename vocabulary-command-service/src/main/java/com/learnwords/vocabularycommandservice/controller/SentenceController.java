package com.learnwords.vocabularycommandservice.controller;

import com.learnwords.common.dto.SendSentenceFromKafkaDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler REST API dla operacji tworzenia przykładowych zdań (Command Side - CQRS).
 * 
 * <p>Udostępnia endpointy do tworzenia nowych przykładowych zdań wraz z tłumaczeniami.
 * Zdania są używane jako materiał kontekstowy do nauki słownictwa.
 * 
 * <h2>Główne funkcjonalności:</h2>
 * <ul>
 *   <li><b>Tworzenie pojedynczych zdań:</b>
 *     <ul>
 *       <li>Tworzenie zdania przypisanego do decka</li>
 *       <li>Automatyczna walidacja wymaganych pól</li>
 *       <li>Zapisywanie do Outbox Pattern</li>
 *     </ul>
 *   </li>
 *   <li><b>Operacje batch:</b>
 *     <ul>
 *       <li>Tworzenie wielu zdań jednocześnie</li>
 *       <li>Mechanizm fail-safe - błędy pojedynczych zdań nie blokują pozostałych</li>
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
 *   <li>POST /api/v1/sentences/create/{deckId} - Utwórz zdanie dla decka</li>
 *   <li>POST /api/v1/sentences/create-batch/{deckId} - Utwórz wiele zdań dla decka (batch)</li>
 * </ul>
 * 
 * <h2>Autoryzacja:</h2>
 * <p>Wszystkie endpointy wymagają autoryzacji użytkownika.
 * 
 * <h2>Obsługa błędów:</h2>
 * <p>Kontroler korzysta z globalnej obsługi wyjątków. Możliwe kody odpowiedzi:
 * <ul>
 *   <li><b>201 Created:</b> Zdanie/zdania utworzone pomyślnie</li>
 *   <li><b>400 Bad Request:</b> Błędne dane wejściowe lub brak wymaganych parametrów</li>
 *   <li><b>404 Not Found:</b> Deck nie istnieje</li>
 *   <li><b>500 Internal Server Error:</b> Nieoczekiwany błąd serwera</li>
 * </ul>
 * 
 * <h2>Walidacja:</h2>
 * <p>Wszystkie DTO używane w requestach są walidowane za pomocą Jakarta Bean Validation.
 * Błędy walidacji są zwracane jako szczegółowa mapa pól z komunikatami błędów.
 * 
 * <h2>Logowanie:</h2>
 * <p>Kontroler loguje:
 * <ul>
 *   <li><b>DEBUG:</b> Parametry wejściowe wszystkich operacji</li>
 *   <li><b>INFO:</b> Udane operacje z kluczowymi informacjami</li>
 *   <li><b>ERROR:</b> Błędy (obsługiwane przez GlobalExceptionHandler)</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see SentenceService
 * @see CreateSentenceDto
 * @see SendSentenceFromKafkaDto
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sentences")
@Tag(name = "Sentence Management", description = "API do zarządzania przykładowymi zdaniami")
public class SentenceController {

    private final SentenceService sentenceService;

    public SentenceController(SentenceService sentenceService){
        this.sentenceService=sentenceService;
    }

    /**
     * Tworzy nowe przykładowe zdanie dla decka.
     * 
     * @param deckId ID decka
     * @param sentenceDto dane nowego zdania
     * @return dane utworzonego zdania
     */
    @Operation(
        summary = "Utwórz zdanie dla decka", 
        description = "Tworzy nowe przykładowe zdanie wraz z tłumaczeniem i przypisuje do decka"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Zdanie utworzone pomyślnie",
            content = @Content(schema = @Schema(implementation = SendSentenceFromKafkaDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane lub brak deckId"),
        @ApiResponse(responseCode = "404", description = "Deck nie istnieje"),
        @ApiResponse(responseCode = "500", description = "Błąd serwera")
    })
    @PostMapping("/create/{deckId}")
    public ResponseEntity<SendSentenceFromKafkaDto> createSentence(
            @Parameter(description = "ID decka", required = true, example = "deck-123") @PathVariable String deckId,
            @Parameter(description = "Dane nowego zdania", required = true)
            @Valid @RequestBody CreateSentenceDto sentenceDto) {
        log.debug("Tworzenie zdania - deckId: '{}', zdanie: '{}'", deckId, sentenceDto.getSentence());
        SendSentenceFromKafkaDto savedSentence = sentenceService.createSentence(sentenceDto, deckId);
        log.info("Zdanie utworzone - sentenceId: '{}', deckId: '{}'", savedSentence.id(), deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSentence);
    }

    /**
     * Tworzy wiele zdań jednocześnie dla decka (batch).
     * 
     * @param deckId ID decka
     * @param sentenceDtos lista danych nowych zdań
     * @return lista utworzonych zdań
     */
    @Operation(
        summary = "Utwórz wiele zdań dla decka (batch)", 
        description = "Tworzy wiele przykładowych zdań jednocześnie i przypisuje wszystkie do decka. Operacja kontynuuje mimo błędów pojedynczych elementów."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Zdania utworzone pomyślnie",
            content = @Content(schema = @Schema(implementation = SendSentenceFromKafkaDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Brak deckId, pusta lista lub nieprawidłowe dane"),
        @ApiResponse(responseCode = "404", description = "Deck nie istnieje"),
        @ApiResponse(responseCode = "500", description = "Błąd serwera")
    })
    @PostMapping("/create-batch/{deckId}")
    public ResponseEntity<List<SendSentenceFromKafkaDto>> createSentences(
            @Parameter(description = "ID decka", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "Lista danych nowych zdań", required = true)
            @Valid @RequestBody List<CreateSentenceDto> sentenceDtos) {
        log.debug("Tworzenie zdań batch - deckId: '{}', liczba: {}", deckId, sentenceDtos.size());
        List<SendSentenceFromKafkaDto> savedSentences = sentenceService.createSentences(sentenceDtos, deckId);
        log.info("Zdania batch utworzone - liczba: {}, deckId: '{}'", savedSentences.size(), deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSentences);
    }
}
