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
import com.learnwords.vocabularycommandservice.service.VocabularyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementacja serwisu tworzenia słownictwa (Command Side - CQRS).
 * 
 * <p>Klasa odpowiada za tworzenie nowych słów wraz z tłumaczeniami i opcjonalnymi
 * przykładowymi zdaniami. Zapisuje wszystkie zmiany do wzorca Outbox Pattern.
 * Implementuje stronę zapisu (Write Side) w architekturze CQRS.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Tworzenie pojedynczych słów i słów dla decków</li>
 *   <li>Tworzenie wielu słów jednocześnie (batch operations)</li>
 *   <li>Automatyczne tworzenie powiązanych zdań przykładowych</li>
 *   <li>Generowanie unikalnych ID dla słów (UUID)</li>
 *   <li>Zapisywanie eventów do tabeli Outbox</li>
 *   <li>Walidacja wymaganych pól z odpowiednim logowaniem</li>
 *   <li>Obsługa błędów z mechanizmem fail-safe dla operacji batch</li>
 * </ul>
 * 
 * <p>Wzorzec Outbox Pattern zapewnia eventual consistency między serwisami:
 * <ol>
 *   <li>Słówko jest zapisywane do tabeli Outbox w tej samej transakcji</li>
 *   <li>Powiązane zdania są również tworzone i zapisywane</li>
 *   <li>Outbox Relay pobiera eventy i publikuje do message brokera</li>
 *   <li>Read Service konsumuje eventy i aktualizuje projekcje read model</li>
 * </ol>
 * 
 * <p>Operacje batch wykorzystują mechanizm fail-safe - jeśli jedno słówko się nie powiedzie,
 * pozostałe są nadal zapisywane. Lista niepowodzeń jest logowana.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see VocabularyService
 * @see CreateWordDto
 * @see SendWordFromKafkaDto
 * @see SentenceService
 */
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

    /**
     * Tworzy nowe słówko bez przypisania do decka (standalone).
     * 
     * <p>Deleguje wykonanie do metody wewnętrznej createVocabularyInternal
     * z parametrem deckId ustawionym na null.
     * 
     * @param createWordDto dane nowego słówka
     * @return SendWordDto z danymi utworzonego słówka
     * @throws IllegalArgumentException gdy słowo lub tłumaczenia są null
     * @throws DataAccessException gdy wystąpi błąd podczas zapisu
     * @throws RuntimeException gdy wystąpi nieoczekiwany błąd
     */
    @Override
    public SendWordFromKafkaDto createVocabulary(CreateWordDto createWordDto) {
        log.debug("Tworzenie słówka - słowo: '{}'", createWordDto.getWord());
        return createVocabularyInternal(createWordDto, null);
    }

    /**
     * Tworzy nowe słówko przypisane do konkretnego decka.
     * 
     * <p>Waliduje deckId przed wykonaniem operacji. Deleguje do metody
     * wewnętrznej createVocabularyInternal.
     * 
     * @param createWordDto dane nowego słówka
     * @param deckId ID decka, do którego zostanie przypisane słówko
     * @return SendWordDto z danymi utworzonego słówka
     * @throws IllegalArgumentException gdy deckId jest null/pusty lub dane nieprawidłowe
     * @throws DataAccessException gdy wystąpi błąd podczas zapisu
     * @throws RuntimeException gdy wystąpi nieoczekiwany błąd
     */
    @Override
    public SendWordFromKafkaDto createVocabularyForDeck(CreateWordDto createWordDto, String deckId) {
        log.debug("Tworzenie słówka dla decka - deckId: '{}', słowo: '{}'", deckId, createWordDto.getWord());
        if (deckId == null || deckId.isEmpty()) {
            log.error("Tworzenie słówka - brak deckId");
            throw new IllegalArgumentException("DeckId must not be null or empty");
        }
        return createVocabularyInternal(createWordDto, deckId);
    }

    /**
     * Tworzy wiele słówek jednocześnie bez przypisania do decka (batch).
     * 
     * <p>Deleguje wykonanie do metody wewnętrznej createVocabulariesInternal
     * z parametrem deckId ustawionym na null.
     * 
     * @param createWordDtos lista danych nowych słówek
     * @return lista SendWordDto z utworzonymi słówkami (może być mniejsza w przypadku błędów)
     * @throws IllegalArgumentException gdy lista jest null lub pusta
     */
    @Override
    public List<SendWordFromKafkaDto> createVocabularies(List<CreateWordDto> createWordDtos) {
        log.debug("Tworzenie słówek batch - liczba: {}", createWordDtos.size());
        return createVocabulariesInternal(createWordDtos, null);
    }

    /**
     * Tworzy wiele słówek jednocześnie i przypisuje je do decka (batch).
     * 
     * <p>Waliduje deckId przed wykonaniem operacji. Deleguje do metody
     * wewnętrznej createVocabulariesInternal.
     * 
     * @param createWordDtos lista danych nowych słówek
     * @param deckId ID decka, do którego zostaną przypisane słówka
     * @return lista SendWordDto z utworzonymi słówkami (może być mniejsza w przypadku błędów)
     * @throws IllegalArgumentException gdy lista jest null/pusta lub deckId jest null/pusty
     */
    @Override
    public List<SendWordFromKafkaDto> createVocabulariesForDeck(List<CreateWordDto> createWordDtos, String deckId) {
        log.debug("Tworzenie słówek batch dla decka - deckId: '{}', liczba: {}", deckId, createWordDtos.size());
        if (deckId == null || deckId.trim().isEmpty()) {
            log.error("Tworzenie słówek batch - brak deckId");
            throw new IllegalArgumentException("DeckId must not be null or empty");
        }
        return createVocabulariesInternal(createWordDtos, deckId);
    }

    /**
     * Wewnętrzna metoda do tworzenia pojedynczego słówka.
     * 
     * <p>Proces tworzenia słówka:
     * <ol>
     *   <li>Walidacja wymaganych pól (słowo i tłumaczenia nie mogą być null)</li>
     *   <li>Generowanie unikalnego ID (UUID) dla słówka</li>
     *   <li>Utworzenie powiązanych zdań przykładowych (jeśli są w request)</li>
     *   <li>Utworzenie obiektu SendWordDto z danymi słówka</li>
     *   <li>Mapowanie do encji Outbox z typem eventu CREATE_VOCABULARY</li>
     *   <li>Zapisanie do tabeli Outbox</li>
     * </ol>
     * 
     * <p>Powiązane zdania są tworzone przez SentenceService i ich ID są dodawane
     * do listy sentenceIds w słówku.
     * 
     * @param createWordDto dane nowego słówka
     * @param deckId opcjonalny ID decka (może być null dla standalone)
     * @return SendWordDto z danymi utworzonego słówka
     * @throws IllegalArgumentException gdy słowo lub tłumaczenia są null
     * @throws DataAccessException gdy wystąpi błąd podczas zapisu do bazy
     * @throws RuntimeException gdy wystąpi nieoczekiwany błąd
     */
    private SendWordFromKafkaDto createVocabularyInternal(CreateWordDto createWordDto, String deckId) {
        String aggregateId = UUID.randomUUID().toString();
        List<String> sentenceIds = new ArrayList<>();
            if (createWordDto.getWord() == null || createWordDto.getTranslations() == null) {
                log.error("Tworzenie słówka - brak wymaganych pól");
                throw new IllegalArgumentException("Word and translations must not be null");
            }
            
            if (createWordDto.getSentences() != null && !createWordDto.getSentences().isEmpty()) {
                for (CreateSentenceDto createSentenceDto : createWordDto.getSentences()){
                    SendSentenceFromKafkaDto sentenceDto = sentenceService.createSentence(createSentenceDto, deckId);
                    sentenceIds.add(sentenceDto.id());
                    log.debug("Utworzono zdanie - sentenceId: '{}', słowo: '{}'", sentenceDto.id(), createWordDto.getWord());
                }
            }

            SendWordFromKafkaDto eventPayload = new SendWordFromKafkaDto(
                    aggregateId,
                    createWordDto.getWord(),
                    createWordDto.getTranslations(),
                    sentenceIds,
                    deckId
            );
            
            AggregateType aggregateType = deckId != null
                    ? AggregateType.VOCABULARYFORDECK
                    : AggregateType.VOCABULARY;

            Outbox outbox = entityToOutboxEntityMapper.map(
                    aggregateId,
                    aggregateType,
                    eventPayload,
                    EventType.CREATE_VOCABULARY,
                    deckId
            );
            outboxRepository.save(outbox);

            log.info("Słówko '{}' zostało utworzone - wordId: '{}'", eventPayload.word(), eventPayload.id());
            return eventPayload;

    }

    /**
     * Wewnętrzna metoda do tworzenia wielu słówek jednocześnie (batch).
     * 
     * <p>Wykorzystuje mechanizm fail-safe - jeśli jedno słówko się nie powiedzie,
     * pozostałe są nadal zapisywane. Lista niepowodzeń jest gromadzona i logowana.
     * 
     * <p>Proces:
     * <ol>
     *   <li>Walidacja listy wejściowej (nie może być null lub pusta)</li>
     *   <li>Iteracja przez wszystkie słówka</li>
     *   <li>Próba utworzenia każdego słówka przez createVocabularyInternal</li>
     *   <li>W przypadku błędu - logowanie i kontynuacja</li>
     *   <li>Zwrócenie listy pomyślnie utworzonych słówek</li>
     *   <li>Logowanie podsumowania (sukces/błędy)</li>
     * </ol>
     * 
     * @param createWordDtos lista danych nowych słówek
     * @param deckId opcjonalny ID decka (może być null dla standalone)
     * @return lista SendWordDto z pomyślnie utworzonymi słówkami (może być mniejsza niż wejściowa)
     * @throws IllegalArgumentException gdy lista jest null lub pusta
     */
    private List<SendWordFromKafkaDto> createVocabulariesInternal(List<CreateWordDto> createWordDtos, String deckId) {
        if (createWordDtos == null || createWordDtos.isEmpty()) {
            log.error("Tworzenie słówek batch - pusta lista");
            throw new IllegalArgumentException("Lista CreateWordDtos nie może być null lub pusta");
        }

        List<SendWordFromKafkaDto> createdVocabularies = new ArrayList<>();
        List<String> failedWords = new ArrayList<>();

        for (int i = 0; i < createWordDtos.size(); i++) {
            CreateWordDto createWordDto = createWordDtos.get(i);
            try {
                SendWordFromKafkaDto createdWord = createVocabularyInternal(createWordDto, deckId);
                createdVocabularies.add(createdWord);
            } catch (Exception e) {
                log.error("Błąd tworzenia słówka batch - indeks: {}, słowo: '{}', błąd: {}", 
                    i + 1, createWordDto.getWord(), e.getMessage());
                failedWords.add(createWordDto.getWord());
            }
        }

        if (!failedWords.isEmpty()) {
            log.warn("Utworzono słówka batch - sukces: {}/{}, błędy: {}", 
                createdVocabularies.size(), createWordDtos.size(), failedWords);
        } else {
            log.info("Utworzono słówka batch - liczba: {}", createdVocabularies.size());
        }

        return createdVocabularies;
    }
}