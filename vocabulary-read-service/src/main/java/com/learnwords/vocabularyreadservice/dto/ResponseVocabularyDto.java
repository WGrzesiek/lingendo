package com.learnwords.vocabularyreadservice.dto;

import java.util.List;

public record ResponseVocabularyDto(String word, List<String> translation, List<String> sentenceIds) {
}
