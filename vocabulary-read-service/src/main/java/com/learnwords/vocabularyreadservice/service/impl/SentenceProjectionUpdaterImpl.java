package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.vocabularyreadservice.entity.Sentence;
import com.learnwords.vocabularyreadservice.repository.SentenceRepository;
import com.learnwords.vocabularyreadservice.service.SentenceProjectionUpdater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SentenceProjectionUpdaterImpl implements SentenceProjectionUpdater {
    private final SentenceRepository sentenceRepository;

    public SentenceProjectionUpdaterImpl(SentenceRepository sentenceRepository){
        this.sentenceRepository = sentenceRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_SENTENCE_TOPIC, groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP, containerFactory = "sentenceKafkaListenerFactory")
    public void processSentenceCreate(SentenceDto sentenceDto) {
        log.info("Otrzymano event: {}", EventType.CREATE_SENTENCE);
        Sentence sentence = new Sentence();
        try {
            sentence.setId(sentenceDto.id());
            sentence.setSentence(sentenceDto.sentence());
            sentence.setTranslation(sentenceDto.translation());

            sentenceRepository.save(sentence);
            log.info("Zapisano zdanie o id: {}", sentenceDto.id());
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas przetwarzania zdania: {}", e.getMessage(), e);
        }
    }
}
