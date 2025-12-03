package com.learnwords.deckservice.service.algorithm.step;


public enum GrzesiekStep implements Step{
    SHOW_BOTH,
    QUIZ,
    SHOW_LANGUAGE_FROM,
    SHOW_LANGUAGE_TO,
    WRITE_LANGUAGE_FROM,
    WRITE_LANGUAGE_TO;

    @Override
    public GrzesiekStep nextStep() {
        return switch (this) {
            case SHOW_BOTH -> QUIZ;
            case QUIZ -> SHOW_LANGUAGE_FROM;
            case SHOW_LANGUAGE_FROM -> SHOW_LANGUAGE_TO;
            case SHOW_LANGUAGE_TO -> WRITE_LANGUAGE_FROM;
            case WRITE_LANGUAGE_FROM -> WRITE_LANGUAGE_TO;
            case WRITE_LANGUAGE_TO -> WRITE_LANGUAGE_TO;
        };
    }

    @Override
    public GrzesiekStep previousStep() {
        return switch (this){
            case SHOW_BOTH -> SHOW_BOTH;
            case QUIZ -> SHOW_BOTH;
            case SHOW_LANGUAGE_FROM -> QUIZ;
            case SHOW_LANGUAGE_TO -> SHOW_LANGUAGE_FROM;
            case WRITE_LANGUAGE_FROM -> SHOW_LANGUAGE_TO;
            case WRITE_LANGUAGE_TO -> WRITE_LANGUAGE_FROM;
        };
    }

    @Override
    public GrzesiekStep initialStep(){
        return SHOW_BOTH;
    }

    @Override
    public boolean isMaxLevel() {
        return this == WRITE_LANGUAGE_TO;
    }

    @Override
    public int index() {
        return ordinal();
    }
}
