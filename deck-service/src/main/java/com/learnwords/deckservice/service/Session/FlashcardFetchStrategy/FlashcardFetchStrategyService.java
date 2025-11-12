package com.learnwords.deckservice.service.Session.FlashcardFetchStrategy;

import com.learnwords.deckservice.entity.Flashcard;

import java.util.List;

/**
 * Serwis strategii pobierania i sortowania fiszek dla sesji nauki.
 * 
 * <p>Odpowiada za implementację różnych strategii wyboru fiszek do sesji:
 * <ul>
 *   <li>ALPHABETICAL - sortowanie alfabetyczne po słowie</li>
 *   <li>RANDOM - losowa kolejność fiszek</li>
 *   <li>REVERSE_ALPHABETICAL - sortowanie odwrotnie alfabetyczne</li>
 *   <li>UNLEARNED_FIRST - nienauczone fiszki na początku, potem nauczone</li>
 * </ul>
 * 
 * <p>Każda strategia może mieć limit liczby fiszek (flashcardsPerSession).
 * Jeśli limit jest null, zwracane są wszystkie fiszki.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 * @see FlashcardFetchStrategy
 */
public interface FlashcardFetchStrategyService {

    /**
     * Sortuje fiszki według wybranej strategii i opcjonalnie limituje liczbę.
     * 
     * <p>Implementacja powinna:
     * <ol>
     *   <li>Zastosować sortowanie według strategii</li>
     *   <li>Jeśli limit jest ustawiony, zwrócić tylko pierwszych N fiszek</li>
     *   <li>Jeśli limit jest null, zwrócić wszystkie posortowane fiszki</li>
     * </ol>
     * 
     * <p>Przykład użycia:
     * <pre>
     * List&lt;Flashcard&gt; flashcards = flashcardRepository.findByDeckId(deckId);
     * List&lt;Flashcard&gt; sorted = strategyService.sortFlashcardsByStrategy(
     *     FlashcardFetchStrategy.UNLEARNED_FIRST, 
     *     20L, 
     *     flashcards
     * );
     * // sorted zawiera max 20 fiszek, nienauczone najpierw
     * </pre>
     * 
     * @param strategy strategia sortowania (ALPHABETICAL, RANDOM, UNLEARNED_FIRST, REVERSE_ALPHABETICAL)
     * @param limit maksymalna liczba fiszek do zwrócenia (null = bez limitu)
     * @param flashcards lista fiszek do posortowania
     * @return posortowana lista fiszek (z limitem jeśli ustawiony)
     * @throws IllegalArgumentException jeśli strategy jest null lub flashcards jest null
     */
    List<Flashcard> sortFlashcardsByStrategy(FlashcardFetchStrategy strategy, Long limit, List<Flashcard> flashcards);
    
    /**
     * Pobiera listę wszystkich obsługiwanych strategii.
     * 
     * @return tablica wszystkich dostępnych strategii
     */
    default FlashcardFetchStrategy[] getSupportedStrategies() {
        return FlashcardFetchStrategy.values();
    }
    
    /**
     * Pobiera domyślną strategię pobierania fiszek.
     * 
     * <p>Używana gdy użytkownik nie wybierze strategii explicite.
     * 
     * @return domyślna strategia (UNLEARNED_FIRST)
     */
    default FlashcardFetchStrategy getDefaultStrategy() {
        return FlashcardFetchStrategy.UNLEARNED_FIRST;
    }
}
