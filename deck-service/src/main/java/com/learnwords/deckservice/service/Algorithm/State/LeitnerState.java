//package com.learnwords.deckservice.service.Algorithm.State;
//
//import com.learnwords.deckservice.service.Algorithm.Step.LeitnerStep;
//
//import java.time.Duration;
//import java.time.Instant;
//
//public record LeitnerState(LeitnerStep step, Instant lastReviewed) implements AlgorithmState {
//
//    @Override
//    public String serialize() {
//        return step.name();
//    }
//
//    @Override
//    public AlgorithmState deserialize(String serializedState) {
//        return new LeitnerState(LeitnerStep.valueOf(serializedState), Instant.now());
//    }
//
//    @Override
//    public AlgorithmState reset() {
//        return new LeitnerState(LeitnerStep.BOX_0, Instant.now());
//    }
//
//    @Override
//    public AlgorithmState next() {
//        return new LeitnerState(step.nextStep());
//    }
//
//    @Override
//    public AlgorithmState previous() {
//        return new LeitnerState(step.previousStep());
//    }
//
//    public AlgorithmState processAnswer(boolean correct) {
//        return switch (correct){
//            case true -> step.isMaxLevel() ? new LeitnerState(step) : next();
//            case false -> previous();
//        };
//    }
//
//    public boolean isReadyForReview() {
//        return switch (step) {
//            case BOX_0 -> true; // Always ready
//            case BOX_1, BOX_2, BOX_3, BOX_4, BOX_5 ->
//                    Duration.between(lastReviewed, Instant.now())
//                            .compareTo(step.getDuration()) >= 0;
//        };
//    }
//}
