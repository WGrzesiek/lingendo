package com.learnwords.deckservice.exception.exceptions;

public class UnauthorizedDeckAccessException extends RuntimeException {

    public UnauthorizedDeckAccessException(String message) {
        super(message);
    }

}
