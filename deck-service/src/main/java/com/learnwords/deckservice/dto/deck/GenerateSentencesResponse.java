package com.learnwords.deckservice.dto.deck;

import lombok.Builder;

@Builder
public record GenerateSentencesResponse(
        String correlationId,
        String message,
        Integer wordsCount
) {
}
