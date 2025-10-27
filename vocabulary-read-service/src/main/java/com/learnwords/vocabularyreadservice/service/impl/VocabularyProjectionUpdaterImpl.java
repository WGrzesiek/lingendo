package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.vocabularyreadservice.entity.Vocabulary;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
import com.learnwords.vocabularyreadservice.service.VocabularyProjectionUpdater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VocabularyProjectionUpdaterImpl implements VocabularyProjectionUpdater {
    private final VocabularyRepository vocabularyRepository;

    public VocabularyProjectionUpdaterImpl(VocabularyRepository vocabularyRepository){
        this.vocabularyRepository = vocabularyRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_VOCABULARY_TOPIC, groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP, containerFactory = "vocabularyKafkaListenerFactory")
    public void processSentenceCreate(VocabularyDto vocabularyDto) {
        log.info("Otrzymano event: {}", EventType.CREATE_VOCABULARY);
        Vocabulary vocabulary = new Vocabulary();

        vocabulary.setId(vocabularyDto.id());
        vocabulary.setWord(vocabularyDto.word());
        vocabulary.setTranslations(vocabularyDto.translations());
        vocabulary.setSentenceIds(vocabularyDto.sentenceIds());
        vocabularyRepository.save(vocabulary);
    }
}
