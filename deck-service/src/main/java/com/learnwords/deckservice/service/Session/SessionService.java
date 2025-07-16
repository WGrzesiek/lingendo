package com.learnwords.deckservice.service.Session;

import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;

public interface SessionService {
    String initializeSession(String deckId, FlashcardFetchStrategy flashcardFetchStrategy);
}
