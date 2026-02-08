package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy krok o podanej nazwie nie istnieje.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StepWithThisNameNoExist extends RuntimeException {
    
    /**
     * Tworzy wyjątek z domyślnym komunikatem.
     */
    public StepWithThisNameNoExist() {
        super("Krok o podanej nazwie nie istnieje");
    }
    
    /**
     * Tworzy wyjątek z informacją o nazwie kroku.
     *
     * @param step nazwa kroku który nie istnieje
     */
    public StepWithThisNameNoExist(String step) {
        super("Krok o nazwie '" + step + "' nie istnieje");
    }
}
