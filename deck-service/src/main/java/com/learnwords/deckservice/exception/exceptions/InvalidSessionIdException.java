package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy ID sesji jest null lub puste.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSessionIdException extends RuntimeException {

    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public InvalidSessionIdException() {
        super("ID sesji nie może być puste");
    }

    /**
     * Tworzy wyjątek z niestandardowym komunikatem.
     *
     * @param message komunikat błędu
     */
    public InvalidSessionIdException(String message) {
        super(message);
    }
}
