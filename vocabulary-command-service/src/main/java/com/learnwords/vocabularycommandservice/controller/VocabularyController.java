package com.learnwords.vocabularycommandservice.controller;

import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.vocabularycommandservice.dto.CreateVocabularyDto;
import com.learnwords.vocabularycommandservice.service.VocabularyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vocabulary")
public class VocabularyController {
    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService){
        this.vocabularyService = vocabularyService;
    }

    @PostMapping("/create/{deckId}")
    public ResponseEntity<VocabularyDto> createVocabulary(@PathVariable String deckId, @Valid @RequestBody CreateVocabularyDto createVocabularyDto, @AuthenticationPrincipal Jwt jwt){
        VocabularyDto saveVocabulary = vocabularyService.createVocabulary(createVocabularyDto, deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabulary);

    }
}
