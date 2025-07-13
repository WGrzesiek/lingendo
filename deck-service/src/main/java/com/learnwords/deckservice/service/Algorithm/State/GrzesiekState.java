package com.learnwords.deckservice.service.Algorithm.State;

import com.learnwords.deckservice.service.Algorithm.Step.GrzesiekStep;

public final class GrzesiekState extends AbstractState<GrzesiekStep, GrzesiekState> implements AlgorithmState {

    public GrzesiekState(GrzesiekStep step) {
        super(step);
    }

    @Override
    protected GrzesiekState createStateFromStepName(String stepName) {
        GrzesiekStep step = GrzesiekStep.valueOf(stepName);
        return new GrzesiekState(step);
    }
}

