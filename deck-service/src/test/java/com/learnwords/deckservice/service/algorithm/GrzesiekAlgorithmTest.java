package com.learnwords.deckservice.service.algorithm;

import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.exception.exceptions.StepWithThisNameNoExist;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.algorithm.state.GrzesiekState;
import com.learnwords.deckservice.service.algorithm.step.GrzesiekStep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class GrzesiekAlgorithmTest {

    @Test
    public void shouldReturnCorrectType(){
        GrzesiekAlgorithm algorithm = new GrzesiekAlgorithm();
        assertEquals(LearnAlgorithm.GRZESIEK_ALGORITHM, algorithm.getType());
    }

    @Test
    public void shouldReturnCorrectInitialState(){
        GrzesiekAlgorithm algorithm = new GrzesiekAlgorithm();
        AlgorithmState initialState = algorithm.getInitialState();
        assertTrue(initialState instanceof GrzesiekState);
        assertEquals(GrzesiekStep.SHOW_BOTH, initialState.getStep());
    }

    @Test
    public void shouldDeserializeValidJson(){
        GrzesiekAlgorithm algorithm = new GrzesiekAlgorithm();
        String json = "{\"step\":\"QUIZ\"}";
        GrzesiekState state = algorithm.deserialize(json);
        assertEquals(GrzesiekStep.QUIZ, state.getStep());
    }

    @Test
    public void shouldThrowExceptionForInvalidStepInJson() {
        GrzesiekAlgorithm algorithm = new GrzesiekAlgorithm();
        String invalidJson = "{\"step\":\"INVALID_STEP\"}";

        assertThrows(StepWithThisNameNoExist.class,
                () -> algorithm.deserialize(invalidJson));
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", " ", "NULL", ""})
    public void shouldThrowExceptionForInvalidJson(String json) {
        GrzesiekAlgorithm algorithm = new GrzesiekAlgorithm();
        assertThrows(StepWithThisNameNoExist.class,
                () -> algorithm.deserialize(json));
    }

}
