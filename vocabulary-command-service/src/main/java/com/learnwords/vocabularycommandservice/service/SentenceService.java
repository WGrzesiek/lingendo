package com.learnwords.vocabularycommandservice.service;

import com.learnwords.common.EventStatus;
import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.UpdateStatusDto;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.entity.Outbox;
import com.learnwords.vocabularycommandservice.mapper.EntityToOutboxEntityMapper;
import com.learnwords.vocabularycommandservice.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SentenceService {
    private final OutboxRepository outboxRepository;
    private final EntityToOutboxEntityMapper entityToOutboxEntityMapper;
    private final KafkaTemplate<String, SentenceDto> kafkaTemplate;

    public SentenceService(OutboxRepository outboxRepository, EntityToOutboxEntityMapper entityToOutboxEntityMapper, KafkaTemplate<String, SentenceDto> kafkaTemplate){
        this.outboxRepository = outboxRepository;
        this.entityToOutboxEntityMapper = entityToOutboxEntityMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public SentenceDto createSentence(CreateSentenceDto createSentenceDto){
        String id = UUID.randomUUID().toString();
        SentenceDto sentenceDto = new SentenceDto(id, createSentenceDto.getSentence(), createSentenceDto.getTranslation());
        Outbox outbox =  entityToOutboxEntityMapper.map(id, sentenceDto, EventType.CREATE_SENTENCE);
        outboxRepository.save(outbox);
        kafkaTemplate.send(KafkaTopic.CREATE_SENTENCE_TOPIC, sentenceDto);
        return new SentenceDto(id, sentenceDto.sentence(), sentenceDto.translation());
    }

}
