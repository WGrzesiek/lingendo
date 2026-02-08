package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy fiszka o podanym ID nie została znaleziona.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class FlashcardNotFoundException extends RuntimeException {

    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public FlashcardNotFoundException() {
        super("Fiszka nie została znaleziona");
    }

    /**
     * Tworzy wyjątek z informacją o ID fiszki.
     *
     * @param flashcardId ID fiszki która nie została znaleziona
     */
    public FlashcardNotFoundException(String flashcardId) {
        super("Fiszka o ID " + flashcardId + " nie została znaleziona");
    }
}

