package com.learnwords.vocabularycommandservice.service;

import com.learnwords.common.AggregateType;
import com.learnwords.common.EventType;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.dto.CreateVocabularyDto;
import com.learnwords.vocabularycommandservice.entity.Outbox;
import com.learnwords.vocabularycommandservice.mapper.EntityToOutboxEntityMapper;
import com.learnwords.vocabularycommandservice.repository.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class VocabularyService {
    private final OutboxRepository outboxRepository;
    private final EntityToOutboxEntityMapper entityToOutboxEntityMapper;
    private final SentenceService sentenceService;

    public VocabularyService(OutboxRepository outboxRepository, EntityToOutboxEntityMapper entityToOutboxEntityMapper, SentenceService sentenceService){
        this.outboxRepository = outboxRepository;
        this.entityToOutboxEntityMapper = entityToOutboxEntityMapper;
        this.sentenceService = sentenceService;
    }

    public VocabularyDto createVocabulary(CreateVocabularyDto createVocabularyDto, String userId) {
        log.debug("Rozpoczęcie tworzenia słówka: {}", createVocabularyDto.getWord());
        List<String> sentenceIds = new ArrayList<>();
        String aggregateId = UUID.randomUUID().toString();
        try {
        if (createVocabularyDto.getWord() == null || createVocabularyDto.getTranslations() == null) {
            log.error("Nie można utworzyć słówka, ponieważ brak jest wymaganych pól.");
            throw new IllegalArgumentException("Word and translations must not be null");
        }
            if (createVocabularyDto.getSentences() != null && !createVocabularyDto.getSentences().isEmpty()) {
                    for (CreateSentenceDto createSentenceDto : createVocabularyDto.getSentences()){
                        SentenceDto sentenceDto = sentenceService.createSentence(createSentenceDto, userId);
                        sentenceIds.add(sentenceDto.id());
                        log.info("Stworzono nowe zdania: {}, dla slowka: {}", sentenceDto.id(), createVocabularyDto.getWord());
                    }
            }
                VocabularyDto eventPayload = new VocabularyDto(
                        aggregateId,
                        createVocabularyDto.getWord(),
                        createVocabularyDto.getTranslations(),
                        sentenceIds
                );
                log.info("Stworzono słówko z aggregateId: {}", aggregateId);
                Outbox outbox = entityToOutboxEntityMapper.map(
                        aggregateId,
                        AggregateType.VOCABULARY,
                        eventPayload,
                        EventType.CREATE_VOCABULARY,
                        userId
                );
                outboxRepository.save(outbox);
                log.info("Stworzono słówko: ID: {}, słowo: '{}', tłumaczenia: {}, powiązane zdania: {}",
                        eventPayload.id(), eventPayload.word(), eventPayload.translations(), eventPayload.sentenceIds());
                return eventPayload;
            } catch (DataAccessException e) {
                log.error("Błąd podczas zapisywania słówka: {}", e.getMessage(), e);
                throw e;
            }
            catch (Exception e) {
                log.error("Błąd podczas zapisywania słówka: {}", e.getMessage(), e);
                throw new RuntimeException("Nie udało się zapisać słówka", e);
            }
        }

    }




