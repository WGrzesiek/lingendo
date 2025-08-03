package com.learnwords.deckservice.service.Algorithm.responseResult;

import com.learnwords.deckservice.service.Algorithm.State.AlgorithmState;

record Failure<T extends AlgorithmState>(T currentState, String reason) implements AlgorithmResult<T> {}

