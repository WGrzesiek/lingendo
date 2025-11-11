package com.learnwords.vocabularycommandservice.service;

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
    
    /**
     * Tworzy nowe słówko bez przypisania do konkretnego decka (standalone).
     * 
     * <p>Słówko zostanie zapisane jako niezależny element i może zostać później
     * dodane do dowolnego decka. Można dołączyć przykładowe zdania, które również
     * zostaną utworzone.
     * 
     * @param createWordDto dane nowego słówka zawierające słowo, tłumaczenia i opcjonalne zdania
     * @return SendWordDto z danymi utworzonego słówka wraz z wygenerowanym ID
     * @throws IllegalArgumentException gdy słowo lub tłumaczenia są null
     * @throws DataAccessException gdy wystąpi błąd podczas zapisu do bazy danych
     * @throws RuntimeException gdy wystąpi nieoczekiwany błąd podczas tworzenia
     */
    SendWordDto createVocabulary(CreateWordDto createWordDto);
    
    /**
     * Tworzy nowe słówko przypisane do konkretnego decka.
     * 
     * <p>Słówko zostanie automatycznie powiązane ze wskazanym deckiem.
     * Zalecane gdy od razu wiadomo, że słówko ma należeć do konkretnego zestawu.
     * 
     * @param createWordDto dane nowego słówka zawierające słowo, tłumaczenia i opcjonalne zdania
     * @param deckId ID decka, do którego zostanie przypisane słówko
     * @return SendWordDto z danymi utworzonego słówka wraz z wygenerowanym ID
     * @throws IllegalArgumentException gdy słowo, tłumaczenia lub deckId są null/puste
     * @throws DataAccessException gdy wystąpi błąd podczas zapisu do bazy danych
     * @throws RuntimeException gdy wystąpi nieoczekiwany błąd podczas tworzenia
     */
    SendWordDto createVocabularyForDeck(CreateWordDto createWordDto, String deckId);
    
    /**
     * Tworzy wiele słówek jednocześnie bez przypisania do decka (batch operation).
     * 
     * <p>Operacja batch pozwala zaoszczędzić czas przy dodawaniu większej liczby słów.
     * W przypadku błędu przy jednym słówku, pozostałe są nadal zapisywane (fail-safe).
     * Zwracana lista zawiera tylko pomyślnie utworzone słówka.
     * 
     * @param createWordDtos lista danych nowych słówek
     * @return lista SendWordDto z danymi utworzonych słówek (może być mniejsza niż wejściowa w przypadku błędów)
     * @throws IllegalArgumentException gdy lista jest null lub pusta
     */
    List<SendWordDto> createVocabularies(List<CreateWordDto> createWordDtos);
    
    /**
     * Tworzy wiele słówek jednocześnie i przypisuje je do decka (batch operation).
     * 
     * <p>Najszybszy sposób na dodanie wielu słów do konkretnego decka naraz.
     * W przypadku błędu przy jednym słówku, pozostałe są nadal zapisywane (fail-safe).
     * Zwracana lista zawiera tylko pomyślnie utworzone słówka.
     * 
     * @param createWordDtos lista danych nowych słówek
     * @param deckId ID decka, do którego zostaną przypisane wszystkie słówka
     * @return lista SendWordDto z danymi utworzonych słówek (może być mniejsza niż wejściowa w przypadku błędów)
     * @throws IllegalArgumentException gdy lista jest null/pusta lub deckId jest null/pusty
     */
    List<SendWordDto> createVocabulariesForDeck(List<CreateWordDto> createWordDtos, String deckId);
}
