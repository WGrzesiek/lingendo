package com.learnwords.deckservice.service;

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


    List<Flashcard> sortFlashcardsByStrategy(FlashcardFetchStrategy strategy, Long limit, List<Flashcard> flashcards);

    default FlashcardFetchStrategy[] getSupportedStrategies() {
        return FlashcardFetchStrategy.values();
    }
    default FlashcardFetchStrategy getDefaultStrategy() {
        return FlashcardFetchStrategy.UNLEARNED_FIRST;
    }
}
