package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.ResponseSentenceDto;
import com.learnwords.common.dto.ResponseVocabularyDto;
import com.learnwords.vocabularyreadservice.exception.exceptions.SentenceNotFoundException;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
import com.learnwords.vocabularyreadservice.service.VocabularyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class VocabularyServiceImpl implements VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    public VocabularyServiceImpl(VocabularyRepository vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    public Mono<ResponseVocabularyDto> getVocabularyById(String id) {
        if (id == null || id.isBlank())
            return Mono.error(new IllegalArgumentException("id must not be blank"));
        log.info("Pobieranie słowa o id: {}", id);
        return vocabularyRepository.findById(id)
                .map(v -> new ResponseVocabularyDto(v.getId(), v.getWord(), v.getTranslations(), v.getSentenceIds()))
                .switchIfEmpty(Mono.error(new SentenceNotFoundException(id)))
                .timeout(Duration.ofSeconds(5), Mono.error(new TimeoutException()))
                .doOnError(error -> log.error("Blad poczas pobierania slowka o id {}: {}", id, error.getMessage()));
    }

    public Mono<List<ResponseVocabularyDto>> getVocabulariesByIds(List<String> ids) {
        if(ids == null || ids.isEmpty())
            return Mono.error(new IllegalArgumentException("id must not be blank"));
        log.info("Pobieranie słów o id: {}", ids);
        return vocabularyRepository.findAllById(ids)
                .map(vocabulary -> new ResponseVocabularyDto(vocabulary.getId(),vocabulary.getWord(), vocabulary.getTranslations(), vocabulary.getSentenceIds()))
                .collectList()
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.error(new SentenceNotFoundException("Brak zdań o podanych id"));
                    } else {
                        return Mono.just(list);
                    }
                })
                .doOnError(error -> log.error("Błąd podczas pobierania zdań o id {}: {}", ids, error.getMessage()))
                .timeout(Duration.ofSeconds(5), Mono.error(new TimeoutException("Przekroczono czas oczekiwania")));
    }

    public Mono<List<OnlyWordDto>> getOnlyWordsByIds(List<String> ids) {
        if (ids == null || ids.isEmpty())
            return Mono.error(new IllegalArgumentException("id must not be blank"));
        log.info("Pobieranie słów o id: {}", ids);
        return vocabularyRepository.findAllById(ids)
                .map(v ->new OnlyWordDto(v.getId(), v.getWord()))
                .collectList()
                .timeout(Duration.ofSeconds(5), Mono.error(new TimeoutException()))
                .doOnError(error -> log.error("Blad podczas pobierania slowek o id: {}, {}",ids,error.getMessage()));
    }
}

