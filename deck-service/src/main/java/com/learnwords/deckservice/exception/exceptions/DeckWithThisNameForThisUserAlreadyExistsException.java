package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DeckWithThisNameForThisUserAlreadyExistsException extends RuntimeException {
    public DeckWithThisNameForThisUserAlreadyExistsException(String name) {
        super(name);
    }
}
