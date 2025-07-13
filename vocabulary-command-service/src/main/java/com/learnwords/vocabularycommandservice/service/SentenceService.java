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
import org.springframework.dao.DataAccessException;
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
    public SentenceDto createSentence(CreateSentenceDto csd, String deckId) {
        log.info("Rozpoczęcie tworzenia zdania: {}", csd.getSentence());
        String aggregateId = UUID.randomUUID().toString();
        try {
            if (csd.getSentence() == null || csd.getTranslation() == null) {
                log.error("Nie można utworzyć zdania, ponieważ brak jest wymaganych pól.");
                throw new IllegalArgumentException("Sentence and translation must not be null");
            }
            SentenceDto eventPayload = new SentenceDto(
                    aggregateId,
                    csd.getSentence(),
                    csd.getTranslation()
            );

            log.info("Stworzono zdanie z aggregateId: {}", aggregateId);

            Outbox outbox = entityToOutboxEntityMapper.map(
                    aggregateId,
                    AggregateType.SENTENCE,
                    eventPayload,
                    EventType.CREATE_SENTENCE,
                    deckId
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
