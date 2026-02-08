package com.learnwords.userservice.exception.exceptions;

public class GroupMemberNotFoundException extends RuntimeException {
    public GroupMemberNotFoundException(String message) {
        super(message);
    }
}
