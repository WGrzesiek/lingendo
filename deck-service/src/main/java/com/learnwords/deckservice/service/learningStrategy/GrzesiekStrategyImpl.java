package com.learnwords.deckservice.service.learningStrategy;

import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.learningStrategy.NextFlashcardRecommendation;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.entity.SessionFlashcard;
import com.learnwords.deckservice.entity.UserFlashcardProgress;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.service.SessionFlashcardService;
import com.learnwords.deckservice.service.SessionService;
import com.learnwords.deckservice.service.UserProgressService;
import com.learnwords.deckservice.service.algorithm.AbstractAlgorithm;
import com.learnwords.deckservice.service.algorithm.GrzesiekAlgorithm;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.algorithm.state.GrzesiekState;
import com.learnwords.deckservice.service.algorithm.step.GrzesiekStep;
import com.learnwords.deckservice.service.grpcClient.VocabularyGrpcClient;
import com.learnwords.vocabulary.v1.Word;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public final class GrzesiekStrategyImpl extends AbstractStrategyRecommender implements LearningStrategy {

    private final GrzesiekAlgorithm grzesiekAlgorithm;
    private static final double THRESHOLD = 0.2;
    private static final int QUIZ_OPTION_COUNT = 4;

    public GrzesiekStrategyImpl(UserProgressService userProgressService, VocabularyGrpcClient vocabularyGrpcClient, @Qualifier("grzesiekAlgorithm") AbstractAlgorithm algorithm, GrzesiekAlgorithm grzesiekAlgorithm) {
        super(userProgressService, vocabularyGrpcClient, algorithm);
        this.grzesiekAlgorithm = grzesiekAlgorithm;
    }

    @Override
    protected SessionFlashcard chooseNext(
            List<SessionFlashcard> eligibleCards,
            List<UserFlashcardProgressDto> eligibleProgress,
            Session session,
            String userId
    ) {
        int totalCards = eligibleCards.size();

        Map<GrzesiekStep, Long> howMany = eligibleProgress.stream()
                .map(p -> (GrzesiekState) algorithm.deserialize(p.algorithmState()))
                .collect(Collectors.groupingBy(GrzesiekState::getStep, Collectors.counting()));

        if (ratio(howMany, totalCards, GrzesiekStep.SHOW_BOTH) < THRESHOLD) {
            return pick(eligibleCards, eligibleProgress, GrzesiekStep.SHOW_BOTH);
        }

        if (ratio(howMany, totalCards, GrzesiekStep.QUIZ) < THRESHOLD) {
            return pick(eligibleCards, eligibleProgress,
                    GrzesiekStep.SHOW_BOTH, GrzesiekStep.QUIZ);
        }

        if (ratio(howMany, totalCards, GrzesiekStep.SHOW_LANGUAGE_FROM) < THRESHOLD) {
            return pick(eligibleCards, eligibleProgress,
                    GrzesiekStep.SHOW_BOTH,
                    GrzesiekStep.QUIZ,
                    GrzesiekStep.SHOW_LANGUAGE_FROM);
        }

        if (ratio(howMany, totalCards, GrzesiekStep.SHOW_LANGUAGE_TO) < THRESHOLD) {
            return pick(eligibleCards, eligibleProgress,
                    GrzesiekStep.SHOW_BOTH,
                    GrzesiekStep.QUIZ,
                    GrzesiekStep.SHOW_LANGUAGE_FROM,
                    GrzesiekStep.SHOW_LANGUAGE_TO);
        }

        if (ratio(howMany, totalCards, GrzesiekStep.WRITE_LANGUAGE_FROM) < THRESHOLD) {
            return pick(eligibleCards, eligibleProgress,
                    GrzesiekStep.SHOW_BOTH,
                    GrzesiekStep.QUIZ,
                    GrzesiekStep.SHOW_LANGUAGE_FROM,
                    GrzesiekStep.SHOW_LANGUAGE_TO,
                    GrzesiekStep.WRITE_LANGUAGE_FROM);
        }
        if (ratio(howMany, totalCards, GrzesiekStep.WRITE_LANGUAGE_TO) < THRESHOLD) {
            return pick(eligibleCards, eligibleProgress,
                    GrzesiekStep.SHOW_BOTH,
                    GrzesiekStep.QUIZ,
                    GrzesiekStep.SHOW_LANGUAGE_FROM,
                    GrzesiekStep.SHOW_LANGUAGE_TO,
                    GrzesiekStep.WRITE_LANGUAGE_FROM,
                    GrzesiekStep.WRITE_LANGUAGE_TO
            );
        }

        return pick(eligibleCards, eligibleProgress,
                GrzesiekStep.SHOW_BOTH,
                GrzesiekStep.QUIZ,
                GrzesiekStep.SHOW_LANGUAGE_FROM,
                GrzesiekStep.SHOW_LANGUAGE_TO,
                GrzesiekStep.WRITE_LANGUAGE_FROM,
                GrzesiekStep.WRITE_LANGUAGE_TO);
    }

    @Override
    protected NextFlashcardRecommendation buildRecommendation(
            SessionFlashcard chosen,
            UserFlashcardProgressDto progress,
            Session session
    ) {
        WordDto content = fetchWordDetails(chosen.getFlashcard().getWordId());

        GrzesiekState state = (GrzesiekState) algorithm.deserialize(progress.algorithmState());


        InteractionType type;
        //Note bierze czasmi drugie tlumaczenie slowka jako opcje do quziu
        List<String> quizOptions = generateQuizOptions(content, session.getSessionFlashcards().stream()
                .map(SessionFlashcard::getFlashcard)
                .toList());

        if (state.getStep() == GrzesiekStep.QUIZ) {
            type = InteractionType.QUIZ_CHOICE;
        } else if (state.getStep() == GrzesiekStep.WRITE_LANGUAGE_TO) {
            type = InteractionType.TYPING_INPUT_TO;
        } else if (state.getStep() == GrzesiekStep.WRITE_LANGUAGE_FROM) {
            type = InteractionType.TYPING_INPUT_FROM;
        }
        else if (state.getStep() == GrzesiekStep.SHOW_LANGUAGE_FROM) {
            type = InteractionType.REMEMBER_CHECK_FROM;
        } else if (state.getStep() == GrzesiekStep.SHOW_LANGUAGE_TO) {
            type = InteractionType.REMEMBER_CHECK_TO;
        }
        else {
            type = InteractionType.PRESENTATION;
        }

        return NextFlashcardRecommendation.builder()
                .flashcardId(chosen.getFlashcard().getId())
                .content(content)
                .interactionType(type)
                .quizOptions(quizOptions)
                .build();
    }

    @Override
    public boolean supports(LearnAlgorithm type) {
        return LearnAlgorithm.GRZESIEK_ALGORITHM.equals(type);
    }

    private double ratio(Map<GrzesiekStep, Long> map, int total, GrzesiekStep step) {
        if (total <= 0) return 0.0;
        return (double) map.getOrDefault(step, 0L) / total;
    }

    private SessionFlashcard pick(
            List<SessionFlashcard> eligibleCards,
            List<UserFlashcardProgressDto> eligibleProgress,
            GrzesiekStep... allowedSteps
    ) {
        Set<GrzesiekStep> allowed = Set.of(allowedSteps);

        Map<String, GrzesiekState> states = eligibleProgress.stream()
                .collect(Collectors.toMap(
                        UserFlashcardProgressDto::flashcardId,
                        p -> (GrzesiekState) algorithm.deserialize(p.algorithmState())
                ));

        List<SessionFlashcard> pool = eligibleCards.stream()
                .filter(card -> {
                    GrzesiekState st = states.get(card.getFlashcard().getId());
                    return st != null && allowed.contains(st.getStep());
                })
                .toList();

        if (pool.isEmpty()) {
            pool = eligibleCards;
        }

        return pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
    }

    private List<String> generateQuizOptions(WordDto correctWord, List<Flashcard> allFlashcards) {
        String correct = correctWord.translations().getFirst();

        List<Flashcard> optionsPool = allFlashcards.stream()
                .filter(fc -> !fc.getWordId().equals(correctWord.id()))
                .toList();

        if (optionsPool.isEmpty()) {
            return List.of(correct);
        }

        var wordsResponse = vocabularyGrpcClient.batchGetWordsByIds(
                optionsPool.stream().map(Flashcard::getWordId).toList()
        );

        List<String> wrongOptions = wordsResponse.getWordsList().stream()
                .flatMap(w -> w.getTranslationsList().stream())
                .filter(t -> !t.equals(correct))
                .distinct()
                .limit(QUIZ_OPTION_COUNT - 1L)
                .toList();

        List<String> options = new ArrayList<>(wrongOptions);
        options.add(correct);

        while (options.size() < QUIZ_OPTION_COUNT) {
            options.add(correct);
        }

        Collections.shuffle(options, ThreadLocalRandom.current());
        return options;
    }
}