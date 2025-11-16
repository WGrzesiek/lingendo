package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy talia nie zawiera żadnych fiszek dostępnych do nauki.
 * 
 * <p>Może wystąpić w następujących sytuacjach:
 * <ul>
 *   <li>Talia jest pusta (nie ma żadnych fiszek)</li>
 *   <li>Wszystkie fiszki zostały już wyuczone (dla strategii "niewyuczone")</li>
 *   <li>Brak fiszek spełniających kryteria wybranej strategii</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-15
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoFlashcardsAvailableException extends RuntimeException {

    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public NoFlashcardsAvailableException() {
        super("Brak dostępnych fiszek do nauki w talii");
    }

    /**
     * Tworzy wyjątek z informacją o ID talii.
     *
     * @param deckId ID talii która nie zawiera dostępnych fiszek
     */
    public NoFlashcardsAvailableException(String deckId) {
        super("Brak dostępnych fiszek do nauki w talii o ID: '" + deckId + "'");
    }

    /**
     * Tworzy wyjątek z niestandardowym komunikatem.
     *
     * @param deckId ID talii
     * @param reason powód braku dostępnych fiszek
     */
    public NoFlashcardsAvailableException(String deckId, String reason) {
        super("Brak dostępnych fiszek do nauki w talii o ID: '" + deckId + "'. Powód: " + reason);
    }
}
