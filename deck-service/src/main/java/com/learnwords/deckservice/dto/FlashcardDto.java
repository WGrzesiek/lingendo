package com.learnwords.deckservice.dto;

import com.learnwords.common.dto.WordDto;

import java.time.Instant;

public record FlashcardDto(
    String id,
    WordDto wordDto,
    int correctAnswers,
    int totalAttempts,
    boolean isLearned,
    boolean isSkipped,
    Instant createdAt,
    Instant updatedAt
) {}
