package com.learnwords.deckservice.dto.deck;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.learnwords.common.dto.WordItemDto;
import lombok.Builder;

import java.util.List;

@Builder
public record SentenceGenerationRequestDto(
    @JsonProperty("id")
    String id,
    @JsonProperty("requested_by_user_id")
    String requestedByUserId,
    @JsonProperty("words")
    List<WordItemDto> words,
    @JsonProperty("level")
    String level,
    @JsonProperty("category")
    String category,
    @JsonProperty("language_from")
    String languageFrom,
    @JsonProperty("language_to")
    String languageTo
) {
}

