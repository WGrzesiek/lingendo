package com.learnwords.deckservice.service.Algorithm.State;

// public sealed interface AlgorithmState permits AbstractState, LeitnerState, GrzesiekState {
public sealed interface AlgorithmState permits AbstractState, GrzesiekState {

    String serialize();
    AlgorithmState deserialize(String serializedState);
    AlgorithmState next();
    AlgorithmState previous();
    AlgorithmState reset();
}
