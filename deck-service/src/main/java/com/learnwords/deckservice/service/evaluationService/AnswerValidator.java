package com.learnwords.deckservice.service.evaluationService;

import com.learnwords.deckservice.dto.flashcard.FlashcardDto;
import com.learnwords.deckservice.service.algorithm.step.Step;

public interface AnswerValidator {
    boolean validate(FlashcardDto flashcard, UserAnswer userAnswer, Step step);
    boolean validateReview(FlashcardDto flashcard, TextAnswer answer);
}
