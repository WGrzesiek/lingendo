package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy talia o podanej nazwie już istnieje dla danego użytkownika.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DeckWithThisNameForThisUserAlreadyExistsException extends RuntimeException {
    
    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public DeckWithThisNameForThisUserAlreadyExistsException() {
        super("Talia o tej nazwie już istnieje dla tego użytkownika");
    }
    
    /**
     * Tworzy wyjątek z informacją o nazwie talii.
     *
     * @param name nazwa talii która już istnieje
     */
    public DeckWithThisNameForThisUserAlreadyExistsException(String name) {
        super("Talia o nazwie '" + name + "' już istnieje dla tego użytkownika");
    }
}
