package com.learnwords.deckservice.facade;

import com.learnwords.deckservice.dto.facade.review.ReviewCounters;
import com.learnwords.deckservice.dto.facade.review.ReviewHeader;
import com.learnwords.deckservice.service.DeckEnrollmentService;
import com.learnwords.deckservice.service.FlashcardService;
import com.learnwords.deckservice.service.SessionService;
import com.learnwords.deckservice.service.UserProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReviewViewFacade {
    private final UserProgressService userProgressService;
    private final SessionService sessionService;
    private final DeckEnrollmentService deckEnrollmentService;
    private final FlashcardService flashcardService;

    public ReviewViewFacade(UserProgressService userProgressService,
                            SessionService sessionService,
                            DeckEnrollmentService deckEnrollmentService,
                            FlashcardService flashcardService
    ){
        this.userProgressService = userProgressService;
        this.sessionService = sessionService;
        this.deckEnrollmentService = deckEnrollmentService;
        this.flashcardService = flashcardService;
    }

    public ReviewHeader getReviewHeader(String enrollmentId, String userId) {
        ReviewCounters counters = userProgressService.getReviewCounters(enrollmentId, userId);
        return new ReviewHeader(
                enrollmentId,
                counters
        );
    }


}
