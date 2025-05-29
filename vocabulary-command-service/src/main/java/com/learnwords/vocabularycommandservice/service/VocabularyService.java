package com.learnwords.vocabularycommandservice.service;

import com.learnwords.common.AggregateType;
import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.UpdateStatusDto;
import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.dto.CreateVocabularyDto;
import com.learnwords.vocabularycommandservice.entity.Outbox;
import com.learnwords.vocabularycommandservice.mapper.EntityToOutboxEntityMapper;
import com.learnwords.vocabularycommandservice.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public VocabularyDto createVocabulary(CreateVocabularyDto createVocabularyDto) {
        log.debug("Rozpoczęcie tworzenia słówka: {}", createVocabularyDto.getWord());
        List<String> sentenceIds = new ArrayList<>();

            if (createVocabularyDto.getSentences() != null && !createVocabularyDto.getSentences().isEmpty()) {
                try {
                    for (CreateSentenceDto createSentenceDto : createVocabularyDto.getSentences()){
                        SentenceDto sentenceDto = sentenceService.createSentence(createSentenceDto);
                        sentenceIds.add(sentenceDto.id());
                        log.info("Stworzono nowe zdania: {}", sentenceDto);
                    }
                } catch (Exception e) {
                    log.error("Błąd podczas zapisywania zdań: {}", e.getMessage(), e);
                    throw new RuntimeException("Nie udało się zapisać zdań", e);
                }
            }
            try {
                String aggregateId = UUID.randomUUID().toString();
                VocabularyDto eventPayload = new VocabularyDto(
                        aggregateId,
                        createVocabularyDto.getWord(),
                        createVocabularyDto.getTranslations(),
                        sentenceIds
                );

                Outbox outbox = entityToOutboxEntityMapper.map(
                        aggregateId,
                        AggregateType.VOCABULARY,
                        eventPayload,
                        EventType.CREATE_VOCABULARY
                );

                outboxRepository.save(outbox);

                log.info("Stworzono słówko: ID: {}, słowo: '{}', tłumaczenia: {}, powiązane zdania: {}",
                        eventPayload.id(), eventPayload.word(), eventPayload.translations(), eventPayload.sentenceIds());
                return eventPayload;
            } catch (Exception e) {
                log.error("Błąd podczas zapisywania słówka: {}", e.getMessage(), e);
                throw new RuntimeException("Nie udało się zapisać słówka", e);
            }
        }

    }




