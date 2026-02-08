package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.EventStatus;
import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SendWordFromKafkaDto;
import com.learnwords.common.dto.UpdateOutboxEventDto;
import com.learnwords.vocabularyreadservice.entity.Vocabulary;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
import com.learnwords.vocabularyreadservice.service.UpdateOutboxEvent;
import com.learnwords.vocabularyreadservice.service.VocabularyProjectionUpdater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacja serwisu aktualizującego projekcję odczytu słownictwa.
 * 
 * <p>Nasłuchuje eventów Kafka związanych z tworzeniem słówek i aktualizuje
 * projekcję odczytu w bazie danych Read Service.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Nasłuchiwanie eventów CREATE_VOCABULARY i CREATE_VOCABULARY_FOR_DECK z Kafka</li>
 *   <li>Aktualizacja statusu outbox event (RECEIVED → PROCESSING → COMPLETED)</li>
 *   <li>Tworzenie nowych wpisów słownictwa w projekcji odczytu</li>
 *   <li>Obsługa błędów z oznaczeniem statusu FAILED</li>
 * </ul>
 */
@Slf4j
@Service
public class VocabularyProjectionUpdaterImpl implements VocabularyProjectionUpdater {
    
    private final VocabularyRepository vocabularyRepository;
    private final UpdateOutboxEvent updateOutboxEvent;

    public VocabularyProjectionUpdaterImpl(VocabularyRepository vocabularyRepository, UpdateOutboxEvent updateOutboxEvent) {
        this.updateOutboxEvent = updateOutboxEvent;
        this.vocabularyRepository = vocabularyRepository;
    }

    /**
     * Przetwarza event tworzenia nowego słownictwa z Kafka.
     * 
     * <p>Metoda:
     * <ul>
     *   <li>Odbiera DTO słowa z Kafka</li>
     *   <li>Aktualizuje status outbox event na RECEIVED</li>
     *   <li>Waliduje dane wejściowe</li>
     *   <li>Tworzy nową encję Vocabulary w projekcji odczytu</li>
     *   <li>Oznacza event jako COMPLETED lub FAILED w przypadku błędu</li>
     * </ul>
     * 
     * <p>Obsługuje dwa topiki:
     * <ul>
     *   <li>CREATE_VOCABULARY_TOPIC - tworzenie słówka standalone</li>
     *   <li>CREATE_VOCABULARY_FOR_DECK_TOPIC - tworzenie słówka dla decka</li>
     * </ul>
     * 
     * @param sendWordFromKafkaDto DTO słowa z Kafka
     */
    @Transactional
    @KafkaListener(
            topics = {
                    KafkaTopic.CREATE_VOCABULARY_TOPIC,
                    KafkaTopic.CREATE_VOCABULARY_FOR_DECK_TOPIC
            },
            groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP,
            properties = {
                    "spring.json.value.default.type=com.learnwords.common.dto.SendWordFromKafkaDto"
            }
    )
    public void processVocabularyCreate(SendWordFromKafkaDto sendWordFromKafkaDto) {
        if (sendWordFromKafkaDto == null) {
            log.error("Otrzymano null jako SendWordFromKafkaDto");
            return;
        }

        String wordId = sendWordFromKafkaDto.id();
        updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(wordId, EventStatus.RECEIVED));
        log.info("Otrzymano event: {} dla słowa: {}", EventType.CREATE_VOCABULARY, wordId);

        try {
            validateWordDto(sendWordFromKafkaDto);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(wordId, EventStatus.PROCESSING));

            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setId(wordId);
            vocabulary.setWord(sendWordFromKafkaDto.word());
            vocabulary.setTranslations(sendWordFromKafkaDto.translations());
            vocabulary.setSentenceIds(sendWordFromKafkaDto.sentenceIds());
            
            vocabularyRepository.save(vocabulary);
            log.info("Zapisano słownictwo o id: {}", wordId);
            
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(wordId, EventStatus.COMPLETED));
        } catch (IllegalArgumentException e) {
            log.error("Walidacja nie powiodła się dla słowa {}: {}", wordId, e.getMessage());
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(wordId, EventStatus.FAILED));
        } catch (Exception e) {
            log.error("Błąd podczas przetwarzania słowa {}: {}", wordId, e.getMessage(), e);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(wordId, EventStatus.FAILED));
        }
    }

    /**
     * Waliduje dane słowa.
     */
    private void validateWordDto(SendWordFromKafkaDto wordDto) {
        if (wordDto.id() == null || wordDto.id().isBlank()) {
            throw new IllegalArgumentException("ID słowa nie może być puste");
        }
        if (wordDto.word() == null || wordDto.word().isBlank()) {
            throw new IllegalArgumentException("Słowo nie może być puste");
        }
        if (wordDto.translations() == null || wordDto.translations().isEmpty()) {
            throw new IllegalArgumentException("Lista tłumaczeń nie może być pusta");
        }
    }
}
