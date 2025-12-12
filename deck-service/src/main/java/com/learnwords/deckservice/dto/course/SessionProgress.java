package com.learnwords.deckservice.dto.course;

import java.time.Instant;

public record SessionProgress(
    int completedSessions,
    int totalSessions,
    int wordsPerSession,
    int totalWords,
    int wordsToReview,
    Instant nextReviewDate
) {

}
