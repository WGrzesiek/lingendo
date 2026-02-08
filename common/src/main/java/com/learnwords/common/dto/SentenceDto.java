package com.learnwords.common.dto;

import lombok.Builder;

/**
 * Reprezentacja przykładowego zdania ze słowem i tłumaczeniem.
 */
@Builder
public record SentenceDto(
        String id,
        String sentence,
        String translation
) {}