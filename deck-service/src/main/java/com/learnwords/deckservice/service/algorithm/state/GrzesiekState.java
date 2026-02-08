package com.learnwords.deckservice.service.algorithm.state;

import com.learnwords.deckservice.exception.exceptions.StepWithThisNameNoExist;
import com.learnwords.deckservice.service.algorithm.step.GrzesiekStep;


public final class GrzesiekState extends AbstractState<GrzesiekStep, GrzesiekState> implements AlgorithmState {

    public GrzesiekState(GrzesiekStep step) {
        super(step);
    }

    @Override
    protected GrzesiekState createStateFromStepName(String stepName) throws StepWithThisNameNoExist {
        try{
            GrzesiekStep step = GrzesiekStep.valueOf(stepName);
            return new GrzesiekState(step);
        }
        catch (IllegalArgumentException e) {
            throw new StepWithThisNameNoExist(stepName);
        }
    }
}

