package com.learnwords.deckservice.dto.facade.review;

public record ReviewCounters(
        long totalWordsToReview,
        long wordsForToday,
        long overdueWords
) {}

