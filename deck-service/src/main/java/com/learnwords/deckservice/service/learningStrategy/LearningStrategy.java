package com.learnwords.deckservice.service.learningStrategy;

import com.learnwords.deckservice.dto.learningStrategy.NextFlashcardRecommendation;
import com.learnwords.deckservice.entity.SessionFlashcard;
import com.learnwords.deckservice.enums.LearnAlgorithm;

import java.util.List;
import java.util.Optional;

public sealed interface LearningStrategy permits AbstractStrategyRecommender, GrzesiekStrategyImpl, LeitnerStrategyImpl {
    Optional<NextFlashcardRecommendation> recommendNext(List<SessionFlashcard> sessionFlashcards, String userId);
    boolean supports(LearnAlgorithm type);
}

