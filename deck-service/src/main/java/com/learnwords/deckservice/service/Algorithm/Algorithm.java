package com.learnwords.deckservice.service.Algorithm;

import com.learnwords.deckservice.service.Algorithm.State.AlgorithmState;

public interface Algorithm<T extends AlgorithmState>{
    T initialize();
    T promote(T state);
    T demote(T state);
    T reset(T state);
    T getCurrentState(T state);
    T deserialize(String serializedState);


}
