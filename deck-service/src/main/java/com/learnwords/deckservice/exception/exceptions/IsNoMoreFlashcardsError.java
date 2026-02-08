package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IsNoMoreFlashcardsError extends RuntimeException {
    public IsNoMoreFlashcardsError() {

        super("Brak dostępnych fiszek do nauki w tej sesji");
    }
}
