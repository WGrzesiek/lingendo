package com.learnwords.deckservice.exception.exceptions;

public class UserPermissionsMissing extends RuntimeException {
    public UserPermissionsMissing(String message) {
        super(message);
    }
}
