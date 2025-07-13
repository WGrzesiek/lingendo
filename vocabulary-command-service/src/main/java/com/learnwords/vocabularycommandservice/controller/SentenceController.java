package com.learnwords.vocabularycommandservice.controller;

import com.learnwords.common.dto.SentenceDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.service.SentenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sentences")
public class SentenceController {

    private final SentenceService sentenceService;

    public SentenceController(SentenceService sentenceService){
        this.sentenceService=sentenceService;
    }

    @PostMapping("/create/{deckId}")
    public ResponseEntity<SentenceDto> createSentence(@PathVariable String deckId, @Valid @RequestBody CreateSentenceDto sentenceDto, @AuthenticationPrincipal Jwt jwt) {

        SentenceDto savedSentence = sentenceService.createSentence(sentenceDto, deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSentence);
    }
}
