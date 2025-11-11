package com.learnwords.vocabularyreadservice.controller;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.ResponseVocabularyDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.vocabularyreadservice.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Kontroler REST API dla operacji odczytu słownictwa.
 * 
 * <p>Udostępnia endpointy do pobierania słów, tłumaczeń i przykładowych zdań
 * w różnych formatach i dla różnych przypadków użycia.
 * 
 * <p>Dostępne endpointy:
 * <ul>
 *   <li>GET /api/v1/vocabulary/{id} - pobiera pełne dane słowa</li>
 *   <li>GET /api/v1/vocabulary/batch - pobiera wiele słów po ID</li>
 *   <li>GET /api/v1/vocabulary/only-words - pobiera minimalne dane słów</li>
 *   <li>GET /api/v1/vocabulary/legacy/{id} - przestarzały endpoint (deprecated)</li>
 *   <li>GET /api/v1/vocabulary/legacy/batch - przestarzały endpoint (deprecated)</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 2.0
 * @since 2025-11-11
 * @see VocabularyService
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/vocabulary")
@Tag(name = "Vocabulary", description = "Operacje odczytu słownictwa")
public class VocabularyController {

    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    /**
     * Pobiera pełne dane słowa według ID.
     * 
     * @param id ID słowa do pobrania
     * @return ResponseEntity z pełnymi danymi słowa lub 404 jeśli nie znaleziono
     */
    @Operation(summary = "Pobierz słowo po ID", 
               description = "Zwraca pełne dane słowa wraz z tłumaczeniami i przykładowymi zdaniami")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Słowo znalezione",
                    content = @Content(schema = @Schema(implementation = WordDto.class))),
        @ApiResponse(responseCode = "404", description = "Słowo nie znalezione"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji")
    })
    @GetMapping("/{id}")
    public ResponseEntity<WordDto> getWordById(
            @Parameter(description = "ID słowa", required = true)
            @PathVariable String id) {
        log.info("Żądanie pobrania słowa o ID: {}", id);
        
        Optional<WordDto> word = vocabularyService.getWordById(id);
        
        return word.map(ResponseEntity::ok)
                   .orElseGet(() -> {
                       log.warn("Nie znaleziono słowa o ID: {}", id);
                       return ResponseEntity.notFound().build();
                   });
    }

    /**
     * Pobiera pełne dane wielu słów według listy ID.
     * 
     * @param ids lista ID słów do pobrania (parametr query)
     * @return ResponseEntity z listą pełnych danych słów
     */
    @Operation(summary = "Pobierz wiele słów po ID", 
               description = "Zwraca pełne dane wielu słów wraz z tłumaczeniami i przykładowymi zdaniami")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista słów pobrana pomyślnie"),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe parametry"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji")
    })
    @GetMapping("/batch")
    public ResponseEntity<List<WordDto>> getWordsByIds(
            @Parameter(description = "Lista ID słów", required = true)
            @RequestParam List<String> ids) {
        log.info("Żądanie pobrania {} słów", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            log.warn("Pusta lista ID słów");
            return ResponseEntity.badRequest().build();
        }
        
        List<WordDto> words = vocabularyService.getWordsByIds(ids);
        log.info("Zwrócono {} słów z {} żądanych", words.size(), ids.size());
        
        return ResponseEntity.ok(words);
    }

    /**
     * Pobiera minimalne dane słów (tylko ID i słowo) według listy ID.
     * 
     * @param ids lista ID słów do pobrania (parametr query)
     * @return ResponseEntity z listą minimalnych danych słów
     */
    @Operation(summary = "Pobierz minimalne dane słów", 
               description = "Zwraca tylko ID i słowo bez tłumaczeń i zdań (lepsza wydajność)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista słów pobrana pomyślnie"),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe parametry"),
        @ApiResponse(responseCode = "401", description = "Brak autoryzacji")
    })
    @GetMapping("/only-words")
    public ResponseEntity<List<OnlyWordDto>> getOnlyWordsByIds(
            @Parameter(description = "Lista ID słów", required = true)
            @RequestParam List<String> ids) {
        log.info("Żądanie pobrania minimalnych danych {} słów", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            log.warn("Pusta lista ID słów");
            return ResponseEntity.badRequest().build();
        }
        
        List<OnlyWordDto> words = vocabularyService.getOnlyWordsByIds(ids);
        log.info("Zwrócono {} słów z {} żądanych", words.size(), ids.size());
        
        return ResponseEntity.ok(words);
    }
}
