package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.dto.ResponseSentenceDto;

import com.learnwords.vocabularyreadservice.entity.Sentence;
import com.learnwords.vocabularyreadservice.enums.FetchStrategy;
import com.learnwords.vocabularyreadservice.exception.exceptions.SentenceNotFoundException;
import com.learnwords.vocabularyreadservice.repository.SentenceRepository;
import com.learnwords.vocabularyreadservice.service.SentenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;


@Slf4j
@Service
public class SentenceServiceImpl implements SentenceService {

    private final SentenceRepository sentenceRepository;

    public SentenceServiceImpl(SentenceRepository sentenceRepository) {
        this.sentenceRepository = sentenceRepository;
    }

    @Override
    public Optional<ResponseSentenceDto> getSentenceById(String id){
        if (id == null || id.isBlank())
            throw new SentenceNotFoundException(id);
        log.info("Pobieranie zdania o id: {}", id);
        return sentenceRepository.findById(id).map(sentence -> new ResponseSentenceDto(sentence.getId(), sentence.getSentence(), sentence.getTranslation()));
    }

    @Override
    public List<ResponseSentenceDto> getSentencesByIds(List<String> ids){
        if (ids == null || ids.isEmpty())
            throw new IllegalArgumentException("ids must not be blank");
        log.info("Pobieranie zdań o id: {}", ids);
        return sentenceRepository.findAllById(ids).stream().map(sentence -> new ResponseSentenceDto(sentence.getId(), sentence.getSentence(), sentence.getTranslation())).toList();
    }

    @Override
    public List<ResponseSentenceDto> getSentences(int page_size, FetchStrategy fetchStrategy){
        if (page_size <= 0) {
            throw new IllegalArgumentException("page_size must be greater than 0");
        }
        log.info("Pobieranie {} zdań, strategia: {}", page_size, fetchStrategy);
        List<Sentence> result = null;
        switch (fetchStrategy) {
            case RANDOM -> {
                result = sentenceRepository.findRandom(page_size);
            }
            case Alphabetically -> {
                result = sentenceRepository.findRandomSortedAlphabetically(page_size);
            }
        }
        return result.stream()
                .map(sentence -> new ResponseSentenceDto(sentence.getId(), sentence.getSentence(), sentence.getTranslation()))
                .toList();
    }
}
