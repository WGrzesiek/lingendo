package com.learnwords.vocabularycommandservice.dto;

import java.util.List;

public record SendWordDto(String id, String word, List<String> translations, List<String> sentenceIds, String deckId) {

    public SendWordDto(String id, String word, List<String> translations, List<String> sentenceIds) {
        this(id, word, translations, sentenceIds, null);
    }
}

