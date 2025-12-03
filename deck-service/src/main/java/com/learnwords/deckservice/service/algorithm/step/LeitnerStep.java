//package com.learnwords.deckservice.service.Algorithm.Step;
//
//import lombok.Getter;
//
//import java.time.Duration;
//
//public enum LeitnerStep implements Step{
//    BOX_0(Duration.ofMinutes(3)),
//    BOX_1(Duration.ofMinutes(10)),
//    BOX_2(Duration.ofMinutes(30)),
//    BOX_3(Duration.ofHours(2)),
//    BOX_4(Duration.ofDays(3)),
//    BOX_5(Duration.ofDays(7));
//
//    @Getter
//    private final Duration duration;
//
//    LeitnerStep(Duration duration) {
//        this.duration = duration;
//    }
//
//    @Override
//    public LeitnerStep nextStep() {
//        return switch (this){
//            case BOX_0 -> BOX_1;
//            case BOX_1 -> BOX_2;
//            case BOX_2 -> BOX_3;
//            case BOX_3 -> BOX_4;
//            case BOX_4 -> BOX_5;
//            case BOX_5 -> BOX_5;
//        };
//    }
//
//    @Override
//    public LeitnerStep previousStep() {
//        return switch (this){
//            case BOX_0 -> BOX_0;
//            case BOX_1 -> BOX_0;
//            case BOX_2 -> BOX_1;
//            case BOX_3 -> BOX_2;
//            case BOX_4 -> BOX_3;
//            case BOX_5 -> BOX_4;
//        };
//    }
//
//    @Override
//    public boolean isMaxLevel() {
//        return this == BOX_5;
//    }
//
//    @Override
//    public int index() {
//        return ordinal();
//    }
//
//
//}
