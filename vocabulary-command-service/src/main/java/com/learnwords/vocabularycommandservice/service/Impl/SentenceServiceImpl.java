package com.learnwords.vocabularycommandservice.service.Impl;

import com.learnwords.common.AggregateType;
import com.learnwords.common.EventType;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.dto.SendSentenceDto;
import com.learnwords.vocabularycommandservice.entity.Outbox;
import com.learnwords.vocabularycommandservice.mapper.EntityToOutboxEntityMapper;
import com.learnwords.vocabularycommandservice.repository.OutboxRepository;
import com.learnwords.vocabularycommandservice.service.SentenceService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SentenceServiceImpl implements SentenceService {
    private final OutboxRepository outboxRepository;
    private final EntityToOutboxEntityMapper entityToOutboxEntityMapper;

    public SentenceServiceImpl(OutboxRepository outboxRepository, EntityToOutboxEntityMapper entityToOutboxEntityMapper){
        this.outboxRepository = outboxRepository;
        this.entityToOutboxEntityMapper = entityToOutboxEntityMapper;
    }

    @Override
    @Transactional
    public SendSentenceDto createSentence(CreateSentenceDto csd, String wordId) {
        log.info("Rozpoczęcie tworzenia zdania: {}", csd.getSentence());
        String aggregateId = UUID.randomUUID().toString();
        try {
            if (csd.getSentence() == null || csd.getTranslation() == null) {
                log.error("Nie można utworzyć zdania, ponieważ brak jest wymaganych pól.");
                throw new IllegalArgumentException("Sentence and translation must not be null");
            }
            SendSentenceDto eventPayload = new SendSentenceDto(
                    aggregateId,
                    csd.getSentence(),
                    csd.getTranslation(),
                    wordId
            );

            log.info("Stworzono zdanie z aggregateId: {}", aggregateId);

            Outbox outbox = entityToOutboxEntityMapper.map(
                    aggregateId,
                    AggregateType.SENTENCE,
                    eventPayload,
                    EventType.CREATE_SENTENCE,
                    wordId
            );
            outboxRepository.save(outbox);
            return eventPayload;

        }catch (DataAccessException e) {
            log.error("Błąd podczas zapisywania zdania: {}", e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            log.error("Błąd podczas tworzenia zdania: {}", e.getMessage());
            throw new RuntimeException("Failed to create sentence", e);
        }
    }
}