package com.learnwords.deckservice.service.evaluationService.responseResult;

import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;

public record MaxLevel<T extends AlgorithmState>(T currentState, String message) implements AlgorithmResult<T> {
    public MaxLevel(T currentState) {
        this(currentState, "Maximum level reached");
    }
}
