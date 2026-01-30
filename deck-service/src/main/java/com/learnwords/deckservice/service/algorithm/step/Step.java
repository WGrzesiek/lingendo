package com.learnwords.deckservice.service.algorithm.step;

import org.springframework.stereotype.Component;

@Component
public sealed interface Step permits GrzesiekStep, LeitnerStep {


    String name();

    Step nextStep();

    Step previousStep();

    Step initialStep();

    boolean isMaxLevel();

    boolean isLastLearnStep();

    int index();

    int stepCount();
}
