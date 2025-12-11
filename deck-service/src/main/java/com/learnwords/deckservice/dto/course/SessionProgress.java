package com.learnwords.deckservice.dto.course;

public record SessionProgress(
    int completedSessions,
    int totalSessions,
    int wordsPerSession,
    int totalWords,
    int wordsToReview
) {

}
