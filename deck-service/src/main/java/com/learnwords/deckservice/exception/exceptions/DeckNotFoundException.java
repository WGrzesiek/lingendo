package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy talia o podanym ID nie została znaleziona.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DeckNotFoundException extends RuntimeException {
    
    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public DeckNotFoundException() {
        super("Talia nie została znaleziona");
    }
    
    /**
     * Tworzy wyjątek z informacją o ID talii.
     *
     * @param deckId ID talii która nie została znaleziona
     */
    public DeckNotFoundException(String deckId) {
        super("Talia o ID " + deckId + " nie została znaleziona");
    }
}
