package com.learnwords.common.dto;

import java.util.List;

public record SendWordFromKafkaDto(String id, String word, List<String> translations, List<String> sentenceIds, String deckId) {

    public SendWordFromKafkaDto(String id, String word, List<String> translations, List<String> sentenceIds) {
        this(id, word, translations, sentenceIds, null);
    }
}

