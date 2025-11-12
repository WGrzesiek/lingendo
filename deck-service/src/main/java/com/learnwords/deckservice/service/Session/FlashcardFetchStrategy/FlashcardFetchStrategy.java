package com.learnwords.deckservice.service.Session.FlashcardFetchStrategy;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Enum definiujący strategie pobierania i sortowania fiszek dla sesji nauki.
 * 
 * <p>Każda strategia określa w jakiej kolejności fiszki będą prezentowane
 * użytkownikowi podczas sesji nauki:
 * 
 * <ul>
 *   <li><b>ALPHABETICAL</b> - sortowanie alfabetyczne po słowie (A-Z)</li>
 *   <li><b>RANDOM</b> - losowa kolejność fiszek</li>
 *   <li><b>REVERSE_ALPHABETICAL</b> - sortowanie odwrotnie alfabetyczne (Z-A)</li>
 *   <li><b>UNLEARNED_FIRST</b> - nienauczone fiszki najpierw, potem nauczone (domyślna)</li>
 * </ul>
 * 
 * <p>Strategia jest wybierana przy inicjalizacji sesji i wpływa na
 * kolejność fiszek zwracaną przez {@link FlashcardFetchStrategyService}.
 * 
 * <p>Przykład użycia:
 * <pre>
 * FlashcardFetchStrategy strategy = FlashcardFetchStrategy.UNLEARNED_FIRST;
 * String sessionId = sessionService.initializeSession(deckId, strategy);
 * </pre>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 * @see FlashcardFetchStrategyService
 */
public enum FlashcardFetchStrategy {

    ALPHABETICAL,
    RANDOM,
    REVERSE_ALPHABETICAL,
    UNLEARNED_FIRST;

    /**
     * Tworzy enum z wartości String.
     * 
     * <p>Używane przez Jackson podczas deserializacji JSON.
     * 
     * @param value wartość string (np. "alphabetical", "RANDOM")
     * @return odpowiednia wartość enum
     * @throws IllegalArgumentException jeśli wartość nie pasuje do żadnej strategii
     */
    @JsonCreator
    public static FlashcardFetchStrategy fromValue(String value) {
        if (value == null || value.isBlank()) {
            return UNLEARNED_FIRST; // Domyślna strategia
        }
        return FlashcardFetchStrategy.valueOf(value.toUpperCase());
    }
}
