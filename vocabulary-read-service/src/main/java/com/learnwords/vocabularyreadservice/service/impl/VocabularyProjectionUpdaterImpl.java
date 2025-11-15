package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.EventStatus;
import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SendWordFromKafkaDto;
import com.learnwords.common.dto.UpdateOutboxEventDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.vocabularyreadservice.dto.GetWordFromKafkaDto;
import com.learnwords.vocabularyreadservice.entity.Vocabulary;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
import com.learnwords.vocabularyreadservice.service.UpdateOutboxEvent;
import com.learnwords.vocabularyreadservice.service.VocabularyProjectionUpdater;
import lombok.extern.slf4j.Slf4j;
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
    @KafkaListener(topics = {
            KafkaTopic.CREATE_VOCABULARY_TOPIC,
            KafkaTopic.CREATE_VOCABULARY_FOR_DECK_TOPIC
    }, groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP,    properties = {
            "spring.json.value.default.type=com.learnwords.common.dto.SendWordFromKafkaDto"
    })
    public void processVocabularyCreate(SendWordFromKafkaDto sendWordFromKafkaDto) {
        updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sendWordFromKafkaDto.id(), EventStatus.RECEIVED));
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(sendWordFromKafkaDto.id(), EventStatus.PROCESSING));
            log.info("Otrzymano event: {}", EventType.CREATE_VOCABULARY);
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setId(sendWordFromKafkaDto.id());
            vocabulary.setWord(sendWordFromKafkaDto.word());
            vocabulary.setTranslations(sendWordFromKafkaDto.translations());
            vocabulary.setSentenceIds(sendWordFromKafkaDto.sentenceIds());
            vocabularyRepository.save(vocabulary);
            updateOutboxEvent.processUpdateOutboxEvent(new UpdateOutboxEventDto(vocabulary.getId(), EventStatus.COMPLETED));
    }
}
