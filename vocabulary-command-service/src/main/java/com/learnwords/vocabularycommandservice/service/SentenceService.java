package com.learnwords.vocabularycommandservice.service;

import com.learnwords.common.dto.SendSentenceFromKafkaDto;
import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;

import java.util.List;

/**
 * Serwis odpowiedzialny za tworzenie przykładowych zdań (Command Side - CQRS).
 * 
 * <p>Interfejs definiuje operacje tworzenia nowych przykładowych zdań wraz z tłumaczeniami.
 * Zdania są przypisywane do konkretnych słówek lub decków.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Tworzenie pojedynczych i wielu zdań (batch)</li>
 *   <li>Walidacja wymaganych pól (zdanie, tłumaczenie)</li>
 *   <li>Publikacja eventów przez Outbox Pattern</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see CreateSentenceDto
 * @see SendSentenceFromKafkaDto
 */
public interface SentenceService {
    
    SendSentenceFromKafkaDto createSentence(CreateSentenceDto csd, String wordId);    
    List<SendSentenceFromKafkaDto> createSentences(List<CreateSentenceDto> csds, String wordId);
}
