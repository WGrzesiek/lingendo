package com.learnwords.deckservice.dto.facade.course;

import com.learnwords.deckservice.dto.facade.learn.SessionInfo;

import java.time.Instant;
import java.util.List;

public record SessionProgress(
    int completedSessions,
    int totalSessions,
    int wordsPerSession,
    int totalWords,
    int wordsToReview,
    Instant nextReviewDate,
    List<SessionInfo> sessions
) {

}
