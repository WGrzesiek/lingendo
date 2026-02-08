package com.learnwords.deckservice.service.algorithm.state;

import com.google.gson.Gson;
import com.learnwords.deckservice.exception.exceptions.StepWithThisNameNoExist;
import com.learnwords.deckservice.service.algorithm.step.Step;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
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

    @Override
    public T getStep() {
        return step;
    }

    @Override
    public int getTotalSteps() {
        return step.initialStep().stepCount();
    }

    protected abstract S createStateFromStepName(String stepName) throws StepWithThisNameNoExist;

}
