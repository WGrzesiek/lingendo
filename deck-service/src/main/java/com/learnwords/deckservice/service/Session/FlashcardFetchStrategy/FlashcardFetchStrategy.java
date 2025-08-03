package com.learnwords.deckservice.service.Session.FlashcardFetchStrategy;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FlashcardFetchStrategy {
    ALPHABETICAL,
    RANDOM,
    REVERSE_ALPHABETICAL,
    UNLEARNED_FIRST;

//    @JsonCreator
//    public static FlashcardFetchStrategy fromValue(String value) {
//        return FlashcardFetchStrategy.valueOf(value.toUpperCase());
//    }
}
