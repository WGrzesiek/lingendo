package com.learnwords.deckservice.service.Algorithm.State;

import com.learnwords.deckservice.exception.exceptions.StepWithThisNameNoExist;
import com.learnwords.deckservice.service.Algorithm.Step.GrzesiekStep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractStateTest {

    @Test
    public void shouldSerialize(){
        GrzesiekState testState = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        String serialized = testState.serialize();
        assertTrue(serialized.contains("SHOW_BOTH"));
    }

    @Test
    public void shouldReset(){
        GrzesiekState testState = new GrzesiekState(GrzesiekStep.QUIZ);
        GrzesiekState resetState = testState.reset();
        assertEquals(GrzesiekStep.SHOW_BOTH, resetState.getStep());
    }

    @Test
    public void shouldReturnNextStep(){
        GrzesiekState testState = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        GrzesiekState nextState = testState.next();
        assertEquals(GrzesiekStep.QUIZ, nextState.getStep());
    }

    @Test
    public void shouldStayAtMaxStepWhenNextCalled(){
        GrzesiekState testState = new GrzesiekState(GrzesiekStep.WRITE_LANGUAGE_TO);
        GrzesiekState nextState = testState.next();
        assertEquals(GrzesiekStep.WRITE_LANGUAGE_TO, nextState.getStep());
    }

    @Test
    public void shouldReturnPreviousStep(){
        GrzesiekState testState = new GrzesiekState(GrzesiekStep.QUIZ);
        GrzesiekState previousState = testState.previous();
        assertEquals(GrzesiekStep.SHOW_BOTH, previousState.getStep());
    }

    @Test
    public void shouldStayAtInitStepWhenPreviousCalled(){
        GrzesiekState testState = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        GrzesiekState previousState = testState.previous();
        assertEquals(GrzesiekStep.SHOW_BOTH, previousState.getStep());
    }

    @Test
    public void shouldReturnCurrentStep(){
        GrzesiekStep expectedStep = GrzesiekStep.QUIZ;
        GrzesiekState state = new GrzesiekState(expectedStep);
        assertEquals(expectedStep, state.getStep());
    }

    @Test
    public void shouldCreateStateFromStepName(){
        GrzesiekState testState = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        GrzesiekState newState = testState.createStateFromStepName("QUIZ");
        assertEquals(GrzesiekStep.QUIZ, newState.getStep());
    }

    @Test
    public void shouldThrowStepWithThisNameNoExistForInvalidStepName(){
        GrzesiekState testState = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        assertThrows(StepWithThisNameNoExist.class,
                () -> testState.createStateFromStepName("INVALID"));
    }

    @ParameterizedTest
    @EnumSource(GrzesiekStep.class)
    public void shouldCreateStateFromValidStepNames(GrzesiekStep step){
        GrzesiekState testState = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        GrzesiekState newState = testState.createStateFromStepName(step.name());
        assertEquals(step, newState.getStep());
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "WRONG_STEP", "NULL", ""})
    public void shouldThrowExceptionForInvalidStepNames(String invalidStepName){
        GrzesiekState testState = new GrzesiekState(GrzesiekStep.SHOW_BOTH);
        assertThrows(StepWithThisNameNoExist.class,
                () -> testState.createStateFromStepName(invalidStepName));
    }

    @Test
    public void shouldTraverseAllStepsInCorrectOrder(){
        GrzesiekState state = new GrzesiekState(GrzesiekStep.SHOW_BOTH);

        assertEquals(GrzesiekStep.SHOW_BOTH, state.getStep());

        state = state.next();
        assertEquals(GrzesiekStep.QUIZ, state.getStep());

        state = state.next();
        assertEquals(GrzesiekStep.SHOW_LANGUAGE_FROM, state.getStep());

        state = state.next();
        assertEquals(GrzesiekStep.SHOW_LANGUAGE_TO, state.getStep());

        state = state.next();
        assertEquals(GrzesiekStep.WRITE_LANGUAGE_FROM, state.getStep());

        state = state.next();
        assertEquals(GrzesiekStep.WRITE_LANGUAGE_TO, state.getStep());

        state = state.next();
        assertEquals(GrzesiekStep.WRITE_LANGUAGE_TO, state.getStep());
    }
}