package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DeckNotFoundException extends RuntimeException {
    public DeckNotFoundException(String deckId) {
        super("Talia o ID " + deckId + " nie została znaleziona");
    }
    
    public DeckNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
