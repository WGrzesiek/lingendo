package com.learnwords.deckservice.service.algorithm;

import com.learnwords.deckservice.exception.exceptions.StepWithThisNameNoExist;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.evaluationService.responseResult.AlgorithmResult;

public interface Algorithm<T extends AlgorithmState>{
    T initialize();
    T promote(T state);
    T demote(T state);
    T reset(T state);
    T getCurrentState(T state);
    T deserialize(String serializedState) throws StepWithThisNameNoExist;
    AlgorithmResult<T> processAnswer(T state, boolean correct);
}
