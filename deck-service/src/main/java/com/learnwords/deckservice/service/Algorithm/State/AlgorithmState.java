package com.learnwords.deckservice.service.Algorithm.State;


import com.learnwords.deckservice.service.Algorithm.Step.Step;

public sealed interface AlgorithmState permits AbstractState, GrzesiekState {
    String serialize();
    AlgorithmState next();
    AlgorithmState previous();
    AlgorithmState reset();
    Step getStep();
}
