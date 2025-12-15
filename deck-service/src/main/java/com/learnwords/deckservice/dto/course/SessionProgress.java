package com.learnwords.deckservice.dto.course;

import org.apache.kafka.common.protocol.types.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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
