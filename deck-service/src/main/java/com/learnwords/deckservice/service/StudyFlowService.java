package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.evaluationService.AnswerResultDto;
import com.learnwords.deckservice.dto.learningStrategy.NextFlashcardRecommendation;
import com.learnwords.deckservice.service.evaluationService.UserAnswer;

public interface StudyFlowService {
    NextFlashcardRecommendation getNextFlashcard(String sessionId, String userId);
    AnswerResultDto submitAnswer(String sessionId, String flashcardId, UserAnswer userAnswer, String userId);
}
