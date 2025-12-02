package com.learnwords.deckservice.dto.userFlashcardProgress;

import com.learnwords.deckservice.enums.LearningPhase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
public record UserFlashcardProgressDto(
        String id,
        String flashcardId,
        String enrollmentId,
        String userId,
        LearningPhase phase,
        boolean isLearned,
        boolean isSkipped,
        int repetitionCount,
        Instant nextReviewAt,
        String algorithmState

) {}
