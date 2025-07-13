package com.learnwords.deckservice.service.Algorithm.State;

import com.google.gson.Gson;
import com.learnwords.deckservice.service.Algorithm.Step.Step;

import java.util.Map;

public abstract non-sealed class AbstractState<T extends Step, S extends AbstractState<T,S>> implements AlgorithmState{

    private final T step;

    protected AbstractState(T step) {
        this.step = step;
    }

    @Override
    public String serialize() {
        Gson gson = new Gson();
        Map<String, String> map = Map.of("step", step.name());
        return gson.toJson(map);
    }

    @Override
    public S deserialize(String serializedState) {
        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(serializedState, Map.class);
        String stepName = map.get("step");
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
