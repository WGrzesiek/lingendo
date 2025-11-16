package com.learnwords.vocabularycommandservice.service.Impl;

import com.learnwords.common.AggregateType;
import com.learnwords.common.EventType;
import com.learnwords.common.dto.SendSentenceFromKafkaDto;
import com.learnwords.common.dto.SendWordFromKafkaDto;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.dto.CreateWordDto;
import com.learnwords.vocabularycommandservice.entity.Outbox;
import com.learnwords.vocabularycommandservice.mapper.EntityToOutboxEntityMapper;
import com.learnwords.vocabularycommandservice.repository.OutboxRepository;
import com.learnwords.vocabularycommandservice.service.SentenceService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementacja serwisu tworzenia przykładowych zdań (Command Side - CQRS).
 * 
 * <p>Klasa odpowiada za tworzenie nowych przykładowych zdań wraz z tłumaczeniami
 * i zapisywanie ich do wzorca Outbox Pattern.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Tworzenie pojedynczych i wielu zdań jednocześnie (batch)</li>
 *   <li>Generowanie unikalnych ID (UUID)</li>
 *   <li>Zapisywanie eventów do tabeli Outbox</li>
 *   <li>Mechanizm fail-safe dla operacji batch</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see SentenceService
 * @see CreateSentenceDto
 * @see SendSentenceFromKafkaDto
 */
@Slf4j
@Service
public class SentenceServiceImpl implements SentenceService {
    private final OutboxRepository outboxRepository;
    private final EntityToOutboxEntityMapper entityToOutboxEntityMapper;

    public SentenceServiceImpl(OutboxRepository outboxRepository, EntityToOutboxEntityMapper entityToOutboxEntityMapper){
        this.outboxRepository = outboxRepository;
        this.entityToOutboxEntityMapper = entityToOutboxEntityMapper;
    }

    /**
     * Tworzy nowe przykładowe zdanie wraz z tłumaczeniem.
     * 
     * @param csd dane nowego zdania
     * @param wordId ID słówka lub decka
     * @return dane utworzonego zdania
     */
    @Override
    @Transactional
    public SendSentenceFromKafkaDto createSentence(CreateSentenceDto csd, String wordId) {
        String aggregateId = UUID.randomUUID().toString();

        if (csd.getSentence() == null || csd.getTranslation() == null) {
            log.error("Tworzenie zdania - brak wymaganych pól");
            throw new IllegalArgumentException("Sentence and translation must not be null");
        }

        SendSentenceFromKafkaDto eventPayload = new SendSentenceFromKafkaDto(
                aggregateId,
                csd.getSentence(),
                csd.getTranslation(),
                wordId
        );

        Outbox outbox = entityToOutboxEntityMapper.map(
                aggregateId,
                AggregateType.SENTENCE,
                eventPayload,
                EventType.CREATE_SENTENCE,
                wordId
        );
        outboxRepository.save(outbox);

        log.info("Zdanie '{}' zostało utworzone - sentenceId: '{}'", csd.getSentence(), aggregateId);

        return eventPayload;
    }

    /**
     * Tworzy wiele zdań jednocześnie (batch).
     * 
     * @param csds lista danych nowych zdań
     * @param wordId ID słówka lub decka
     * @return lista utworzonych zdań
     */
    @Override
    @Transactional
    public List<SendSentenceFromKafkaDto> createSentences(List<CreateSentenceDto> csds, String wordId) {
        if (csds == null || csds.isEmpty()) {
            log.error("Tworzenie zdań batch - pusta lista");
            throw new IllegalArgumentException("Lista CreateSentenceDtos nie może być null lub pusta");
        }
        
        List<SendSentenceFromKafkaDto> createdSentences = new ArrayList<>();
        List<String> failedSentences = new ArrayList<>();

        for (int i = 0; i < csds.size(); i++) {
            CreateSentenceDto createSentenceDto = csds.get(i);
            try {
                SendSentenceFromKafkaDto createdSentence = createSentence(createSentenceDto, wordId);
                createdSentences.add(createdSentence);
            } catch (Exception e) {
                log.error("Błąd tworzenia zdania batch - indeks: {}, zdanie: '{}', błąd: {}",
                        i + 1, createSentenceDto.getSentence(), e.getMessage());
                failedSentences.add(createSentenceDto.getSentence());
            }
        }

        if (!failedSentences.isEmpty()) {
            log.warn("Utworzono zdania batch - sukces: {}/{}, błędy: {}",
                    createdSentences.size(), csds.size(), failedSentences);
        } else {
            log.info("Utworzono zdania batch - liczba: {}", createdSentences.size());
        }

        return createdSentences;
    }
}