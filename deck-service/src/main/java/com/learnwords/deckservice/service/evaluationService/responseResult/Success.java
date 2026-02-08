package com.learnwords.deckservice.service.evaluationService.responseResult;

import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;

public record Success<T extends AlgorithmState>(T newState, String message) implements AlgorithmResult<T> {
    public Success(T newState) {
        this(newState, "Success");
    }
}

