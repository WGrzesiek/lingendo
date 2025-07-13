package com.learnwords.deckservice.service.Algorithm;


import com.learnwords.deckservice.service.Algorithm.State.GrzesiekState;
import com.learnwords.deckservice.service.Algorithm.Step.GrzesiekStep;
import org.springframework.stereotype.Component;

@Component
public final class GrzesiekAlgorithm extends Algorithm {


    @Override
    public GrzesiekState getInitialState() {
        return new GrzesiekState(GrzesiekStep.SHOW_BOTH);
    }
}
