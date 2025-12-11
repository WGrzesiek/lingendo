package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.UserFlashcardProgress;
import com.learnwords.deckservice.enums.LearningPhase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFlashcardProgressRepository extends JpaRepository<com.learnwords.deckservice.entity.UserFlashcardProgress, String> {
    List<UserFlashcardProgress> findByEnrollment_Id(String enrollmentId);
    Optional<UserFlashcardProgress> findByFlashcard_IdAndEnrollment_Id(String flashcardId, String enrollmentId);
    Optional<UserFlashcardProgress> findByFlashcard_IdAndUserId(String flashcardId, String userId);
    Page<UserFlashcardProgress> findByEnrollment_Id(String enrollmentId, Pageable pageable);

    int countByEnrollment_IdAndUserIdAndPhase(String enrollmentId, String userId, LearningPhase phase);

}
