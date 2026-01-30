package com.learnwords.deckservice.service.reviewsStrategy;

import com.learnwords.deckservice.dto.evaluationService.AnswerResultDto;
import com.learnwords.deckservice.dto.learningStrategy.NextFlashcardRecommendation;
import com.learnwords.deckservice.service.evaluationService.TextAnswer;

import java.util.Optional;

public interface ReviewsStrategy {
    Optional<NextFlashcardRecommendation> recommendNext(String userId, String enrolmentId);
    AnswerResultDto registerReviewResult(String flashcardId, TextAnswer answer, String userId);
}
