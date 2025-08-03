package com.learnwords.deckservice.service.Algorithm;

import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.service.Algorithm.State.AlgorithmState;
import com.learnwords.deckservice.service.Algorithm.Step.GrzesiekStep;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractAlgorithmTest {

    @Test
    public void shouldInitialize(){
        AbstractAlgorithm algorithm = new GrzesiekAlgorithm();
        AlgorithmState state = algorithm.initialize();
        assertEquals(GrzesiekStep.SHOW_BOTH, state.getStep());
    }

    @Test
    public void shouldPromote(){
        AbstractAlgorithm algorithm = new GrzesiekAlgorithm();
        AlgorithmState state = algorithm.initialize();
        AlgorithmState nextState = algorithm.promote(state);
        assertEquals(GrzesiekStep.QUIZ, nextState.getStep());
    }

    @Test
    public void shouldDemote(){
        AbstractAlgorithm algorithm = new GrzesiekAlgorithm();
        AlgorithmState state = algorithm.initialize();
        AlgorithmState nextState = algorithm.promote(state);
        AlgorithmState previousState = algorithm.demote(nextState);
        assertEquals(GrzesiekStep.SHOW_BOTH, previousState.getStep());
    }

    @Test
    public void shouldReset(){
        AbstractAlgorithm algorithm = new GrzesiekAlgorithm();
        AlgorithmState state = algorithm.initialize();
        AlgorithmState advancedState = algorithm.promote(algorithm.promote(state)); // SHOW_LANGUAGE_FROM
        AlgorithmState resetState = algorithm.reset(advancedState);
        assertEquals(GrzesiekStep.SHOW_BOTH, resetState.getStep());
    }

    @Test
    public void shouldGetCurrentState() {
        AbstractAlgorithm algorithm = new GrzesiekAlgorithm();
        AlgorithmState state = algorithm.initialize();
        AlgorithmState currentState = algorithm.getCurrentState(state);
        assertEquals(state, currentState);
        assertEquals(GrzesiekStep.SHOW_BOTH, currentState.getStep());
    }

    @Test
    public void shouldReturnCorrectAlgorithmType() {
        AbstractAlgorithm algorithm = new GrzesiekAlgorithm();
        assertEquals(LearnAlgorithm.GRZESIEK_ALGORITHM, algorithm.getType());
    }

    @Test
    public void shouldTraverseFullAlgorithmFlow() {
        AbstractAlgorithm algorithm = new GrzesiekAlgorithm();

        AlgorithmState state = algorithm.initialize();
        assertEquals(GrzesiekStep.SHOW_BOTH, state.getStep());

        state = algorithm.promote(state);
        assertEquals(GrzesiekStep.QUIZ, state.getStep());

        state = algorithm.promote(state);
        assertEquals(GrzesiekStep.SHOW_LANGUAGE_FROM, state.getStep());

        state = algorithm.promote(state);
        assertEquals(GrzesiekStep.SHOW_LANGUAGE_TO, state.getStep());

        AlgorithmState resetState = algorithm.reset(state);
        assertEquals(GrzesiekStep.SHOW_BOTH, resetState.getStep());
    }
}


