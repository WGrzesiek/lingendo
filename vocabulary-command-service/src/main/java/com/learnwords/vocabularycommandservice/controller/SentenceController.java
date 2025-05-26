package com.learnwords.vocabularycommandservice.controller;

import com.learnwords.common.dto.SentenceDto;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.service.SentenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentenceController {

    private final SentenceService sentenceService;

    public SentenceController(SentenceService sentenceService){
        this.sentenceService=sentenceService;
    }

    @PostMapping("/add")
    public ResponseEntity<SentenceDto> createSentence(@Valid @RequestBody CreateSentenceDto sentenceDto){
        SentenceDto savedSentence = sentenceService.createSentence(sentenceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSentence);
    }

}
