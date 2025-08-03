package com.learnwords.deckservice.service.Session.FlashcardFetchStrategy;

import com.learnwords.deckservice.entity.Flashcard;

import java.util.List;
import java.util.Set;

public interface FlashcardFetchStrategyService {

    List<Flashcard> sortFlashcardsByStrategy(FlashcardFetchStrategy strategy, Long limit, List<Flashcard> flashcards);
}
