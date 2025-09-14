package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.vocabularyreadservice.entity.Vocabulary;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
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

    public VocabularyProjectionUpdaterImpl(VocabularyRepository vocabularyRepository){
        this.vocabularyRepository = vocabularyRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_VOCABULARY_TOPIC, groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP, containerFactory = "vocabularyKafkaListenerFactory")
    public void processSentenceCreate(VocabularyDto vocabularyDto) {
        log.info("Otrzymano event: {}", EventType.CREATE_VOCABULARY);
        Vocabulary vocabulary = new Vocabulary();
        try{
            vocabulary.setId(vocabularyDto.id());
            vocabulary.setWord(vocabularyDto.word());
            vocabulary.setTranslations(vocabularyDto.translations());
            vocabulary.setSentenceIds(vocabularyDto.sentenceIds());
            vocabularyRepository.save(vocabulary);
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas przetwarzania słowa: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas przetwarzania słowa: " + e.getMessage(), e);
        }
    }
}
