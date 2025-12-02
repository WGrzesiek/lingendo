package com.learnwords.deckservice.service.evaluationService;

import com.learnwords.deckservice.service.algorithm.step.GrzesiekStep;

public sealed interface UserAnswer permits TextAnswer, ChoiceAnswer, RememberedAnswer {
}

