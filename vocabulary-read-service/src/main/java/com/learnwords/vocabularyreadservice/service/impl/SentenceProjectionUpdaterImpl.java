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
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SentenceProjectionUpdaterImpl implements SentenceProjectionUpdater {
    private final SentenceRepository sentenceRepository;
    private final UpdateOutboxEvent updateOutboxEvent;

    public SentenceProjectionUpdaterImpl(SentenceRepository sentenceRepository, UpdateOutboxEvent updateOutboxEvent) {
        this.updateOutboxEvent = updateOutboxEvent;
        this.sentenceRepository = sentenceRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_SENTENCE_TOPIC, groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP)
    public void processSentenceCreate(SentenceDto sentenceDto) {
        updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sentenceDto.id(), EventStatus.RECEIVED));
        try{
            log.info("Otrzymano event: {}", EventType.CREATE_SENTENCE);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sentenceDto.id(), EventStatus.PROCESSING));
            Sentence sentence = new Sentence();
            sentence.setId(sentenceDto.id());
            sentence.setSentence(sentenceDto.sentence());
            sentence.setTranslation(sentenceDto.translation());
            sentenceRepository.save(sentence);
            log.info("Zapisano zdanie o id: {}", sentence.getId());
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sentence.getId(), EventStatus.COMPLETED));
        }
        catch (DataAccessException e){
            log.error("Błąd podczas zapisywania zdania: {}", e.getMessage(), e);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sentenceDto.id(), EventStatus.RETRYING));
            throw e;
        }
        catch (Exception e){
            log.error("Błąd podczas przetwarzania zdania: {}", e.getMessage(), e);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sentenceDto.id(), EventStatus.FAILED));
            throw e;
        }
    }
}
