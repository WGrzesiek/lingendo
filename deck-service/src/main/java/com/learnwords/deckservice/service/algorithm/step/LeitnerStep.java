package com.learnwords.deckservice.service.algorithm.step;

import lombok.Getter;

import java.time.Duration;

/**
 * Kroki algorytmu Leitnera (system pudełek).
 * Każde pudełko ma przypisany czas powtórki.
 * Poprawna odpowiedź = przejście do wyższego pudełka.
 * Błędna odpowiedź = powrót do pudełka 0.
 */
public enum LeitnerStep implements Step {
    BOX_0(Duration.ofMinutes(3)),
    BOX_1(Duration.ofMinutes(10)),
    BOX_2(Duration.ofMinutes(30)),
    BOX_3(Duration.ofHours(2)),
    BOX_4(Duration.ofDays(3)),
    BOX_5(Duration.ofDays(7)),
    MAX_LEVEL(Duration.ofDays(30));

    @Getter
    private final Duration duration;

    LeitnerStep(Duration duration) {
        this.duration = duration;
    }

    @Override
    public LeitnerStep nextStep() {
        return switch (this) {
            case BOX_0 -> BOX_1;
            case BOX_1 -> BOX_2;
            case BOX_2 -> BOX_3;
            case BOX_3 -> BOX_4;
            case BOX_4 -> BOX_5;
            case BOX_5 -> MAX_LEVEL;
            case MAX_LEVEL -> MAX_LEVEL;
        };
    }

    @Override
    public LeitnerStep previousStep() {
        return switch (this) {
            case BOX_0 -> BOX_0;
            case BOX_1 -> BOX_0;
            case BOX_2 -> BOX_0;
            case BOX_3 -> BOX_0;
            case BOX_4 -> BOX_0;
            case BOX_5 -> BOX_0;
            case MAX_LEVEL -> MAX_LEVEL;
        };
    }

    @Override
    public LeitnerStep initialStep() {
        return BOX_0;
    }

    @Override
    public boolean isMaxLevel() {
        return this == MAX_LEVEL;
    }

    @Override
    public boolean isLastLearnStep() {
        return this == BOX_5;
    }

    @Override
    public int index() {
        return ordinal();
    }

    @Override
    public int stepCount() {
        return LeitnerStep.values().length;
    }
}
