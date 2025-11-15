package com.learnwords.vocabularyreadservice.dto;

import java.util.List;

/**
 * DTO odpowiedzi po utworzeniu słówka (Command Side).
 * 
 * <p>Zawiera wszystkie dane utworzonego słówka, w tym wygenerowane ID,
 * listę ID powiązanych zdań oraz opcjonalny ID decka. Używane jako odpowiedź
 * z endpointów tworzących słówka oraz jako payload eventów w Outbox Pattern.
 * 
 * <p>Record zapewnia immutability - wszystkie pola są final i nie można ich zmienić
 * po utworzeniu obiektu.
 * 
 * <p>Przykład użycia:
 * <pre>
 * SendWordDto word = new SendWordDto(
 *     "550e8400-e29b-41d4-a716-446655440000",
 *     "hello",
 *     List.of("cześć", "witaj"),
 *     List.of("sentence-id-1", "sentence-id-2"),
 *     "deck-123"
 * );
 * </pre>
 * 
 * @param id unikalny identyfikator słówka (UUID)
 * @param word słowo w języku źródłowym
 * @param translations lista tłumaczeń słowa
 * @param sentenceIds lista ID powiązanych zdań przykładowych
 * @param deckId opcjonalny ID decka, do którego należy słówko (może być null)
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see CreateWordDto
 */
public record GetWordFromKafkaDto(String id, String word, List<String> translations, List<String> sentenceIds, String deckId) {

    /**
     * Konstruktor pomocniczy do tworzenia słówka bez przypisania do decka.
     *
     * <p>Automatycznie ustawia deckId na null, tworząc standalone słówko.
     *
     * @param id unikalny identyfikator słówka
     * @param word słowo w języku źródłowym
     * @param translations lista tłumaczeń
     * @param sentenceIds lista ID powiązanych zdań
     */
    public GetWordFromKafkaDto(String id, String word, List<String> translations, List<String> sentenceIds) {
        this(id, word, translations, sentenceIds, null);
    }
}

