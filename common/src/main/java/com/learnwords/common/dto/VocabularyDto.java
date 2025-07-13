package com.learnwords.common.dto;

import java.util.List;

public record VocabularyDto(String id, String word, List<String> translations, List<String> sentenceIds, String deckId) {
    public VocabularyDto(String id, String word, List<String> translations, List<String> sentenceIds) {
        this(id, word, translations, sentenceIds, null);
    }
}
