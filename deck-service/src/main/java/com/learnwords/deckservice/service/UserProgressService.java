package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.facade.review.ReviewCounters;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.enums.LearningPhase;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.evaluationService.responseResult.AlgorithmResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface UserProgressService {
    void resetFlashcardProgress(String flashcardId, String userId);
    void markAsLearned(String flashcardId, boolean learned, String userId);
    void markAsSkipped(String flashcardId, boolean skipped, String userId);
    List<UserFlashcardProgressDto> getProgressForDeck(String deckId, String userId);
    List<UserFlashcardProgressDto> getDueFlashcards(String enrollmentId, int limit);
    void setInitialFlashcardState(String deckId, Flashcard flashcard, String userId);
    void initializeSessionFlashcardsState(String deckId, List<String> flashcardIds, String userId);
    void initializeDeckFlashcardsState(String deckId, String userId);
    UserFlashcardProgressDto getFlashcardProgress(String flashcardId, String userId);
    void updateProgress(UserFlashcardProgressDto progressDto, AlgorithmResult result, boolean isCorrect);
    Page<UserFlashcardProgressDto> getProgressForEnrollment(String enrollmentId, String userId, Pageable pageable);
    int countWordsToReview(String enrollmentId, String userId);
    ReviewCounters getReviewCounters(String enrollmentId, String userId);
    void updateProgressAfterReview(UserFlashcardProgressDto progressDto, boolean isCorrect);
    Instant getNextNearReviewDate(String enrollmentId, String userId);
    void updateLastShownAt(String id);

}