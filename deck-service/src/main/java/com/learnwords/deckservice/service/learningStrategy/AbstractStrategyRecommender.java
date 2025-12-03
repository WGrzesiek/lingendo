package com.learnwords.deckservice.service.learningStrategy;

import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.learningStrategy.NextFlashcardRecommendation;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.entity.SessionFlashcard;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.service.UserProgressService;
import com.learnwords.deckservice.service.algorithm.AbstractAlgorithm;
import com.learnwords.deckservice.service.algorithm.state.AbstractState;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.grpcClient.VocabularyGrpcClient;
import com.learnwords.vocabulary.v1.GetWordResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public non-sealed abstract class AbstractStrategyRecommender implements LearningStrategy {

    protected final UserProgressService userProgressService;
    protected final VocabularyGrpcClient vocabularyGrpcClient;
    protected final AbstractAlgorithm algorithm;


    protected AbstractStrategyRecommender(UserProgressService userProgressService, VocabularyGrpcClient vocabularyGrpcClient, AbstractAlgorithm algorithm) {
        this.algorithm = algorithm;
        this.vocabularyGrpcClient = vocabularyGrpcClient;
        this.userProgressService = userProgressService;
    }

    @Override
    public final Optional<NextFlashcardRecommendation> recommendNext(List<SessionFlashcard> cards, String userId) {
        if (cards.isEmpty()) {
            return Optional.empty();
        }

        Session session = cards.getFirst().getSession();
        List<UserFlashcardProgressDto> progress =
                userProgressService.getProgressForDeck(session.getEnrollment().getId(), userId);

        List<UserFlashcardProgressDto> eligibleProgress = filterCommon(progress);

        if (eligibleProgress.isEmpty()) {
            return Optional.empty();
        }

        List<SessionFlashcard> eligibleCards = mapToSessionCards(cards, eligibleProgress);

        if (eligibleCards.isEmpty()) {
            return Optional.empty();
        }

        SessionFlashcard chosen = chooseNext(eligibleCards, eligibleProgress, session, userId);
        UserFlashcardProgressDto chosenProgress = eligibleProgress.stream()
                .filter(p -> p.flashcardId().equals(chosen.getFlashcard().getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Progress not found for chosen card"));

        return Optional.of(buildRecommendation(chosen, chosenProgress, session));    }

    protected List<UserFlashcardProgressDto> filterCommon(List<UserFlashcardProgressDto> progress) {
        return progress.stream()
                .filter(p -> {
                    if (p.isLearned() || p.isSkipped()) {
                        return false;
                    }
                    AlgorithmState state = algorithm.deserialize(p.algorithmState());
                    return !state.getStep().isMaxLevel();
                })
                .toList();
    }

    protected List<SessionFlashcard> mapToSessionCards(List<SessionFlashcard> cards, List<UserFlashcardProgressDto> eligibleProgress) {
        Set<String> ids = eligibleProgress.stream()
                .map(UserFlashcardProgressDto::flashcardId)
                .collect(Collectors.toSet());

        return cards.stream()
                .filter(sf -> ids.contains(sf.getFlashcard().getId()))
                .toList();
    }

    protected WordDto fetchWordDetails(String wordId) {
        GetWordResponse response = vocabularyGrpcClient.getWordById(wordId);
        List<SentenceDto> sentences = response.getWord().getSentencesList().stream()
                .map(s -> SentenceDto.builder()
                        .id(s.getId())
                        .sentence(s.getSentence())
                        .translation(s.getTranslation())
                        .build())
                .toList();
        List<SentenceDto> sentencesAI = response.getWord().getSentencesAiList().stream()
                .map(s -> SentenceDto.builder()
                        .id(s.getId())
                        .sentence(s.getSentence())
                        .translation(s.getTranslation())
                        .build())
                .toList();
        return WordDto.builder()
                .id(response.getWord().getId())
                .word(response.getWord().getWord())
                .translations(response.getWord().getTranslationsList())
                .sentences(sentences)
                .sentencesAI(sentencesAI)
                .build();
    }

    protected abstract NextFlashcardRecommendation buildRecommendation(
            SessionFlashcard chosen,
            UserFlashcardProgressDto progress,
            Session session
    );

    protected abstract SessionFlashcard chooseNext(
            List<SessionFlashcard> eligibleCards,
            List<UserFlashcardProgressDto> eligibleProgress,
            Session session,
            String userId
    );
    @Override
    public abstract boolean supports(LearnAlgorithm type);
}
