package com.learnwords.deckservice.service.evaluationService.impl;

import com.learnwords.deckservice.dto.flashcard.FlashcardDto;
import com.learnwords.deckservice.service.algorithm.step.GrzesiekStep;
import com.learnwords.deckservice.service.algorithm.step.Step;
import com.learnwords.deckservice.service.evaluationService.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AnswerValidatorImpl implements AnswerValidator {

    public boolean validate(FlashcardDto flashcard, UserAnswer userAnswer, Step step) {
        return switch (userAnswer) {
            case RememberedAnswer remember -> remember.remembered();
            case TextAnswer text -> validateText(flashcard, text, step);
            case ChoiceAnswer choice -> validateChoice(flashcard, choice);
        };
    }

    private boolean validateText(FlashcardDto flashcard, TextAnswer answer, Step step) {
        String given = normalize(answer.text());

        if (step == GrzesiekStep.WRITE_LANGUAGE_FROM) {
            return matchesAny(flashcard.wordDto().translations(), given);
        } else if (step == GrzesiekStep.WRITE_LANGUAGE_TO) {
            return equalsNormalized(flashcard.wordDto().word(), given);
        }
        return false;
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private boolean matchesAny(List<String> expected, String given) {
        return expected.stream().map(this::normalize).anyMatch(given::equals);
    }

    private boolean equalsNormalized(String expected, String given) {
        return normalize(expected).equals(given);
    }


    private boolean validateChoice(FlashcardDto flashcard, ChoiceAnswer answer) {

        return true;
    }
}