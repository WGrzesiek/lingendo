package com.learnwords.deckservice.service.evaluationService.responseResult;

import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;

public record Failure<T extends AlgorithmState>(T currentState, String reason) implements AlgorithmResult<T> {

    public Failure(T currentState) {
        this(currentState, "Failure");
    }
    public Failure(T currentState, String reason) {
        this.currentState = currentState;
        this.reason = reason;
    }
}

