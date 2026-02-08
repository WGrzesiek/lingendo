package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.dto.facade.review.ReviewCounters;
import com.learnwords.deckservice.entity.UserFlashcardProgress;
import com.learnwords.deckservice.enums.LearningPhase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserFlashcardProgressRepository extends JpaRepository<com.learnwords.deckservice.entity.UserFlashcardProgress, String> {
    List<UserFlashcardProgress> findByEnrollment_Id(String enrollmentId);
    Optional<UserFlashcardProgress> findByFlashcard_IdAndEnrollment_Id(String flashcardId, String enrollmentId);
    Optional<UserFlashcardProgress> findByFlashcard_IdAndUserId(String flashcardId, String userId);
    Page<UserFlashcardProgress> findByEnrollment_Id(String enrollmentId, Pageable pageable);

    Page<UserFlashcardProgress> findByEnrollment_IdAndPhase(String enrollmentId, LearningPhase phase, Pageable pageable);

    @Query("""
        SELECT COUNT(u)
        FROM UserFlashcardProgress u
        WHERE u.enrollment.id = :enrollmentId
          AND u.userId = :userId
          AND u.isLearned = true
          AND u.phase = :phase
          AND u.repetitionCount <= :maxRepetitionCount
    """)
    int countToReview(String enrollmentId, String userId, LearningPhase phase, int maxRepetitionCount);

    @Query("""
  SELECT new com.learnwords.deckservice.dto.facade.review.ReviewCounters(
      COUNT(u),
      COALESCE(SUM(CASE WHEN u.nextReviewAt < :startOfTomorrow THEN 1L ELSE 0L END), 0L),
      COALESCE(SUM(CASE WHEN u.nextReviewAt < :now THEN 1L ELSE 0L END), 0L)
  )
  FROM UserFlashcardProgress u
  WHERE u.enrollment.id = :enrollmentId
    AND u.userId = :userId
    AND u.isLearned = true
    AND u.phase = :phase
""")
    ReviewCounters countReviewStats(
            @Param("enrollmentId") String enrollmentId,
            @Param("userId") String userId,
            @Param("phase") LearningPhase phase,
            @Param("now") Instant now,
            @Param("startOfTomorrow") Instant startOfTomorrow
    );


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
