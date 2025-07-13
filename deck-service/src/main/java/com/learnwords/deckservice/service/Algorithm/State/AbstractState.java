package com.learnwords.deckservice.service.Algorithm.State;

import com.learnwords.deckservice.service.Algorithm.Step.Step;

public abstract non-sealed class AbstractState<T extends Step, S extends AbstractState<T,S>> implements AlgorithmState{

    private final T step;

    protected AbstractState(T step) {
        this.step = step;
    }

    @Override
    public String serialize() {
        return String.format("Step: %s", step.name());
    }

    @Override
    public S deserialize(String serializedState) {
        String stepName = serializedState.replace("Step: ", "");
        return createStateFromStepName(stepName);
    }

    @Override
    public S reset(){
        return createStateFromStepName(step.initialStep().name());
    }

    @Override
    public S next(){
        return createStateFromStepName(step.nextStep().name());
    }

    @Override
    public S previous(){
        return createStateFromStepName(step.previousStep().name());
    }

    protected abstract S createStateFromStepName(String stepName);
}
