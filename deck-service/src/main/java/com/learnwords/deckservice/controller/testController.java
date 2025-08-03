package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.feignClient.VocabularyClient;
import com.learnwords.common.dto.ResponseVocabularyDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    private final VocabularyClient vocabularyClient;

    public testController(VocabularyClient vocabularyClient) {
        this.vocabularyClient = vocabularyClient;
    }

    @GetMapping("/test")
    public ResponseVocabularyDto test(){
        String id = "27a45f88-da63-46a7-b854-4d4968286b54";
        return vocabularyClient.getVocabulary(id);

    }
}
