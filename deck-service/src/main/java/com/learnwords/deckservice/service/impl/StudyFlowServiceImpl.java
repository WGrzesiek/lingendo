package com.learnwords.deckservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.FlashcardAnsweredEvent;
import com.learnwords.deckservice.dto.evaluationService.AnswerResultDto;
import com.learnwords.deckservice.dto.flashcard.FlashcardDto;
import com.learnwords.deckservice.dto.session.SessionDto;
import com.learnwords.deckservice.dto.learningStrategy.NextFlashcardRecommendation;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.entity.SessionFlashcard;
import com.learnwords.deckservice.exception.exceptions.SessionFinishedException;
import com.learnwords.deckservice.service.*;
import com.learnwords.deckservice.service.algorithm.AlgorithmFactory;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.evaluationService.AnswerValidator;
import com.learnwords.deckservice.service.evaluationService.UserAnswer;
import com.learnwords.deckservice.service.evaluationService.responseResult.AlgorithmResult;
import com.learnwords.deckservice.service.event.GenericEventProducer;
import com.learnwords.deckservice.service.learningStrategy.LearningStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
@Slf4j
@Service
public class StudyFlowServiceImpl implements StudyFlowService {
    private final List<LearningStrategy> strategies;
    private final SessionService sessionService;
    private final SessionFlashcardService sessionFlashcardService;
    private final UserProgressService userProgressService;
    private final AlgorithmFactory algorithmFactory;
    private final AnswerValidator answerValidator;
    private final GenericEventProducer eventProducer;
    private final FlashcardService flashcardService;

    public StudyFlowServiceImpl(List<LearningStrategy> strategies,
                                SessionService sessionService,
                                SessionFlashcardService sessionFlashcardService,
                                UserProgressService userProgressService,
                                AlgorithmFactory algorithmFactory,
                                AnswerValidator answerValidator,
                                GenericEventProducer eventProducer,
                                FlashcardService flashcardService) {
        this.strategies = strategies;
        this.sessionService = sessionService;
        this.sessionFlashcardService = sessionFlashcardService;
        this.userProgressService = userProgressService;
        this.algorithmFactory = algorithmFactory;
        this.answerValidator = answerValidator;
        this.eventProducer = eventProducer;
        this.flashcardService = flashcardService;
    }

    @Override
    public NextFlashcardRecommendation getNextFlashcard(String sessionId, String userId) {
        SessionDto session = sessionService.getSessionById(sessionId, userId);
        DeckEnrollment deckEnrollment = session.enrollment();
        List<SessionFlashcard> sessionFlashcards = sessionFlashcardService.getSessionFlashcards(sessionId);

        LearningStrategy strategy = strategies.stream()
                .filter(s -> s.supports(deckEnrollment.getPreferredAlgorithm()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No strategy found for algorithm: " + deckEnrollment.getPreferredAlgorithm()));

        return strategy.recommendNext(sessionFlashcards, userId)
                .orElseThrow(() -> new SessionFinishedException("No more flashcards to learn in this session"));
    }

    @Override
    public AnswerResultDto submitAnswer(String sessionId, String flashcardId, UserAnswer userAnswer, String userId) {
        UserFlashcardProgressDto progress = userProgressService.getFlashcardProgress(flashcardId, userId);
        SessionDto session = sessionService.getSessionById(sessionId, userId);

        FlashcardDto flashcardDto = flashcardService.getFlashcardById(flashcardId, userId);

        var algorithm = algorithmFactory.get(session.enrollment().getPreferredAlgorithm());
        var currentState = algorithm.deserialize(progress.algorithmState());


        boolean isCorrect = answerValidator.validate(flashcardDto, userAnswer, currentState.getStep());
        AlgorithmResult<AlgorithmState> result = algorithm.processAnswer(currentState, isCorrect);
        userProgressService.updateProgress(progress, result, isCorrect);
        long responseTimeMs = Duration.between(progress.lastShownAt(), Instant.now()).toMillis();



        FlashcardAnsweredEvent event = FlashcardAnsweredEvent.builder()
                .eventTime(Instant.now())
                .userId(userId)
                .deckEnrollmentId(session.enrollment().getId())
                .sessionId(sessionId)
                .flashcardId(flashcardId)
                .correct(isCorrect)
                .receivedAt(Instant.now())
                .timeTaken(Duration.ofMillis(responseTimeMs))
                .build();
        eventProducer.send(KafkaTopic.FLASHCARD_ANSWERED, event);

        return new AnswerResultDto(
                isCorrect,
                result
        );
    }
}

