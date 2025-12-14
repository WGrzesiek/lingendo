package com.learnwords.deckservice.dto.userFlashcardProgress;

import com.learnwords.deckservice.enums.LearningPhase;
import lombok.Builder;


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
        String algorithmState,
        Instant lastShownAt

) {}
