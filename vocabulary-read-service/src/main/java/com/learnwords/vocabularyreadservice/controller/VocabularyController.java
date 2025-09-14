//package com.learnwords.vocabularyreadservice.controller;
//
//import com.learnwords.common.dto.OnlyWordDto;
//import com.learnwords.common.dto.ResponseVocabularyDto;
//import com.learnwords.vocabularyreadservice.service.VocabularyService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/vocabulary")
//public class VocabularyController {
//    private final VocabularyService vocabularyService;
//
//    public VocabularyController(VocabularyService vocabularyService) {
//        this.vocabularyService = vocabularyService;
//    }
//
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ResponseVocabularyDto> vocabulary(@PathVariable String id) {
//        ResponseVocabularyDto vocabulary = vocabularyService.getVocabulary(id);
//        return ResponseEntity.ok(vocabulary);
//    }
//
//    @PostMapping("/vocabularies")
//    public ResponseEntity<List<ResponseVocabularyDto>> vocabularies(@RequestBody List<String> ids) {
//        List<ResponseVocabularyDto> vocabularies =  vocabularyService.getVocabularies(ids);
//        return ResponseEntity.ok(vocabularies);
//    }
//
//    @PostMapping("/words")
//    public List<OnlyWordDto> words(@RequestBody List<String> ids) {
//        return vocabularyService.getOnlyWordsByIds(ids);
//    }
//}
