package com.learnwords.vocabularycommandservice.controller;

import com.learnwords.vocabularycommandservice.dto.CreateWordDto;
import com.learnwords.vocabularycommandservice.dto.SendWordDto;
import com.learnwords.vocabularycommandservice.service.VocabularyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vocabulary")
public class VocabularyController {
    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService){
        this.vocabularyService = vocabularyService;
    }

    @PostMapping("/create")
    public ResponseEntity<SendWordDto> createVocabulary(
            @Valid @RequestBody CreateWordDto createWordDto) {
        SendWordDto saveVocabulary = vocabularyService.createVocabulary(createWordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabulary);
    }

    @PostMapping("/deck/{deckId}/create")
    public ResponseEntity<SendWordDto> createVocabularyForDeck(
            @PathVariable String deckId,
            @Valid @RequestBody CreateWordDto createWordDto) {
        SendWordDto saveVocabulary = vocabularyService.createVocabularyForDeck(createWordDto, deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabulary);
    }

    @PostMapping("/create-batch")
    public ResponseEntity<List<SendWordDto>> createVocabularies(
            @Valid @RequestBody List<CreateWordDto> createWordDtos) {
        List<SendWordDto> saveVocabularies = vocabularyService.createVocabularies(createWordDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabularies);
    }

    @PostMapping("/deck/{deckId}/create-batch")
    public ResponseEntity<List<SendWordDto>> createVocabulariesForDeck(
            @PathVariable String deckId,
            @Valid @RequestBody List<CreateWordDto> createWordDtos) {
        List<SendWordDto> saveVocabularies = vocabularyService.createVocabulariesForDeck(createWordDtos, deckId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabularies);
    }
}
