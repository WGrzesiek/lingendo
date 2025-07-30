package com.learnwords.deckservice.exception.exceptions;

public class StepWithThisNameNoExist extends RuntimeException {
    public StepWithThisNameNoExist(String step) {
        super("Step with name '" + step + "' does not exist.");

    }
}
