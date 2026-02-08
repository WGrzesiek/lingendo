package com.learnwords.common.dto;

import lombok.Builder;

import java.util.List;
/**
 * Pełna reprezentacja słowa ze wszystkimi szczegółami.
 * Używana przez deck-service do wyświetlania fiszek w sesji nauki.
 */
@Builder
public record WordDto(
        String id,
        String word,
        List<String> translations,
        List<SentenceDto> sentences,
        List<SentenceDto> sentencesAI
) {

    public WordDto(String id, String word, List<String> translations) {
        this(id, word, translations, List.of(), List.of());
    }

    public WordDto(String id, String word, List<String> translations, List<SentenceDto> sentences) {
        this(id, word, translations, sentences, List.of());
    }
}