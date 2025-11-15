package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy ID fiszki jest null lub pusty.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFlashcardIdException extends RuntimeException {

    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public InvalidFlashcardIdException() {
        super("ID fiszki nie może być puste");
    }

    /**
     * Tworzy wyjątek z niestandardowym komunikatem.
     *
     * @param message komunikat błędu
     */
    public InvalidFlashcardIdException(String message) {
        super(message);
    }
}
