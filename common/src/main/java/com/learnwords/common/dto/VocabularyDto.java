package com.learnwords.common.dto;

import java.util.List;

public record VocabularyDto(String id, String word, List<String> translations, List<String> sentenceIds) {
}
