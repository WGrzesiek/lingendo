package com.learnwords.deckservice.service.learningStrategy;

import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.learningStrategy.NextFlashcardRecommendation;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.entity.SessionFlashcard;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.service.UserProgressService;
import com.learnwords.deckservice.service.algorithm.LeitnerAlgorithm;
import com.learnwords.deckservice.service.algorithm.state.LeitnerState;
import com.learnwords.deckservice.service.algorithm.step.LeitnerStep;
import com.learnwords.deckservice.service.grpcClient.VocabularyGrpcClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Strategia nauki dla algorytmu Leitnera (system pudełek).
 * Priorytetyzuje fiszki z niższych pudełek, które wymagają częstszych powtórek.
 */
@Component
public final class LeitnerStrategyImpl extends AbstractStrategyRecommender implements LearningStrategy {

    private final LeitnerAlgorithm leitnerAlgorithm;

    public LeitnerStrategyImpl(
            UserProgressService userProgressService, 
            VocabularyGrpcClient vocabularyGrpcClient, 
            LeitnerAlgorithm leitnerAlgorithm
    ) {
        super(userProgressService, vocabularyGrpcClient, leitnerAlgorithm);
        this.leitnerAlgorithm = leitnerAlgorithm;
    }

    @Override
    protected SessionFlashcard chooseNext(
            List<SessionFlashcard> eligibleCards,
            List<UserFlashcardProgressDto> eligibleProgress,
            Session session,
            String userId
    ) {
        Map<String, LeitnerState> states = eligibleProgress.stream()
                .collect(Collectors.toMap(
                        UserFlashcardProgressDto::flashcardId,
                        p -> leitnerAlgorithm.deserialize(p.algorithmState())
                ));


        Optional<SessionFlashcard> chosen = eligibleCards.stream()
                .min(Comparator.comparingInt(card -> {
                    LeitnerState state = states.get(card.getFlashcard().getId());
                    return state != null ? state.getStep().ordinal() : Integer.MAX_VALUE;
                }));

        if (chosen.isPresent()) {
            LeitnerStep lowestStep = states.get(chosen.get().getFlashcard().getId()).getStep();
            List<SessionFlashcard> sameLevel = eligibleCards.stream()
                    .filter(card -> {
                        LeitnerState state = states.get(card.getFlashcard().getId());
                        return state != null && state.getStep() == lowestStep;
                    })
                    .toList();

            if (!sameLevel.isEmpty()) {
                return sameLevel.get(ThreadLocalRandom.current().nextInt(sameLevel.size()));
            }
        }

        return eligibleCards.get(ThreadLocalRandom.current().nextInt(eligibleCards.size()));
    }

    @Override
    protected NextFlashcardRecommendation buildRecommendation(
            SessionFlashcard chosen,
            UserFlashcardProgressDto progress,
            Session session
    ) {
        WordDto content = fetchWordDetails(chosen.getFlashcard().getWordId());

        return NextFlashcardRecommendation.builder()
                .flashcardId(chosen.getFlashcard().getId())
                .content(content)
                .interactionType(InteractionType.PRESENTATION)
                .quizOptions(Collections.emptyList())
                .build();
    }

    @Override
    public boolean supports(LearnAlgorithm type) {
        return LearnAlgorithm.LEITNER_ALGORITHM.equals(type);
    }
}
