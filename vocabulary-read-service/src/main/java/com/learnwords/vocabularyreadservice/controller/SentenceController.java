package com.learnwords.vocabularyreadservice.controller;

import com.learnwords.common.dto.ResponseSentenceDto;
import com.learnwords.vocabularyreadservice.enums.FetchStrategy;
import com.learnwords.vocabularyreadservice.service.SentenceService;
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
 * Kontroler REST API dla operacji odczytu zdań przykładowych.
 * 
 * <p>Udostępnia endpointy do pobierania przykładowych zdań w różnych formatach
 * i strategiach pobierania (normalne zdania, zdania AI, lub losowe).
 * 
 * <p>Dostępne endpointy:
 * <ul>
 *   <li>GET /api/v1/sentences/{id} - pobiera pojedyncze zdanie po ID</li>
 *   <li>GET /api/v1/sentences/batch - pobiera wiele zdań po ID</li>
 *   <li>GET /api/v1/sentences - pobiera zdania ze stronicowaniem i strategią</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see SentenceService
 * @see FetchStrategy
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/read/sentences")
@Tag(name = "Sentences", description = "Operacje odczytu przykładowych zdań")
public class SentenceController {

    private final SentenceService sentenceService;

    public SentenceController(SentenceService sentenceService) {
        this.sentenceService = sentenceService;
    }

    /**
     * Pobiera pojedyncze zdanie według ID.
     * 
     * @param id ID zdania do pobrania
     * @return ResponseEntity ze zdaniem lub 404 jeśli nie znaleziono
     */
    @Operation(summary = "Pobierz zdanie po ID", 
               description = "Zwraca przykładowe zdanie wraz z tłumaczeniem")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Zdanie znalezione",
                    content = @Content(schema = @Schema(implementation = ResponseSentenceDto.class))),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe ID"),
        @ApiResponse(responseCode = "404", description = "Zdanie nie znalezione")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseSentenceDto> getSentenceById(
            @Parameter(description = "ID zdania", required = true)
            @PathVariable String id) {
        log.info("Żądanie pobrania zdania o ID: {}", id);
        
        Optional<ResponseSentenceDto> sentence = sentenceService.getSentenceById(id);
        
        return sentence.map(ResponseEntity::ok)
                      .orElseGet(() -> {
                          log.warn("Nie znaleziono zdania o ID: {}", id);
                          return ResponseEntity.notFound().build();
                      });
    }

    /**
     * Pobiera wiele zdań według listy ID.
     * 
     * @param ids lista ID zdań do pobrania (parametr query)
     * @return ResponseEntity z listą zdań
     */
    @Operation(summary = "Pobierz wiele zdań po ID", 
               description = "Zwraca listę przykładowych zdań wraz z tłumaczeniami")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista zdań pobrana pomyślnie"),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe parametry")
    })
    @GetMapping("/batch")
    public ResponseEntity<List<ResponseSentenceDto>> getSentencesByIds(
            @Parameter(description = "Lista ID zdań", required = true)
            @RequestParam List<String> ids) {
        log.info("Żądanie pobrania {} zdań", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            log.warn("Pusta lista ID zdań");
            return ResponseEntity.badRequest().build();
        }
        
        List<ResponseSentenceDto> sentences = sentenceService.getSentencesByIds(ids);
        log.info("Zwrócono {} zdań z {} żądanych", sentences.size(), ids.size());
        
        return ResponseEntity.ok(sentences);
    }

    /**
     * Pobiera zdania ze stronicowaniem i określoną strategią pobierania.
     * 
     * <p>Strategie pobierania:
     * <ul>
     *   <li>NORMAL - tylko normalne zdania</li>
     *   <li>AI - tylko zdania wygenerowane przez AI</li>
     *   <li>RANDOM - losowa mieszanka normalnych i AI</li>
     * </ul>
     * 
     * @param pageSize liczba zdań do pobrania (domyślnie 10)
     * @param fetchStrategy strategia pobierania (domyślnie NORMAL)
     * @return ResponseEntity z listą zdań
     */
    @Operation(summary = "Pobierz zdania ze stronicowaniem", 
               description = "Zwraca listę zdań według strategii pobierania (NORMAL, AI, RANDOM)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista zdań pobrana pomyślnie"),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe parametry")
    })
    @GetMapping
    public ResponseEntity<List<ResponseSentenceDto>> getSentences(
            @Parameter(description = "Liczba zdań do pobrania", example = "10")
            @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Strategia pobierania: NORMAL, AI, RANDOM", example = "NORMAL")
            @RequestParam(defaultValue = "NORMAL") FetchStrategy fetchStrategy) {
        log.info("Żądanie pobrania {} zdań ze strategią: {}", pageSize, fetchStrategy);
        
        if (pageSize <= 0 || pageSize > 100) {
            log.warn("Nieprawidłowy rozmiar strony: {}", pageSize);
            return ResponseEntity.badRequest().build();
        }
        
        List<ResponseSentenceDto> sentences = sentenceService.getSentences(pageSize, fetchStrategy);
        log.info("Zwrócono {} zdań", sentences.size());
        
        return ResponseEntity.ok(sentences);
    }
}
