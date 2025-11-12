package com.learnwords.deckservice.dto;

import java.util.List;

public record GetWordFromKafkaDto(String id, String word, List<String> translations, List<String> sentenceIds, String deckId) {
}

