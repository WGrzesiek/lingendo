package com.learnwords.deckservice.service.evaluationService.responseResult;

import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;

public sealed interface AlgorithmResult<T extends AlgorithmState>
        permits Success, Failure, MaxLevel {
}