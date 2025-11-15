package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy dane słówka są nieprawidłowe (null lub brakujące ID).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidWordDataException extends RuntimeException {

    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public InvalidWordDataException() {
        super("Dane słówka nie mogą być puste");
    }

    /**
     * Tworzy wyjątek z niestandardowym komunikatem.
     *
     * @param message komunikat błędu
     */
    public InvalidWordDataException(String message) {
        super(message);
    }
}

