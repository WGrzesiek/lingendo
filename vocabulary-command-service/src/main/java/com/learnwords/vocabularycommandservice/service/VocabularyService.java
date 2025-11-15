package com.learnwords.vocabularycommandservice.service;

import com.learnwords.common.dto.SendWordFromKafkaDto;
import com.learnwords.vocabularycommandservice.dto.CreateWordDto;
import com.learnwords.vocabularycommandservice.dto.SendWordDto;

import java.util.List;

/**
 * Serwis odpowiedzialny za tworzenie słownictwa (Command Side - CQRS).
 * 
 * <p>Interfejs definiuje operacje tworzenia nowych słów wraz z tłumaczeniami 
 * i opcjonalnymi przykładowymi zdaniami. Implementuje stronę zapisu (Write Side)
 * w architekturze CQRS i wykorzystuje wzorzec Outbox Pattern.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Tworzenie pojedynczych słów (standalone lub dla decka)</li>
 *   <li>Tworzenie wielu słów jednocześnie (batch operations)</li>
 *   <li>Automatyczne tworzenie powiązanych zdań przykładowych</li>
 *   <li>Walidacja wymaganych pól (słowo, tłumaczenia)</li>
 *   <li>Publikacja eventów przez Outbox Pattern</li>
 * </ul>
 * 
 * <p>Wzorzec Outbox Pattern zapewnia eventual consistency między serwisami
 * w architekturze mikroserwisowej. Eventy są najpierw zapisywane do tabeli Outbox,
 * a następnie publikowane do message brokera przez dedykowany relay.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see CreateWordDto
 * @see SendWordDto
 */
public interface VocabularyService {

    SendWordFromKafkaDto createVocabulary(CreateWordDto createWordDto);
    SendWordFromKafkaDto createVocabularyForDeck(CreateWordDto createWordDto, String deckId);
    List<SendWordFromKafkaDto> createVocabularies(List<CreateWordDto> createWordDtos);
    List<SendWordFromKafkaDto> createVocabulariesForDeck(List<CreateWordDto> createWordDtos, String deckId);
}
