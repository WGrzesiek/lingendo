package com.learnwords.vocabularyreadservice.exception;


import com.learnwords.vocabularyreadservice.exception.exceptions.InvalidPageSizeException;
import com.learnwords.vocabularyreadservice.exception.exceptions.InvalidSentenceIdException;
import com.learnwords.vocabularyreadservice.exception.exceptions.InvalidVocabularyIdException;
import com.learnwords.vocabularyreadservice.exception.exceptions.SentenceNotFoundException;
import com.learnwords.vocabularyreadservice.exception.exceptions.VocabularyNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SentenceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> handleSentenceNotFoundException(SentenceNotFoundException ex) {
        log.error("Zdanie nie znalezione: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(VocabularyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> handleVocabularyNotFoundException(VocabularyNotFoundException ex){
        log.error("Słownictwo nie znalezione: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidVocabularyIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleInvalidVocabularyIdException(InvalidVocabularyIdException ex){
        log.error("Nieprawidłowe ID słownictwa: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidSentenceIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleInvalidSentenceIdException(InvalidSentenceIdException ex){
        log.error("Nieprawidłowe ID zdania: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidPageSizeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleInvalidPageSizeException(InvalidPageSizeException ex){
        log.error("Nieprawidłowy rozmiar strony: {}", ex.getMessage());
        ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
