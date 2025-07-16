package com.learnwords.deckservice.service.Algorithm;


import com.google.gson.Gson;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.service.Algorithm.State.AlgorithmState;
import com.learnwords.deckservice.service.Algorithm.State.GrzesiekState;
import com.learnwords.deckservice.service.Algorithm.Step.GrzesiekStep;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class GrzesiekAlgorithm extends AbstractAlgorithm {


    @Override public LearnAlgorithm getType() {
        return LearnAlgorithm.GRZESIEK_ALGORITHM;
    }

    @Override protected AlgorithmState getInitialState() {
        return new GrzesiekState(GrzesiekStep.SHOW_BOTH);
    }

    @Override
    public GrzesiekState deserialize(String json) {
        return new GrzesiekState(GrzesiekStep.valueOf(
                new Gson().<Map<String,String>>fromJson(json, Map.class).get("step")));
    }

}
