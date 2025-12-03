package com.learnwords.deckservice.service.algorithm.state;


import com.learnwords.deckservice.service.algorithm.step.Step;

public sealed interface AlgorithmState permits AbstractState, GrzesiekState {
    String serialize();
    AlgorithmState next();
    AlgorithmState previous();
    AlgorithmState reset();
    Step getStep();
}
