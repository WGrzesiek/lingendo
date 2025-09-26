package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.CreateDeckDto;
import com.learnwords.deckservice.dto.ResponseDeckDto;
import com.learnwords.deckservice.service.DeckService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/decks")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDeckDto> createDeck(@RequestHeader Map<String, String> headers, @Valid @RequestBody CreateDeckDto createDeckDto) {
        log.info("Creating deck: {}", createDeckDto.getDeckName());

        String userId = headers.get("x-client-id");
        deckService.createDeck(userId, createDeckDto);
        log.info("Deck created successfully: {}", createDeckDto.getDeckName());
        ResponseDeckDto responseDeckDto = new ResponseDeckDto(createDeckDto.getDeckName(), "Deck created successfully");
        return ResponseEntity.ok(responseDeckDto);
    }
    @GetMapping("/debug/headers")
    public Map<String, String> debugHeaders(@RequestHeader Map<String, String> headers) {
    headers.forEach((k,v) -> log.info("Header {} = {}", k, v));
    return headers;
}


}
