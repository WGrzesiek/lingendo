package com.learnwords.vocabularycommandservice.controller;

import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.vocabularycommandservice.dto.CreateVocabularyDto;
import com.learnwords.vocabularycommandservice.service.VocabularyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VocabularyController {
    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService){
        this.vocabularyService = vocabularyService;
    }

    @PostMapping("/voc")
    public ResponseEntity<VocabularyDto> createVocabulary(@Valid @RequestBody CreateVocabularyDto createVocabularyDto, @AuthenticationPrincipal Jwt jwt){
        String userId = jwt.getClaimAsString("user_id");
        if (userId == null) userId = jwt.getSubject();
        VocabularyDto saveVocabulary = vocabularyService.createVocabulary(createVocabularyDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveVocabulary);

    }
}
