package com.learnwords.deckservice.service.algorithm.state;

import com.learnwords.deckservice.exception.exceptions.StepWithThisNameNoExist;
import com.learnwords.deckservice.service.algorithm.step.LeitnerStep;

/**
 * Stan algorytmu Leitnera.
 * Przechowuje aktualny krok (pudełko) w którym znajduje się fiszka.
 */
public final class LeitnerState extends AbstractState<LeitnerStep, LeitnerState> implements AlgorithmState {

    public LeitnerState(LeitnerStep step) {
        super(step);
    }

    @Override
    protected LeitnerState createStateFromStepName(String stepName) throws StepWithThisNameNoExist {
        try {
            LeitnerStep step = LeitnerStep.valueOf(stepName);
            return new LeitnerState(step);
        } catch (IllegalArgumentException e) {
            throw new StepWithThisNameNoExist(stepName);
        }
    }
}
