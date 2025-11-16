package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.EventStatus;
import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.UpdateOutboxEventDto;
import com.learnwords.vocabularyreadservice.entity.Sentence;
import com.learnwords.vocabularyreadservice.repository.SentenceRepository;
import com.learnwords.vocabularyreadservice.service.SentenceProjectionUpdater;
import com.learnwords.vocabularyreadservice.service.UpdateOutboxEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacja serwisu aktualizującego projekcję odczytu zdań.
 * 
 * <p>Nasłuchuje eventów Kafka związanych z tworzeniem zdań i aktualizuje
 * projekcję odczytu w bazie danych Read Service.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Nasłuchiwanie eventów CREATE_SENTENCE z Kafka</li>
 *   <li>Aktualizacja statusu outbox event (RECEIVED → PROCESSING → COMPLETED)</li>
 *   <li>Tworzenie nowych wpisów zdań w projekcji odczytu</li>
 *   <li>Obsługa błędów z oznaczeniem statusu FAILED</li>
 * </ul>
 */
@Slf4j
@Service
public class SentenceProjectionUpdaterImpl implements SentenceProjectionUpdater {
    
    private final SentenceRepository sentenceRepository;
    private final UpdateOutboxEvent updateOutboxEvent;

    public SentenceProjectionUpdaterImpl(SentenceRepository sentenceRepository, UpdateOutboxEvent updateOutboxEvent) {
        this.updateOutboxEvent = updateOutboxEvent;
        this.sentenceRepository = sentenceRepository;
    }

    /**
     * Przetwarza event tworzenia nowego zdania z Kafka.
     * 
     * <p>Metoda:
     * <ul>
     *   <li>Odbiera DTO zdania z Kafka</li>
     *   <li>Aktualizuje status outbox event na RECEIVED</li>
     *   <li>Waliduje dane wejściowe</li>
     *   <li>Tworzy nową encję Sentence w projekcji odczytu</li>
     *   <li>Oznacza event jako COMPLETED lub FAILED w przypadku błędu</li>
     * </ul>
     * 
     * @param sentenceDto DTO zdania z Kafka
     */
    @Transactional
    @KafkaListener(
            topics = KafkaTopic.CREATE_SENTENCE_TOPIC,
            groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP,
            properties = {
                    "spring.json.value.default.type=com.learnwords.common.dto.SentenceDto"
            }
    )
    public void processSentenceCreate(SentenceDto sentenceDto) {
        if (sentenceDto == null) {
            log.error("Otrzymano null jako SentenceDto");
            return;
        }

        String sentenceId = sentenceDto.id();
        updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sentenceId, EventStatus.RECEIVED));
        log.info("Otrzymano event: {} dla zdania: {}", EventType.CREATE_SENTENCE, sentenceId);

        try {
            validateSentenceDto(sentenceDto);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sentenceId, EventStatus.PROCESSING));

            Sentence sentence = new Sentence();
            sentence.setId(sentenceId);
            sentence.setSentence(sentenceDto.sentence());
            sentence.setTranslation(sentenceDto.translation());
            
            sentenceRepository.save(sentence);
            log.info("Zapisano zdanie o id: {}", sentenceId);
            
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sentenceId, EventStatus.COMPLETED));
        } catch (IllegalArgumentException e) {
            log.error("Walidacja nie powiodła się dla zdania {}: {}", sentenceId, e.getMessage());
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sentenceId, EventStatus.FAILED));
        } catch (Exception e) {
            log.error("Błąd podczas przetwarzania zdania {}: {}", sentenceId, e.getMessage(), e);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sentenceId, EventStatus.FAILED));
        }
    }

    /**
     * Waliduje dane zdania.
     */
    private void validateSentenceDto(SentenceDto sentenceDto) {
        if (sentenceDto.id() == null || sentenceDto.id().isBlank()) {
            throw new IllegalArgumentException("ID zdania nie może być puste");
        }
        if (sentenceDto.sentence() == null || sentenceDto.sentence().isBlank()) {
            throw new IllegalArgumentException("Zdanie nie może być puste");
        }
        if (sentenceDto.translation() == null || sentenceDto.translation().isBlank()) {
            throw new IllegalArgumentException("Tłumaczenie zdania nie może być puste");
        }
    }
}
