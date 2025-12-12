package com.learnwords.deckservice.service.algorithm;

import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.evaluationService.responseResult.AlgorithmResult;
import com.learnwords.deckservice.service.evaluationService.responseResult.Failure;
import com.learnwords.deckservice.service.evaluationService.responseResult.MaxLevel;
import com.learnwords.deckservice.service.evaluationService.responseResult.Success;


public abstract sealed class AbstractAlgorithm implements Algorithm<AlgorithmState> permits GrzesiekAlgorithm {

    @Override
    public AlgorithmState initialize() {
        return getInitialState();
    }

    @Override
    public AlgorithmState promote(AlgorithmState state) {
        return state.next();
    }

    @Override
    public AlgorithmState demote(AlgorithmState state) {
        return state.previous();
    }

    @Override
    public AlgorithmState reset(AlgorithmState state) {
        return state.reset();
    }

    @Override
    public AlgorithmState getCurrentState(AlgorithmState state) {
        return state;
    }

    @Override
    public AlgorithmResult<AlgorithmState> processAnswer(AlgorithmState state, boolean correct) {
        if (correct) {
            if(state.getStep().isMaxLevel()) {
                return new MaxLevel<>(state);
            }
            return new Success<>(promote(state));
        } else {
            return new Failure<>(demote(state));
        }
    }


    protected abstract AlgorithmState getInitialState();

    public abstract LearnAlgorithm getType();
}
