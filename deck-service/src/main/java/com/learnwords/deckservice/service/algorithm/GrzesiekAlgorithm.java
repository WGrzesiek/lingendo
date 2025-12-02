package com.learnwords.deckservice.service.algorithm;


import com.google.gson.Gson;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.exception.exceptions.StepWithThisNameNoExist;
import com.learnwords.deckservice.service.evaluationService.responseResult.AlgorithmResult;
import com.learnwords.deckservice.service.evaluationService.responseResult.Failure;
import com.learnwords.deckservice.service.evaluationService.responseResult.MaxLevel;
import com.learnwords.deckservice.service.evaluationService.responseResult.Success;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.algorithm.state.GrzesiekState;
import com.learnwords.deckservice.service.algorithm.step.GrzesiekStep;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class GrzesiekAlgorithm extends AbstractAlgorithm {


    @Override
    public LearnAlgorithm getType() {
        return LearnAlgorithm.GRZESIEK_ALGORITHM;
    }

    @Override
    protected AlgorithmState getInitialState() {
        return new GrzesiekState(GrzesiekStep.SHOW_BOTH);
    }

    @Override
    public GrzesiekState deserialize(String json) throws StepWithThisNameNoExist {
        try {
            return new GrzesiekState(GrzesiekStep.valueOf(
                    new Gson().<Map<String, String>>fromJson(json, Map.class).get("step")));
        }
        catch (Exception e){
            throw new StepWithThisNameNoExist("Invalid json");
        }
    }
    @Override
    public AlgorithmResult<AlgorithmState> processAnswer(AlgorithmState state, boolean correct) {
        if (!(state instanceof GrzesiekState gState)) {
            throw new IllegalArgumentException("Invalid state type");
        }

        GrzesiekStep step = gState.getStep();

        if (step.isMaxLevel() && correct) {
            return new MaxLevel<>(gState);
        }

        if (correct) {
            return new Success<>(promote(gState));
        } else {
            return new Failure<>(demote(gState));
        }
    }

}
