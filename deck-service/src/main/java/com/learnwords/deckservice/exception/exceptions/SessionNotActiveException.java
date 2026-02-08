package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy operacja wymaga aktywnej sesji, ale sesja nie jest w statusie IN_PROGRESS.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SessionNotActiveException extends RuntimeException {

    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public SessionNotActiveException() {
        super("Sesja nie jest aktywna");
    }

    /**
     * Tworzy wyjątek z informacją o ID sesji i jej statusie.
     *
     * @param sessionId ID sesji
     * @param currentStatus aktualny status sesji
     */
    public SessionNotActiveException(String sessionId, String currentStatus) {
        super("Sesja o ID '" + sessionId + "' nie jest aktywna. Aktualny status: " + currentStatus);
    }
}
