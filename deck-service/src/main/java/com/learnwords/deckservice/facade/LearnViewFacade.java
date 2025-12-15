package com.learnwords.deckservice.facade;

import com.learnwords.deckservice.dto.facade.learn.LearnHeader;
import com.learnwords.deckservice.dto.session.SessionDto;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.service.DeckEnrollmentService;
import com.learnwords.deckservice.service.SessionService;
import com.learnwords.deckservice.service.UserProgressService;
import com.learnwords.deckservice.service.algorithm.AbstractAlgorithm;
import com.learnwords.deckservice.service.algorithm.AlgorithmFactory;
import com.learnwords.deckservice.service.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LearnViewFacade {
    private final DeckEnrollmentService deckEnrollmentService;
    private final SessionService sessionService;
    private final AlgorithmFactory algorithmFactory;
    private final UserProgressService userProgressService;

    public LearnViewFacade(DeckEnrollmentService deckEnrollmentService, SessionService sessionService, AlgorithmFactory algorithmFactory, UserProgressService userProgressService) {
        this.userProgressService = userProgressService;
        this.algorithmFactory = algorithmFactory;
        this.deckEnrollmentService = deckEnrollmentService;
        this.sessionService = sessionService;
    }
    public LearnHeader getLearnHeader(String userId, String sessionId) {
        SessionDto session = sessionService.getSessionById(sessionId, userId);

        DeckEnrollment enrollment = session.enrollment();
        AbstractAlgorithm algorithm = algorithmFactory.get(enrollment.getPreferredAlgorithm());

        int totalSteps = algorithm.getInitialState().getTotalSteps();
        int correct = session.correctAnswers();

        if (totalSteps <= 0 || correct <= 0) {
            return new LearnHeader(sessionId, 0.0);
        }
        double percent = (correct * 100.0) / totalSteps;
        percent = Math.min(100.0, Math.max(0.0, percent));
        return new LearnHeader(sessionId, percent);
    }

}
