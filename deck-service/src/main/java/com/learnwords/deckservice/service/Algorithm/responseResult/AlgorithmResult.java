package com.learnwords.deckservice.service.Algorithm.responseResult;

import com.learnwords.deckservice.service.Algorithm.State.AlgorithmState;

public sealed interface AlgorithmResult<T extends AlgorithmState>
        permits Success, Failure, MaxLevel {
}