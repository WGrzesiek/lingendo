//package com.learnwords.deckservice.service.Algorithm;
//
//import com.learnwords.deckservice.service.Algorithm.State.LeitnerState;
//import com.learnwords.deckservice.service.Algorithm.Step.LeitnerStep;
//
//public final class LeinerAlgorithm implements AlgorithmType<LeitnerState> {
//
//    @Override
//    public LeitnerState initialize() {
//        return new LeitnerState(LeitnerStep.BOX_0);
//    }
//
//    @Override
//    public LeitnerState promote(LeitnerState state) {
//        return new LeitnerState(state.step().nextStep());
//    }
//
//    @Override
//    public LeitnerState demote(LeitnerState state) {
//        return new LeitnerState(state.step().previousStep());
//    }
//
//    @Override
//    public LeitnerState reset(LeitnerState state) {
//        return new LeitnerState(LeitnerStep.BOX_0);
//    }
//
//    @Override
//    public LeitnerState getCurrentState(LeitnerState state) {
//        return state;
//    }
//
//}
