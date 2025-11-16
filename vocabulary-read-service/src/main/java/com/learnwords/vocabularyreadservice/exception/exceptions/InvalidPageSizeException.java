package com.learnwords.vocabularyreadservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy parametr page_size jest nieprawidłowy.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPageSizeException extends RuntimeException {
    
    public InvalidPageSizeException() {
        super("Rozmiar strony musi być większy od 0");
    }
    
    public InvalidPageSizeException(String message) {
        super(message);
    }
}
