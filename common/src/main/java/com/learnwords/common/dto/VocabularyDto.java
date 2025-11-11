package com.learnwords.common.dto;

import java.util.List;

/**
 * @deprecated Użyj {@link WordDto} zamiast tego.
 * Zostało dla kompatybilności wstecznej.
 */
@Deprecated(since = "2025-11-11", forRemoval = true)
public record VocabularyDto(String id, String word, List<String> translations, List<String> sentenceIds, String deckId) {
    public VocabularyDto(String id, String word, List<String> translations, List<String> sentenceIds) {
        this(id, word, translations, sentenceIds, null);
    }
}
