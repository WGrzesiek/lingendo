package com.learnwords.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record WordItemDto(
    @JsonProperty("word_id")
    String wordId,
    @JsonProperty("word")
    String word,
    @JsonProperty("translations")
    List<String> translations
) {
}

