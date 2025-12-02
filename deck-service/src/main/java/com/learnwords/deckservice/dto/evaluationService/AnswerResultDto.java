package com.learnwords.deckservice.dto.evaluationService;

import com.learnwords.deckservice.service.evaluationService.responseResult.AlgorithmResult;

public record AnswerResultDto(Boolean isCorrect, AlgorithmResult result) {
}
