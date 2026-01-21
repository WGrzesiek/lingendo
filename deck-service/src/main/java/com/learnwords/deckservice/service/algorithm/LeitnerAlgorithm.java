package com.learnwords.deckservice.service.algorithm;

import com.google.gson.Gson;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.exception.exceptions.StepWithThisNameNoExist;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.algorithm.state.LeitnerState;
import com.learnwords.deckservice.service.algorithm.step.LeitnerStep;
import com.learnwords.deckservice.service.evaluationService.responseResult.AlgorithmResult;
import com.learnwords.deckservice.service.evaluationService.responseResult.Failure;
import com.learnwords.deckservice.service.evaluationService.responseResult.MaxLevel;
import com.learnwords.deckservice.service.evaluationService.responseResult.Success;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Algorytm Leitnera - system pudełek do nauki fiszek.
 * 
 * Zasady:
 * - Każda fiszka zaczyna w pudełku BOX_0
 * - Poprawna odpowiedź = przejście do następnego pudełka
 * - Błędna odpowiedź = powrót do pudełka BOX_0
 * - Każde pudełko ma przypisany interwał czasowy powtórki
 */
@Component
public final class LeitnerAlgorithm extends AbstractAlgorithm {

    @Override
    public LearnAlgorithm getType() {
        return LearnAlgorithm.LEITNER_ALGORITHM;
    }

    @Override
    public AlgorithmState getInitialState() {
        return new LeitnerState(LeitnerStep.BOX_0);
    }

    @Override
    public LeitnerState deserialize(String json) throws StepWithThisNameNoExist {
        try {
            return new LeitnerState(LeitnerStep.valueOf(
                    new Gson().<Map<String, String>>fromJson(json, Map.class).get("step")));
        } catch (Exception e) {
            throw new StepWithThisNameNoExist("Invalid json");
        }
    }

    @Override
    public AlgorithmResult<AlgorithmState> processAnswer(AlgorithmState state, boolean correct) {
        if (!(state instanceof LeitnerState lState)) {
            throw new IllegalArgumentException("Invalid state type");
        }

        LeitnerStep step = lState.getStep();

        if (step.isMaxLevel() && correct) {
            return new MaxLevel<>(lState);
        }

        if (correct) {
            return new Success<>(promote(lState));
        } else {
            // W algorytmie Leitnera błędna odpowiedź = reset do BOX_0
            return new Failure<>(reset(lState));
        }
    }
}
