package com.learnwords.deckservice.service.algorithm.step;

import org.springframework.stereotype.Component;

@Component
public sealed interface Step permits GrzesiekStep {


    String name();

    Step nextStep();

    Step previousStep();

    Step initialStep();

    boolean isMaxLevel();

    int index();

    int stepCount();
}
