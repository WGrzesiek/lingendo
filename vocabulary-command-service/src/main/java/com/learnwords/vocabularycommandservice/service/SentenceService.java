package com.learnwords.vocabularycommandservice.service;

import com.learnwords.common.AggregateType;
import com.learnwords.common.EventType;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.entity.Outbox;
import com.learnwords.vocabularycommandservice.mapper.EntityToOutboxEntityMapper;
import com.learnwords.vocabularycommandservice.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SentenceService {
    private final OutboxRepository outboxRepository;
    private final EntityToOutboxEntityMapper entityToOutboxEntityMapper;

    public SentenceService(OutboxRepository outboxRepository, EntityToOutboxEntityMapper entityToOutboxEntityMapper){
        this.outboxRepository = outboxRepository;
        this.entityToOutboxEntityMapper = entityToOutboxEntityMapper;
    }

    @Transactional
    public SentenceDto createSentence(CreateSentenceDto csd) {

        String aggregateId = UUID.randomUUID().toString();
        SentenceDto eventPayload = new SentenceDto(
                aggregateId,
                csd.getSentence(),
                csd.getTranslation()
        );

        Outbox outbox = entityToOutboxEntityMapper.map(
                aggregateId,
                AggregateType.SENTENCE,
                eventPayload,
                EventType.CREATE_SENTENCE
        );
        outboxRepository.save(outbox);
        return eventPayload;
    }
}
