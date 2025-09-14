package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.dto.ResponseSentenceDto;

import com.learnwords.vocabularyreadservice.exception.exceptions.SentenceNotFoundException;
import com.learnwords.vocabularyreadservice.repository.SentenceRepository;
import com.learnwords.vocabularyreadservice.service.SentenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
}
