package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.CreateDeckDto;
import com.learnwords.deckservice.dto.DeckDetailsDto;
import com.learnwords.deckservice.dto.DeckDto;
import com.learnwords.deckservice.dto.ResponseDeckDto;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.service.DeckService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/decks")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    /**
     * Tworzy nową talię
     * POST /api/v1/decks/create
     */
    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDeckDto> createDeck(
            @RequestHeader Map<String, String> headers, 
            @Valid @RequestBody CreateDeckDto createDeckDto) {
        log.info("Creating deck: {}", createDeckDto.getDeckName());
        String userId = headers.get("x-client-id");
        deckService.createDeck(userId, createDeckDto);
        log.info("Deck created successfully: {}", createDeckDto.getDeckName());
        ResponseDeckDto responseDeckDto = new ResponseDeckDto(
            createDeckDto.getDeckName(), 
            "Deck created successfully"
        );
        return ResponseEntity.ok(responseDeckDto);
    }

    /**
     * Pobiera szczegółowe informacje o talii
     * GET /api/v1/decks/{deckId}/details
     */
    @GetMapping("/{deckId}/details")
    public ResponseEntity<DeckDetailsDto> getDeckDetails(@PathVariable String deckId) {
        log.info("Pobieranie szczegółów talii: {}", deckId);
        DeckDetailsDto details = deckService.getDeckDetailsById(deckId);
        if (details == null) {
            log.warn("Talia nie znaleziona: {}", deckId);
            return ResponseEntity.notFound().build();
        }      
        return ResponseEntity.ok(details);
    }

    /**
     * Edytuje szczegóły talii (pełna aktualizacja)
     * PUT /api/v1/decks/{deckId}/details
     */
    @PutMapping("/{deckId}/details")
    public ResponseEntity<DeckDetailsDto> updateDeckDetails(
            @PathVariable String deckId,
            @Valid @RequestBody DeckDetailsDto deckDetailsDto) {
        log.info("Aktualizacja szczegółów talii: {}", deckId);
        DeckDetailsDto updatedDetails = deckService.editDeckDetails(deckId, deckDetailsDto);
        if (updatedDetails == null) {
            log.warn("Talia nie znaleziona: {}", deckId);
            return ResponseEntity.notFound().build();
        }
        log.info("Szczegóły talii zaktualizowane pomyślnie: {}", deckId);
        return ResponseEntity.ok(updatedDetails);
    }

    /**
     * Filtruje talie użytkownika
     * GET /api/v1/decks/user/{userId}/filter?isPublic=true&owner=TEACHER
     */
    @GetMapping("/user/{userId}/filter")
    public ResponseEntity<List<DeckDto>> filterUserDecks(
            @PathVariable String userId,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) DeckOwner owner) {
        log.info("Filtrowanie talii użytkownika: userId={}, isPublic={}, owner={}",
                userId, isPublic, owner);
        List<DeckDto> decks = deckService.getDecksByFilter(userId, isPublic, owner);
        return ResponseEntity.ok(decks);
    }

    /**
     * Pobiera wszystkie talie użytkownika (bez filtrów)
     * GET /api/v1/decks/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeckDto>> getUserDecks(@PathVariable String userId) {
        log.info("Pobieranie wszystkich talii użytkownika: {}", userId);
        List<DeckDto> decks = deckService.getDecksByFilter(userId);
        return ResponseEntity.ok(decks);
    }

    /**
     * Pobiera talie użytkownika z konkretnym owner
     * GET /api/v1/decks/user/{userId}/owner/{owner}
     */
    @GetMapping("/user/{userId}/owner/{owner}")
    public ResponseEntity<List<DeckDto>> getUserDecksByOwner(
            @PathVariable String userId,
            @PathVariable DeckOwner owner) {
        log.info("Pobieranie talii użytkownika {} z owner: {}", userId, owner);
        List<DeckDto> decks = deckService.getDecksByFilter(userId, owner);
        return ResponseEntity.ok(decks);
    }

    /**
     * Pobiera wszystkie publiczne talie (wszystkich użytkowników)
     * GET /api/v1/decks/public
     */
    @GetMapping("/public")
    public ResponseEntity<List<DeckDto>> getPublicDecks() {
        log.info("Pobieranie wszystkich publicznych talii");
        
        List<DeckDto> decks = deckService.getDecksByFilter(true);
        return ResponseEntity.ok(decks);
    }

    /**
     * Debug endpoint - wyświetla nagłówki
     */
    @GetMapping("/debug/headers")
    public Map<String, String> debugHeaders(@RequestHeader Map<String, String> headers) {
        headers.forEach((k,v) -> log.info("Header {} = {}", k, v));
        return headers;
    }
}
