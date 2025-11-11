package com.learnwords.common.dto;

/**
 * Reprezentacja przykładowego zdania ze słowem i tłumaczeniem.
 */
public record SentenceDto(
        String id,
        String sentence,
        String translation
) {}