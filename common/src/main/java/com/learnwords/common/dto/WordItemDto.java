package com.learnwords.common.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record WordItemDto(
    String wordId,
    String word,
    List<String > translations
) {
}
