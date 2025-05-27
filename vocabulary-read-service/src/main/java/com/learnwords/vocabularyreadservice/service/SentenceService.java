package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.vocabularyreadservice.entity.Sentence;
import com.learnwords.vocabularyreadservice.repository.SentenceRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class SentenceService {
    private final SentenceRepository sentenceRepository;

    public SentenceService(SentenceRepository sentenceRepository){
        this.sentenceRepository = sentenceRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_SENTENCE_TOPIC, groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP)
    public void processSentenceCreate(SentenceDto sentenceDto) {
        log.info("Otrzymano event: {}", EventType.CREATE_SENTENCE);
        Sentence sentence = new Sentence();
        Date now = new Date();
        sentence.setId(sentenceDto.id());
        sentence.setSentence(sentenceDto.sentence());
        sentence.setTranslation(sentenceDto.translation());
        sentence.setCreatedAt(now);
        sentence.setUpdatedAt(now);
        sentenceRepository.save(sentence);
    }
}
