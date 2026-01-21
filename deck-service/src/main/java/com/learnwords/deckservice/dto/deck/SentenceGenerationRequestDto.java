package com.learnwords.deckservice.dto.deck;

import com.learnwords.common.dto.WordItemDto;
import lombok.Builder;

import java.util.List;

@Builder
public record SentenceGenerationRequestDto(
    String id,
    String requestedByUserId,
    List<WordItemDto> words,
    String level,
    String category,
    String languageFrom,
    String languageTo

) {
}
