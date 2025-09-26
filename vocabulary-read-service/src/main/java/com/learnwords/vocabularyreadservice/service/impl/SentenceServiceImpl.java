package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.dto.ResponseSentenceDto;

import com.learnwords.vocabularyreadservice.entity.Sentence;
import com.learnwords.vocabularyreadservice.enums.FetchStrategy;
import com.learnwords.vocabularyreadservice.exception.exceptions.SentenceNotFoundException;
import com.learnwords.vocabularyreadservice.repository.SentenceRepository;
import com.learnwords.vocabularyreadservice.service.SentenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;


@Slf4j
@Service
public class SentenceServiceImpl implements SentenceService {

    private final SentenceRepository sentenceRepository;

    public SentenceServiceImpl(SentenceRepository sentenceRepository) {
        this.sentenceRepository = sentenceRepository;
    }

    @Override
    public Mono<ResponseSentenceDto> getSentenceById(String id){
        if (id == null || id.isBlank())
            return Mono.error(new IllegalArgumentException("id must not be blank"));
        log.info("Pobieranie zdania o id: {}", id);
        return sentenceRepository.findById(id)
                .map(sentence -> new ResponseSentenceDto(sentence.getId(),sentence.getSentence(), sentence.getTranslation()))
                .switchIfEmpty(Mono.error(new SentenceNotFoundException(id)))
                .doOnError(error -> log.error("Błąd podczas pobierania zdania o id {}: {}", id, error.getMessage()))
                .timeout(Duration.ofSeconds(5), Mono.error(new TimeoutException("Przekroczono czas oczekiwania")));
    }

    @Override
    public Mono<List<ResponseSentenceDto>> getSentencesByIds(List<String> ids){
        if (ids == null || ids.isEmpty())
            return Mono.error(new IllegalArgumentException("ids must not be empty"));
        log.info("Pobieranie zdań o id: {}", ids);
        return sentenceRepository.findAllById(ids)
                .map(sentence -> new ResponseSentenceDto(sentence.getId(),sentence.getSentence(), sentence.getTranslation()))
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

    @Override
    public Mono<List<ResponseSentenceDto>> getSentences(int page_size, FetchStrategy fetchStrategy){
        if (page_size <= 0) {
            return Mono.error(new IllegalArgumentException("page_size must be greater than 0"));
        }
        log.info("Pobieranie {} zdań, strategia: {}", page_size, fetchStrategy);
        Mono<List<Sentence>> result = null;
        switch (fetchStrategy) {
            case RANDOM -> {
                result = sentenceRepository.findRandom(page_size);
            }
            case Alphabetically -> {
                result = sentenceRepository.findRandomSortedAlphabetically(page_size);
            }
        }
        return result
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.error(new SentenceNotFoundException("Brak zdań w bazie danych"));
                    } else {
                        return Mono.just(list);
                    }
                })
                .map(sentences -> sentences.stream()
                        .map(sentence -> new ResponseSentenceDto(sentence.getId(), sentence.getSentence(), sentence.getTranslation()))
                        .toList())
                .doOnError(error -> log.error("Błąd podczas pobierania zdań: {}", error.getMessage()))
                .timeout(Duration.ofSeconds(5), Mono.error(new TimeoutException("Przekroczono czas oczekiwania")));

    }
}
