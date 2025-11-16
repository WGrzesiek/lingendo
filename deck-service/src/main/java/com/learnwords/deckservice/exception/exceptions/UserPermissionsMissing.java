package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy użytkownik nie ma wymaganych uprawnień do wykonania operacji.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserPermissionsMissing extends RuntimeException {
    
    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public UserPermissionsMissing() {
        super("Brak wymaganych uprawnień do wykonania tej operacji");
    }
    
    /**
     * Tworzy wyjątek z niestandardowym komunikatem.
     *
     * @param message komunikat błędu
     */
    public UserPermissionsMissing(String message) {
        super(message);
    }
}
