package com.learnwords.deckservice.service.algorithm.state;

import com.learnwords.deckservice.exception.exceptions.StepWithThisNameNoExist;
import com.learnwords.deckservice.service.algorithm.step.GrzesiekStep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class GrzesiekStateTest {

    @Test
    public void shouldCreateStateWithStep(){
        GrzesiekState state = new GrzesiekState(GrzesiekStep.QUIZ);
        assertEquals(GrzesiekStep.QUIZ, state.getStep());
    }

    @ParameterizedTest
    @EnumSource(GrzesiekStep.class)
    public void shouldCreateStateFromAllValidStepNames(GrzesiekStep step){
        GrzesiekState state = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        GrzesiekState newState = state.createStateFromStepName(step.name());
        assertEquals(step, newState.getStep());
    }

    @Test
    public void shouldCreateStateFromStepName(){
        GrzesiekState state = new GrzesiekState(GrzesiekStep.QUIZ);
        GrzesiekState newState = state.createStateFromStepName("QUIZ");
        assertEquals(state.getStep(), newState.getStep());
    }

    @Test
    public void shouldThrowExceptionForInvalidStepName() {
        GrzesiekState state = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        assertThrows(StepWithThisNameNoExist.class,
                () -> state.createStateFromStepName("INVALID_STEP"));
    }

    @Test
    public void shouldSerializeState(){
        GrzesiekState state = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        String serialized = state.serialize();
        assertTrue(serialized.contains("{\"step\":\"SHOW_BOTH\"}"));
    }

    @Test
    public void shouldResetToInitialStep(){
        GrzesiekState state = new GrzesiekState(GrzesiekStep.SHOW_LANGUAGE_FROM);
        GrzesiekState resetState = state.reset();
        assertEquals(GrzesiekStep.SHOW_BOTH, resetState.getStep());
    }

    @Test
    public void shouldReturnNextStep(){
        GrzesiekState state = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        GrzesiekState nextState = state.next();
        assertEquals(GrzesiekStep.QUIZ, nextState.getStep());
    }

    @Test
    public void shouldReturnPreviousStep(){
        GrzesiekState state = new GrzesiekState(GrzesiekStep.QUIZ);
        GrzesiekState previousState = state.previous();
        assertEquals(GrzesiekStep.SHOW_BOTH, previousState.getStep());
    }
}
