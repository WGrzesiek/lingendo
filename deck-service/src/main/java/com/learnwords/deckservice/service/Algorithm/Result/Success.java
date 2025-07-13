package com.learnwords.deckservice.service.Algorithm.Result;

import com.learnwords.deckservice.service.Algorithm.State.AlgorithmState;

record Success<T extends AlgorithmState>(T newState, String message) implements AlgorithmResult<T> {}

