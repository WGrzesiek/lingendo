package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.UserFlashcardProgress;
import com.learnwords.deckservice.enums.LearningPhase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserFlashcardProgressRepository extends JpaRepository<com.learnwords.deckservice.entity.UserFlashcardProgress, String> {
    List<UserFlashcardProgress> findByEnrollment_Id(String enrollmentId);
    Optional<UserFlashcardProgress> findByFlashcard_IdAndEnrollment_Id(String flashcardId, String enrollmentId);
    Optional<UserFlashcardProgress> findByFlashcard_IdAndUserId(String flashcardId, String userId);
    Page<UserFlashcardProgress> findByEnrollment_Id(String enrollmentId, Pageable pageable);

    int countByEnrollment_IdAndUserIdAndIsLearnedAndPhaseAndNextReviewAtAfterAndRepetitionCountIsLessThan(String enrollmentId,String userId ,boolean isLearned, LearningPhase phase, Instant nextReviewAtAfter, int repetitionCountIsLessThan);

    List<UserFlashcardProgress> findByEnrollment_IdAndUserIdAndIsLearnedAndPhaseAndNextReviewAtAfterAndRepetitionCountIsLessThan(String enrollmentId,String userId ,boolean isLearned, LearningPhase phase, Instant nextReviewAtAfter, int repetitionCountIsLessThan);

    @Query("""
        SELECT u.nextReviewAt
        FROM UserFlashcardProgress u
        WHERE u.enrollment.id = :enrollmentId
          AND u.userId = :userId
          AND u.repetitionCount < :maxRep
          AND u.nextReviewAt > :now
        ORDER BY u.nextReviewAt ASC
        LIMIT 1
""")
    Optional<Instant> findNextReviewAt(
            String enrollmentId,
            String userId,
            Instant now,
            int maxRep
    );
}
