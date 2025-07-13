package com.learnwords.deckservice.exception;

import com.learnwords.deckservice.dto.ApiErrorResponse;
import com.learnwords.deckservice.exception.exceptions.DeckWithThisNameForThisUserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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
}
