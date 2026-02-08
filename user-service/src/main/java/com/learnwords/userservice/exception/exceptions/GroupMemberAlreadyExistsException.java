package com.learnwords.userservice.exception.exceptions;

public class GroupMemberAlreadyExistsException extends RuntimeException {
    public GroupMemberAlreadyExistsException(String message) {
        super(message);
    }
}
