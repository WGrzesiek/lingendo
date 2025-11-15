package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy sesja o podanym ID nie została znaleziona.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SessionNotFoundException extends RuntimeException {

    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public SessionNotFoundException() {
        super("Sesja nie została znaleziona");
    }

    /**
     * Tworzy wyjątek z informacją o ID sesji.
     *
     * @param sessionId ID sesji która nie została znaleziona
     */
    public SessionNotFoundException(String sessionId) {
        super("Sesja o ID '" + sessionId + "' nie została znaleziona");
    }
}
