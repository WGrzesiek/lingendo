package com.learnwords.vocabularycommandservice.service.Impl;

import com.learnwords.common.AggregateType;
import com.learnwords.common.EventType;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.dto.CreateWordDto;
import com.learnwords.vocabularycommandservice.dto.SendSentenceDto;
import com.learnwords.vocabularycommandservice.dto.SendWordDto;
import com.learnwords.vocabularycommandservice.entity.Outbox;
import com.learnwords.vocabularycommandservice.mapper.EntityToOutboxEntityMapper;
import com.learnwords.vocabularycommandservice.repository.OutboxRepository;
import com.learnwords.vocabularycommandservice.service.SentenceService;
import com.learnwords.vocabularycommandservice.service.VocabularyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VocabularyServiceImpl implements VocabularyService {
    private final OutboxRepository outboxRepository;
    private final EntityToOutboxEntityMapper entityToOutboxEntityMapper;
    private final SentenceService sentenceService;

    public VocabularyServiceImpl(OutboxRepository outboxRepository, EntityToOutboxEntityMapper entityToOutboxEntityMapper, SentenceService sentenceService) {
        this.outboxRepository = outboxRepository;
        this.entityToOutboxEntityMapper = entityToOutboxEntityMapper;
        this.sentenceService = sentenceService;
    }

    @Override
    public SendWordDto createVocabulary(CreateWordDto createWordDto) {
        return createVocabularyInternal(createWordDto, null);
    }

    @Override
    public SendWordDto createVocabularyForDeck(CreateWordDto createWordDto, String deckId) {
        log.info("Rozpoczęcie tworzenia słówka dla decka: {}", deckId);
        if (deckId == null || deckId.isEmpty()) {
            log.error("DeckId nie może być null lub pusty");
            throw new IllegalArgumentException("DeckId must not be null or empty");
        }
        return createVocabularyInternal(createWordDto, deckId);
    }

    @Override
    public List<SendWordDto> createVocabularies(List<CreateWordDto> createWordDtos) {
        return createVocabulariesInternal(createWordDtos, null);
    }

    @Override
    public List<SendWordDto> createVocabulariesForDeck(List<CreateWordDto> createWordDtos, String deckId) {
        log.info("Rozpoczęcie tworzenia {} słówek dla decka: {}", createWordDtos.size(), deckId);
        if (deckId == null || deckId.trim().isEmpty()) {
            log.error("DeckId nie może być null lub pusty");
            throw new IllegalArgumentException("DeckId must not be null or empty");
        }
        return createVocabulariesInternal(createWordDtos, deckId);
    }

    private SendWordDto createVocabularyInternal(CreateWordDto createWordDto, String deckId) {
        log.info("Rozpoczęcie tworzenia słówka: {}", createWordDto.getWord());
        String aggregateId = UUID.randomUUID().toString();
        List<String> sentenceIds = new ArrayList<>();
        try {
            if (createWordDto.getWord() == null || createWordDto.getTranslations() == null) {
                log.error("Nie można utworzyć słówka, ponieważ brak jest wymaganych pól.");
                throw new IllegalArgumentException("Word and translations must not be null");
            }
            if (createWordDto.getSentences() != null && !createWordDto.getSentences().isEmpty()) {
                for (CreateSentenceDto createSentenceDto : createWordDto.getSentences()){
                    SendSentenceDto sentenceDto = sentenceService.createSentence(createSentenceDto, deckId);
                    sentenceIds.add(sentenceDto.id());
                    log.info("Stworzono nowe zdania: {}, dla slowka: {}", sentenceDto.id(), createWordDto.getWord());
                }
            }
            SendWordDto eventPayload = new SendWordDto(
                    aggregateId,
                    createWordDto.getWord(),
                    createWordDto.getTranslations(),
                    sentenceIds,
                    deckId
            );
            log.info("Stworzono słówko z aggregateId: {}", aggregateId);
            Outbox outbox = entityToOutboxEntityMapper.map(
                    aggregateId,
                    AggregateType.VOCABULARY,
                    eventPayload,
                    EventType.CREATE_VOCABULARY,
                    deckId
            );
            outboxRepository.save(outbox);
            log.info("Stworzono słówko: ID: {}, słowo: '{}', tłumaczenia: {}, powiązane zdania: {}",
                    eventPayload.id(), eventPayload.word(), eventPayload.translations(), eventPayload.sentenceIds());
            return eventPayload;

        } catch(DataAccessException e){
            log.error("Błąd podczas zapisywania słówka: {}", e.getMessage(), e);
            throw e;
        } catch(Exception e){
            log.error("Błąd podczas zapisywania słówka: {}", e.getMessage(), e);
            throw new RuntimeException("Nie udało się zapisać słówka", e);
        }
    }

    private List<SendWordDto> createVocabulariesInternal(List<CreateWordDto> createWordDtos, String deckId) {
        log.info("Rozpoczęcie tworzenia {} słówek", createWordDtos.size());
        
        if (createWordDtos == null || createWordDtos.isEmpty()) {
            log.error("Lista słówek nie może być null lub pusta");
            throw new IllegalArgumentException("CreateWordDtos list must not be null or empty");
        }

        List<SendWordDto> createdVocabularies = new ArrayList<>();
        List<String> failedWords = new ArrayList<>();

        for (int i = 0; i < createWordDtos.size(); i++) {
            CreateWordDto createWordDto = createWordDtos.get(i);
            try {
                SendWordDto createdWord = createVocabularyInternal(createWordDto, deckId);
                createdVocabularies.add(createdWord);
                log.info("Pomyślnie utworzono słówko {}/{}: '{}'", i + 1, createWordDtos.size(), createWordDto.getWord());
            } catch (Exception e) {
                log.error("Błąd podczas tworzenia słówka {}/{}: '{}' - {}", 
                    i + 1, createWordDtos.size(), createWordDto.getWord(), e.getMessage(), e);
                failedWords.add(createWordDto.getWord());
            }
        }

        if (!failedWords.isEmpty()) {
            log.warn("Utworzono {}/{} słówek. Błędy podczas tworzenia: {}", 
                createdVocabularies.size(), createWordDtos.size(), failedWords);
        } else {
            log.info("Pomyślnie utworzono wszystkie {} słówek", createdVocabularies.size());
        }

        return createdVocabularies;
    }
}