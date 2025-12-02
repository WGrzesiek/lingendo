package com.learnwords.deckservice.service.algorithm.step;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class GrzesiekStepTest {

    @Test
    void shouldReturnCorrectNextStep() {
        assertAll(
                () -> assertEquals(GrzesiekStep.QUIZ, GrzesiekStep.SHOW_BOTH.nextStep()),
                () -> assertEquals(GrzesiekStep.SHOW_LANGUAGE_FROM, GrzesiekStep.QUIZ.nextStep()),
                () -> assertEquals(GrzesiekStep.SHOW_LANGUAGE_TO, GrzesiekStep.SHOW_LANGUAGE_FROM.nextStep()),
                () -> assertEquals(GrzesiekStep.WRITE_LANGUAGE_FROM, GrzesiekStep.SHOW_LANGUAGE_TO.nextStep()),
                () -> assertEquals(GrzesiekStep.WRITE_LANGUAGE_TO, GrzesiekStep.WRITE_LANGUAGE_FROM.nextStep()),
                () -> assertEquals(GrzesiekStep.WRITE_LANGUAGE_TO, GrzesiekStep.WRITE_LANGUAGE_TO.nextStep())
        );
    }

    @Test
    void shouldReturnCorrectPreviousStep() {
        assertAll(
                () -> assertEquals(GrzesiekStep.SHOW_BOTH, GrzesiekStep.SHOW_BOTH.previousStep()),
                () -> assertEquals(GrzesiekStep.SHOW_BOTH, GrzesiekStep.QUIZ.previousStep()),
                () -> assertEquals(GrzesiekStep.QUIZ, GrzesiekStep.SHOW_LANGUAGE_FROM.previousStep()),
                () -> assertEquals(GrzesiekStep.SHOW_LANGUAGE_FROM, GrzesiekStep.SHOW_LANGUAGE_TO.previousStep()),
                () -> assertEquals(GrzesiekStep.SHOW_LANGUAGE_TO, GrzesiekStep.WRITE_LANGUAGE_FROM.previousStep()),
                () -> assertEquals(GrzesiekStep.WRITE_LANGUAGE_FROM, GrzesiekStep.WRITE_LANGUAGE_TO.previousStep())
        );
    }

    @Test
    void shouldReturnShowBothAsInitialStep() {
        assertEquals(GrzesiekStep.SHOW_BOTH, GrzesiekStep.SHOW_BOTH.initialStep());
    }

    @Test
    void shouldIdentifyMaxLevel() {
        assertAll(
                () -> assertTrue(GrzesiekStep.WRITE_LANGUAGE_TO.isMaxLevel()),
                () -> assertFalse(GrzesiekStep.SHOW_BOTH.isMaxLevel()),
                () -> assertFalse(GrzesiekStep.QUIZ.isMaxLevel()),
                () -> assertFalse(GrzesiekStep.SHOW_LANGUAGE_FROM.isMaxLevel()),
                () -> assertFalse(GrzesiekStep.SHOW_LANGUAGE_TO.isMaxLevel()),
                () -> assertFalse(GrzesiekStep.WRITE_LANGUAGE_FROM.isMaxLevel())
        );
    }

    @Test
    void shouldReturnCorrectIndex() {
        assertAll(
                () -> assertEquals(0, GrzesiekStep.SHOW_BOTH.index()),
                () -> assertEquals(1, GrzesiekStep.QUIZ.index()),
                () -> assertEquals(2, GrzesiekStep.SHOW_LANGUAGE_FROM.index()),
                () -> assertEquals(3, GrzesiekStep.SHOW_LANGUAGE_TO.index()),
                () -> assertEquals(4, GrzesiekStep.WRITE_LANGUAGE_FROM.index()),
                () -> assertEquals(5, GrzesiekStep.WRITE_LANGUAGE_TO.index())
        );
    }
}
