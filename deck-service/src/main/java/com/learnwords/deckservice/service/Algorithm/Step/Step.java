package com.learnwords.deckservice.service.Algorithm.Step;

//public sealed interface Step permits GrzesiekStep, LeitnerStep {
public sealed interface Step permits GrzesiekStep {


    String name();

    Step nextStep();

    Step previousStep();

    Step initialStep();

    boolean isMaxLevel();

    int index();
}
