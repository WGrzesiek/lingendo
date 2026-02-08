package com.learnwords.deckservice.exception;

import com.learnwords.deckservice.dto.ApiErrorResponse;
import com.learnwords.deckservice.exception.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeckWithThisNameForThisUserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleDeckWithThisNameForThisUserAlreadyExistsException(DeckWithThisNameForThisUserAlreadyExistsException ex) {
        log.error("Deck with this name for this user already exists: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(StepWithThisNameNoExist.class)
    public ResponseEntity<ApiErrorResponse> handleStepWithThisNameNoExist(StepWithThisNameNoExist ex) {
        log.error("Step with this name does not exist: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DeckNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDeckNotFoundException(DeckNotFoundException ex) {
        log.error("Deck not found: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(FlashcardNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleFlashcardNotFoundException(FlashcardNotFoundException ex) {
        log.error("Flashcard not found: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidFlashcardIdException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidFlashcardIdException(InvalidFlashcardIdException ex) {
        log.error("Invalid flashcard ID: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidWordDataException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidWordDataException(InvalidWordDataException ex) {
        log.error("Invalid word data: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Błędy walidacji");
        response.put("errors", errors);

        log.error("Błędy walidacji: {}", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Nieprawidłowa wartość parametru '%s': '%s'", ex.getName(), ex.getValue());
        log.error("Type mismatch error: {}", message);
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Wystąpił nieoczekiwany błąd. Spróbuj ponownie później."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(UserPermissionsMissing.class)
    public ResponseEntity<ApiErrorResponse> handleUserPermissionsMissing(UserPermissionsMissing ex) {
        log.error("User permissions missing: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(NoFlashcardsAvailableException.class)
    public ResponseEntity<ApiErrorResponse> handleNoFlashcardsAvailableException(NoFlashcardsAvailableException ex) {
        log.error("No flashcards available: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSessionNotFoundException(SessionNotFoundException ex) {
        log.error("Session not found: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SessionNotActiveException.class)
    public ResponseEntity<ApiErrorResponse> handleSessionNotActiveException(SessionNotActiveException ex) {
        log.error("Session not active: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InvalidSessionIdException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidSessionIdException(InvalidSessionIdException ex) {
        log.error("Invalid session ID: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(SessionFinishedException.class)
    public ResponseEntity<ApiErrorResponse> handleSessionFinishedException(SessionFinishedException ex) {
        log.error("Session finished: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IsNoMoreFlashcardsError.class)
    public ResponseEntity<ApiErrorResponse> handleIsNoMoreFlashcardsError(IsNoMoreFlashcardsError ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DeckShareNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDeckShareNotFoundException(DeckShareNotFoundException ex) {
        log.error("Deck share not found: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedDeckAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedDeckAccessException(UnauthorizedDeckAccessException ex) {
        log.error("Unauthorized deck access: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}
