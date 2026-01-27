package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SentenceGeneratedEventDto;
import com.learnwords.vocabularyreadservice.entity.SentenceAI;
import com.learnwords.vocabularyreadservice.repository.SentenceAIRepository;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
import com.learnwords.vocabularyreadservice.service.SentenceAIProjectionUpdater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementacja serwisu aktualizującego projekcję odczytu zdań AI.
 * 
 * <p>Nasłuchuje eventów Kafka z koog-service związanych z generowaniem zdań AI
 * i zapisuje je do kolekcji SentenceAI w MongoDB.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Nasłuchiwanie eventów AI_SENTENCE_GENERATED z Kafka</li>
 *   <li>Tworzenie nowych wpisów zdań AI w projekcji odczytu</li>
 *   <li>Aktualizacja Vocabulary o ID zapisanych zdań AI</li>
 *   <li>Obsługa wielu zdań w jednym evencie</li>
 * </ul>
 */
@Slf4j
@Service
public class SentenceAIProjectionUpdaterImpl implements SentenceAIProjectionUpdater {
    
    private final SentenceAIRepository sentenceAIRepository;
    private final VocabularyRepository vocabularyRepository;

    public SentenceAIProjectionUpdaterImpl(SentenceAIRepository sentenceAIRepository, 
                                           VocabularyRepository vocabularyRepository) {
        this.sentenceAIRepository = sentenceAIRepository;
        this.vocabularyRepository = vocabularyRepository;
    }

    /**
     * Przetwarza event wygenerowanych zdań AI z koog-service.
     * 
     * @param event event z Kafka zawierający wygenerowane zdania
     */
    @Override
    @Transactional
    @KafkaListener(
            topics = KafkaTopic.AI_SENTENCE_GENERATED,
            groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP,
            properties = {
                    "spring.json.value.default.type=com.learnwords.common.dto.SentenceGeneratedEventDto",
                    "spring.json.use.type.headers=false"
            }
    )
    public void processSentenceAIGenerated(SentenceGeneratedEventDto event) {
        if (event == null) {
            log.error("Otrzymano null jako SentenceGeneratedEventDto");
            return;
        }

        log.info("Otrzymano event AI_SENTENCE_GENERATED - wordId: {}, liczba zdań: {}", event.wordId(), event.sentences().size());

        try {
            validateEvent(event);
            
            List<String> savedSentenceIds = new ArrayList<>();
            
            for (SentenceGeneratedEventDto.GeneratedSentenceDto sentence : event.sentences()) {
                String sentenceId = UUID.randomUUID().toString();
                
                SentenceAI sentenceAI = SentenceAI.builder()
                        .id(sentenceId)
                        .sentenceAI(sentence.sentence())
                        .translationAI(sentence.translation())
                        .createdAt(Instant.now())
                        .build();
                
                sentenceAIRepository.save(sentenceAI);
                savedSentenceIds.add(sentenceId);
            }
            
            vocabularyRepository.addSentenceAIIds(event.wordId(), savedSentenceIds);
            
            log.info("Zapisano {} zdań AI i zaktualizowano Vocabulary dla wordId: {}",
                    savedSentenceIds.size(), event.wordId());
            
        } catch (IllegalArgumentException e) {
            log.error("Walidacja nie powiodła się dla eventu {}: {}", event.wordId(), e.getMessage());
        } catch (Exception e) {
            log.error("Błąd podczas przetwarzania eventu {}: {}", event.wordId(), e.getMessage(), e);
        }
    }

    /**
     * Waliduje dane eventu.
     */
    private void validateEvent(SentenceGeneratedEventDto event) {
        if (event.wordId() == null || event.wordId().isBlank()) {
            throw new IllegalArgumentException("ID słowa nie może być puste");
        }
        if (event.sentences() == null || event.sentences().isEmpty()) {
            throw new IllegalArgumentException("Lista zdań nie może być pusta");
        }
        for (SentenceGeneratedEventDto.GeneratedSentenceDto sentence : event.sentences()) {
            if (sentence.sentence() == null || sentence.sentence().isBlank()) {
                throw new IllegalArgumentException("Zdanie nie może być puste");
            }
            if (sentence.translation() == null || sentence.translation().isBlank()) {
                throw new IllegalArgumentException("Tłumaczenie nie może być puste");
            }
        }
    }
}
