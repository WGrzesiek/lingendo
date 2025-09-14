//package com.learnwords.vocabularyreadservice.controller;
//
//import com.learnwords.common.dto.ResponseSentenceDto;
//import com.learnwords.vocabularyreadservice.service.SentenceService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@Slf4j
//@Controller
//public class SentenceController {
//
//    private final SentenceService sentenceService;
//
//    public SentenceController(SentenceService sentenceService) {
//        this.sentenceService = sentenceService;
//    }
//
//    @GetMapping("/sentence/{id}")
//    public ResponseEntity<ResponseSentenceDto> getSentence(@PathVariable String id) {
//        log.info("Pobieranie zdania o id: {}", id);
//        ResponseSentenceDto responseSentenceDto = sentenceService.getSentence(id);
//        return ResponseEntity.ok(responseSentenceDto);
//    }
//}
