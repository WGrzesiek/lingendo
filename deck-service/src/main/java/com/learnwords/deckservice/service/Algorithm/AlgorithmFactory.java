package com.learnwords.deckservice.service.Algorithm;

import com.learnwords.deckservice.enums.LearnAlgorithm;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AlgorithmFactory {

    private final Map<LearnAlgorithm, AbstractAlgorithm> algorithms;

    public AlgorithmFactory(List<AbstractAlgorithm> beans) {
        this.algorithms = beans.stream()
                .collect(Collectors.toMap(AbstractAlgorithm::getType, Function.identity()));
    }

    public AbstractAlgorithm get(LearnAlgorithm type) {
        return Optional.ofNullable(algorithms.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Nieobsługiwany algorytm: " + type));
    }
}

