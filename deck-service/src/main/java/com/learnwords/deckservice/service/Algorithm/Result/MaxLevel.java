package com.learnwords.deckservice.service.Algorithm.Result;

import com.learnwords.deckservice.service.Algorithm.State.AlgorithmState;

import java.time.Duration;

record MaxLevel<T extends AlgorithmState>(T currentState, Duration waitTime) implements AlgorithmResult<T> {}
