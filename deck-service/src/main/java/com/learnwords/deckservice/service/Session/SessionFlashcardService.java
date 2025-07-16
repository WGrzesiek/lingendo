package com.learnwords.deckservice.service.Session;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;

import java.util.Set;

public interface SessionFlashcardService {
    String addFlashcardsToSession(Session session, Deck deck, FlashcardFetchStrategy flashcardFetchStrategy);
//    void removeFlashcardFromSession(String sessionId, String flashcardId);
}
