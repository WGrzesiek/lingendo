package com.learnwords.vocabularycommandservice.service;

import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.dto.SendSentenceDto;

/**
 * Serwis odpowiedzialny za tworzenie przykładowych zdań (Command Side - CQRS).
 * 
 * <p>Interfejs definiuje operacje tworzenia nowych przykładowych zdań wraz z tłumaczeniami.
 * Zdania są przypisywane do konkretnych słówek lub decków i służą jako materiał 
 * kontekstowy do nauki słownictwa.
 * 
 * <p>Implementacja wykorzystuje wzorzec Outbox Pattern do zapewnienia eventual consistency
 * między serwisami w architekturze mikroserwisowej.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Tworzenie nowych zdań z tłumaczeniami</li>
 *   <li>Walidacja wymaganych pól (zdanie, tłumaczenie)</li>
 *   <li>Przypisywanie zdań do słówek/decków</li>
 *   <li>Publikacja eventów przez Outbox Pattern</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see CreateSentenceDto
 * @see SendSentenceDto
 */
public interface SentenceService {
    
    /**
     * Tworzy nowe przykładowe zdanie wraz z tłumaczeniem.
     * 
     * <p>Zdanie zostanie zapisane do wzorca Outbox i przypisane do wskazanego 
     * słówka lub decka. Event zostanie później przetworzony przez Read Service 
     * w ramach wzorca CQRS.
     * 
     * <p>Metoda jest transakcyjna - w przypadku błędu wszystkie zmiany zostaną wycofane.
     * 
     * @param csd dane nowego zdania zawierające zdanie w języku źródłowym i tłumaczenie
     * @param wordId ID słówka lub decka, do którego zostanie przypisane zdanie
     * @return SendSentenceDto z danymi utworzonego zdania wraz z wygenerowanym ID
     * @throws IllegalArgumentException gdy zdanie lub tłumaczenie jest null
     * @throws DataAccessException gdy wystąpi błąd podczas zapisu do bazy danych
     * @throws RuntimeException gdy wystąpi nieoczekiwany błąd podczas tworzenia
     */
    SendSentenceDto createSentence(CreateSentenceDto csd, String wordId);
}
