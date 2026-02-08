package com.learnwords.userservice.exception;

public record ApiErrorResponse(int status, String message) {
}
