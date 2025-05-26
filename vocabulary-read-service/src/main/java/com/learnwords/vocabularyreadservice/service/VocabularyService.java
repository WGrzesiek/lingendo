package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.EventStatus;
import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.UpdateStatusDto;
import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.vocabularyreadservice.entity.Vocabulary;
import com.learnwords.vocabularyreadservice.repository.SentenceRepository;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class VocabularyService {
    private final SentenceRepository sentenceRepository;
    private final VocabularyRepository vocabularyRepository;
    private final KafkaTemplate<String, UpdateStatusDto> kafkaTemplate;

    public VocabularyService(SentenceRepository sentenceRepository, VocabularyRepository vocabularyRepository, KafkaTemplate<String, UpdateStatusDto> kafkaTemplate){
        this.sentenceRepository = sentenceRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_VOCABULARY_TOPIC, groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP)
    public void processSentenceCreate(VocabularyDto vocabularyDto) {
        log.info("Received event: {}", EventType.CREATE_VOCABULARY);
        Vocabulary vocabulary = new Vocabulary();
        Date now = new Date();
        vocabulary.setId(vocabularyDto.id());
        vocabulary.setWord(vocabularyDto.word());
        vocabulary.setTranslations(vocabularyDto.translations());
        vocabulary.setSentenceIds(vocabularyDto.sentenceIds());
        vocabulary.setCreatedAt(now);
        vocabulary.setUpdatedAt(now);
        vocabularyRepository.save(vocabulary);
        UpdateStatusDto updateStatusDto = new UpdateStatusDto(vocabularyDto.id(), EventStatus.COMPLETED.name());
        kafkaTemplate.send(KafkaTopic.UPPATED_STATUS, updateStatusDto);
    }
}
