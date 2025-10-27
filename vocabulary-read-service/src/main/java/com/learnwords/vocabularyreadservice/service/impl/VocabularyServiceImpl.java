package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.ResponseVocabularyDto;
import com.learnwords.vocabularyreadservice.exception.exceptions.SentenceNotFoundException;
import com.learnwords.vocabularyreadservice.exception.exceptions.VocabularyNotFoundException;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
import com.learnwords.vocabularyreadservice.service.VocabularyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VocabularyServiceImpl implements VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    public VocabularyServiceImpl(VocabularyRepository vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    public Optional<ResponseVocabularyDto> getVocabularyById(String id) {
        if (id == null || id.isBlank())
            throw new VocabularyNotFoundException(id);
        log.info("Pobieranie słowa o id: {}", id);
        return vocabularyRepository.findById(id).map(vocabulary -> new ResponseVocabularyDto(vocabulary.getId(), vocabulary.getWord(), vocabulary.getTranslations(), vocabulary.getSentenceIds()));
    }

    public List<ResponseVocabularyDto> getVocabulariesByIds(List<String> ids) {
        if(ids == null || ids.isEmpty())
            throw new IllegalArgumentException("ids must not be blank");
        log.info("Pobieranie słów o id: {}", ids);
        return vocabularyRepository.findAllById(ids).stream()
                .map(vocabulary -> new ResponseVocabularyDto(vocabulary.getId(),vocabulary.getWord(), vocabulary.getTranslations(), vocabulary.getSentenceIds()))
                .toList();
    }

    public List<OnlyWordDto> getOnlyWordsByIds(List<String> ids) {
        if (ids == null || ids.isEmpty())
            throw new IllegalArgumentException("id must not be blank");
        log.info("Pobieranie słów o id: {}", ids);
        return vocabularyRepository.findAllById(ids).stream()
                .map(v ->new OnlyWordDto(v.getId(), v.getWord())).toList();
    }
}

