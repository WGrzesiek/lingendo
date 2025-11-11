package com.learnwords.vocabularycommandservice.service.Impl;

import com.learnwords.common.AggregateType;
import com.learnwords.common.EventType;
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

/**
 * Implementacja serwisu tworzenia przykładowych zdań (Command Side - CQRS).
 * 
 * <p>Klasa odpowiada za tworzenie nowych przykładowych zdań wraz z tłumaczeniami
 * i zapisywanie ich do wzorca Outbox Pattern. Implementuje stronę zapisu (Write Side)
 * w architekturze CQRS.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Tworzenie nowych zdań z walidacją wymaganych pól</li>
 *   <li>Generowanie unikalnych ID dla zdań (UUID)</li>
 *   <li>Zapisywanie eventów do tabeli Outbox</li>
 *   <li>Obsługa błędów z odpowiednim logowaniem</li>
 *   <li>Transakcyjność operacji (@Transactional)</li>
 * </ul>
 * 
 * <p>Wzorzec Outbox Pattern zapewnia eventual consistency między serwisami:
 * <ol>
 *   <li>Zdanie jest zapisywane do tabeli Outbox w tej samej transakcji</li>
 *   <li>Outbox Relay pobiera eventy i publikuje do message brokera</li>
 *   <li>Read Service konsumuje eventy i aktualizuje projekcje read model</li>
 * </ol>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see SentenceService
 * @see CreateSentenceDto
 * @see SendSentenceDto
 */
@Slf4j
@Service
public class SentenceServiceImpl implements SentenceService {
    private final OutboxRepository outboxRepository;
    private final EntityToOutboxEntityMapper entityToOutboxEntityMapper;

    /**
     * Konstruktor serwisu z wstrzykiwaniem zależności.
     * 
     * @param outboxRepository repozytorium do zapisu eventów Outbox
     * @param entityToOutboxEntityMapper mapper do konwersji DTO na encję Outbox
     */
    public SentenceServiceImpl(OutboxRepository outboxRepository, EntityToOutboxEntityMapper entityToOutboxEntityMapper){
        this.outboxRepository = outboxRepository;
        this.entityToOutboxEntityMapper = entityToOutboxEntityMapper;
    }

    /**
     * Tworzy nowe przykładowe zdanie wraz z tłumaczeniem.
     * 
     * <p>Proces tworzenia zdania:
     * <ol>
     *   <li>Walidacja wymaganych pól (zdanie i tłumaczenie nie mogą być null)</li>
     *   <li>Generowanie unikalnego ID (UUID) dla zdania</li>
     *   <li>Utworzenie obiektu SendSentenceDto z danymi zdania</li>
     *   <li>Mapowanie do encji Outbox z typem eventu CREATE_SENTENCE</li>
     *   <li>Zapisanie do tabeli Outbox w transakcji</li>
     * </ol>
     * 
     * <p>W przypadku błędu:
     * <ul>
     *   <li>DataAccessException - błąd bazy danych (re-throw)</li>
     *   <li>IllegalArgumentException - błąd walidacji (brak wymaganych pól)</li>
     *   <li>Exception - inne błędy opakowywane w RuntimeException</li>
     * </ul>
     * 
     * <p>Metoda jest oznaczona @Transactional - wszystkie operacje są wykonywane
     * w ramach jednej transakcji bazodanowej.
     * 
     * @param csd dane nowego zdania (zdanie w języku źródłowym i tłumaczenie)
     * @param wordId ID słówka lub decka, do którego zostanie przypisane zdanie
     * @return SendSentenceDto z danymi utworzonego zdania wraz z wygenerowanym ID
     * @throws IllegalArgumentException gdy zdanie lub tłumaczenie jest null
     * @throws DataAccessException gdy wystąpi błąd podczas zapisu do bazy danych
     * @throws RuntimeException gdy wystąpi nieoczekiwany błąd podczas tworzenia
     */
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
            
            log.info("Zdanie zapisane do Outbox: ID: {}, zdanie: '{}', wordId: {}", 
                    aggregateId, csd.getSentence(), wordId);
            
            return eventPayload;

        } catch (DataAccessException e) {
            log.error("Błąd podczas zapisywania zdania: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Błąd podczas tworzenia zdania: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create sentence", e);
        }
    }
}