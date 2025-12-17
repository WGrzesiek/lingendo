package com.learnwords.deckservice.service.reviewsStrategy.impl;

import com.learnwords.deckservice.dto.evaluationService.AnswerResultDto;
import com.learnwords.deckservice.dto.flashcard.FlashcardDto;
import com.learnwords.deckservice.dto.learningStrategy.NextFlashcardRecommendation;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.entity.*;
import com.learnwords.deckservice.enums.LearningPhase;
import com.learnwords.deckservice.service.FlashcardService;
import com.learnwords.deckservice.service.UserProgressService;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.evaluationService.AnswerValidator;
import com.learnwords.deckservice.service.evaluationService.TextAnswer;
import com.learnwords.deckservice.service.evaluationService.UserAnswer;
import com.learnwords.deckservice.service.evaluationService.responseResult.AlgorithmResult;
import com.learnwords.deckservice.service.grpcClient.VocabularyGrpcClient;
import com.learnwords.deckservice.service.learningStrategy.InteractionType;
import com.learnwords.deckservice.service.reviewsStrategy.ReviewsStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class ReviewsStrategyImpl implements ReviewsStrategy {

    private final UserProgressService userProgressService;
    private final AnswerValidator answerValidator;
    private final VocabularyGrpcClient vocabularyGrpcClient;
    private final FlashcardService flashcardService;

    public ReviewsStrategyImpl(
                               UserProgressService userProgressService,
                               AnswerValidator answerValidator,
                               VocabularyGrpcClient vocabularyGrpcClient,
                               FlashcardService flashcardService) {
        this.userProgressService = userProgressService;
        this.answerValidator = answerValidator;
        this.vocabularyGrpcClient = vocabularyGrpcClient;
        this.flashcardService = flashcardService;
    }

    @Override
    public Optional<NextFlashcardRecommendation> recommendNext(String userId, String enrolmentId) {
        List<UserFlashcardProgressDto> progress = userProgressService.getProgressForDeck(enrolmentId, userId);
        List<UserFlashcardProgressDto> toReview = progress.stream()
                .filter(p -> !p.isSkipped())
                .filter(p -> p.repetitionCount() < 3)
                .filter(UserFlashcardProgressDto::isLearned)
                .filter(p -> p.phase() == LearningPhase.REVIEW)
                .filter(p -> p.nextReviewAt().isBefore(Instant.now()))
                .toList();
        if (toReview.isEmpty()) {
            //NOTE dorobic exception ze nie ma juz do powtorek na dzisiaj
            return Optional.empty();
        }

        List<UserFlashcardProgressDto> shuffled = new ArrayList<>(toReview);
        Collections.shuffle(shuffled);
        UserFlashcardProgressDto selected = shuffled.getFirst();
        FlashcardDto flashcard = flashcardService.getFlashcardById(selected.flashcardId(), userId);
        return Optional.ofNullable(NextFlashcardRecommendation.builder()
                .flashcardId(flashcard.id())
                .content(flashcard.wordDto())
                .interactionType(InteractionType.TYPING_INPUT_TO)
                .build());

    }
    //NOTE trzeva kiedys dodac szukanie po enrolmentId tak samo bo jedna fiszka moze byc w dwoch enrolmentach
    //NOTE tak samo w study flow
    @Override
    public AnswerResultDto registerReviewResult(String flashcardId, TextAnswer answer, String userId) {
        UserFlashcardProgressDto progress = userProgressService.getFlashcardProgress(flashcardId, userId);

        FlashcardDto flashcardDto = flashcardService.getFlashcardById(flashcardId, userId);
        boolean isCorrect = answerValidator.validateReview(flashcardDto, answer);
        userProgressService.updateProgressAfterReview(progress, isCorrect);


        //NOTE dodac wysylanie eventa z powtorka
        return AnswerResultDto.builder()
                .isCorrect(isCorrect)
                .build();
    }

}
