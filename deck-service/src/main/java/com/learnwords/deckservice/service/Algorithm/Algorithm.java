package com.learnwords.deckservice.service.Algorithm;

import com.learnwords.deckservice.service.Algorithm.State.AlgorithmState;

public abstract sealed class Algorithm implements AlgorithmType<AlgorithmState> permits GrzesiekAlgorithm  {

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

    protected abstract AlgorithmState getInitialState();
}
