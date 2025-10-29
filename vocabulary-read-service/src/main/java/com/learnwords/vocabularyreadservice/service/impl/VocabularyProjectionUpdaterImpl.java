package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.EventStatus;
import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.UpdateOutboxEventDto;
import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.vocabularyreadservice.entity.Vocabulary;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
import com.learnwords.vocabularyreadservice.service.UpdateOutboxEvent;
import com.learnwords.vocabularyreadservice.service.VocabularyProjectionUpdater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VocabularyProjectionUpdaterImpl implements VocabularyProjectionUpdater {
    private final VocabularyRepository vocabularyRepository;
    private final UpdateOutboxEvent updateOutboxEvent;

    public VocabularyProjectionUpdaterImpl(VocabularyRepository vocabularyRepository, UpdateOutboxEvent updateOutboxEvent) {
        this.updateOutboxEvent = updateOutboxEvent;
        this.vocabularyRepository = vocabularyRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_VOCABULARY_TOPIC, groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP,    properties = {
            "spring.json.value.default.type=com.learnwords.common.dto.VocabularyDto"
    })
    public void processSentenceCreate(VocabularyDto vocabularyDto) {
        updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(vocabularyDto.id(), EventStatus.RECEIVED));
        try {
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(vocabularyDto.id(), EventStatus.PROCESSING));
            log.info("Otrzymano event: {}", EventType.CREATE_VOCABULARY);
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setId(vocabularyDto.id());
            vocabulary.setWord(vocabularyDto.word());
            vocabulary.setTranslations(vocabularyDto.translations());
            vocabulary.setSentenceIds(vocabularyDto.sentenceIds());
            vocabularyRepository.save(vocabulary);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(vocabulary.getId(), EventStatus.COMPLETED));
        }
        catch (DataAccessException e){
            log.error("Błąd podczas zapisywania słowa: {}", e.getMessage(), e);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(vocabularyDto.id(), EventStatus.RETRYING));
            throw e;
        }
        catch (Exception e){
            log.error("Błąd podczas przetwarzania słowa: {}", e.getMessage(), e);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(vocabularyDto.id(), EventStatus.FAILED));
            throw e;
        }
    }
}
