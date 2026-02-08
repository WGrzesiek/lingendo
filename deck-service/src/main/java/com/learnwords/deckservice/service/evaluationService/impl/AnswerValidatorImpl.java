package com.learnwords.deckservice.service.evaluationService.impl;

import com.learnwords.deckservice.dto.flashcard.FlashcardDto;
import com.learnwords.deckservice.service.algorithm.step.GrzesiekStep;
import com.learnwords.deckservice.service.algorithm.step.LeitnerStep;
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
            case RememberedAnswer remember -> validateRemembered(remember, step);
            case TextAnswer text -> validateText(flashcard, text, step);
            case ChoiceAnswer choice -> validateChoice(flashcard, choice ,step);
        };
    }

    @Override
    public boolean validateReview(FlashcardDto flashcard, TextAnswer answer) {
        String given = normalize(answer.text());
        String word = normalize(flashcard.wordDto().word());
        return equalsNormalized(word, given);

    }

    private boolean validateRemembered(RememberedAnswer answer, Step step) {
        if (step == GrzesiekStep.SHOW_BOTH || step == GrzesiekStep.SHOW_LANGUAGE_FROM || step == GrzesiekStep.SHOW_LANGUAGE_TO) {
            return answer.remembered();
        }
        if (step instanceof LeitnerStep) {
            return answer.remembered();
        }
        return false;
    }

    private boolean validateChoice(FlashcardDto flashcard, ChoiceAnswer answer, Step step) {
        String given = normalize(answer.selectedOption());
        List<String> expectedTranslations = flashcard.wordDto().translations().stream()
                .map(this::normalize)
                .toList();
        if (step == GrzesiekStep.QUIZ) {
            return matchesAny(expectedTranslations, given);
        }
        return false;
    }

    private boolean validateText(FlashcardDto flashcard, TextAnswer answer, Step step) {
        String given = normalize(answer.text());
        String word = normalize(flashcard.wordDto().word());
        List<String> expectedTranslations = flashcard.wordDto().translations().stream()
                .map(this::normalize)
                .toList();

        if (step == GrzesiekStep.WRITE_LANGUAGE_FROM) {
            return matchesAny(expectedTranslations, given);
        } else if (step == GrzesiekStep.WRITE_LANGUAGE_TO) {
            return equalsNormalized(word, given);
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

}