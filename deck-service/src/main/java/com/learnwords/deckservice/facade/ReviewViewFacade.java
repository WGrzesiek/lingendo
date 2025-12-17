package com.learnwords.deckservice.facade;

import com.learnwords.deckservice.dto.facade.course.FlashcardsWithStatus;
import com.learnwords.deckservice.dto.facade.review.ReviewCounters;
import com.learnwords.deckservice.dto.facade.review.ReviewHeader;
import com.learnwords.deckservice.dto.flashcard.FlashcardDto;
import com.learnwords.deckservice.dto.session.FlashcardSessionNumber;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewViewFacade {
    private final UserProgressService userProgressService;
    private final SessionService sessionService;
    private final DeckEnrollmentService deckEnrollmentService;
    private final FlashcardService flashcardService;
    private final SessionFlashcardService sessionFlashcardService;

    public ReviewViewFacade(UserProgressService userProgressService,
                            SessionService sessionService,
                            DeckEnrollmentService deckEnrollmentService,
                            FlashcardService flashcardService,
                            SessionFlashcardService sessionFlashcardService
    ){
        this.userProgressService = userProgressService;
        this.sessionService = sessionService;
        this.deckEnrollmentService = deckEnrollmentService;
        this.flashcardService = flashcardService;
        this.sessionFlashcardService = sessionFlashcardService;
    }

    public ReviewHeader getReviewHeader(String enrollmentId, String userId) {
        ReviewCounters counters = userProgressService.getReviewCounters(enrollmentId, userId);
        return new ReviewHeader(
                enrollmentId,
                counters
        );
    }

    public Page<FlashcardsWithStatus> getFlashcardsForReviewView(
            String userId,
            String enrollmentId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("repetitionCount").descending());

        Page<UserFlashcardProgressDto> progressPage =
                userProgressService.getReviewProgressForEnrollment(enrollmentId, userId, pageable);

        if(progressPage.isEmpty()) {
            return Page.empty();
        }
        List<UserFlashcardProgressDto> progresses = progressPage.getContent();

        List<String> flashcardIds = progresses.stream()
                .map(UserFlashcardProgressDto::flashcardId)
                .toList();

        List<FlashcardDto> flashcards = flashcardService.getFlashcardsByIds(flashcardIds);
        List<FlashcardSessionNumber> flashcardSessionNumbers =
                sessionFlashcardService.getFlashcardSessionNumbersByIds(flashcardIds);

        var flashcardById = flashcards.stream()
                .collect(Collectors.toMap(FlashcardDto::id, f -> f));

        var sessionNumberByFlashcardId = flashcardSessionNumbers.stream()
                .collect(Collectors.toMap(
                        FlashcardSessionNumber::flashcardId,
                        FlashcardSessionNumber::sessionNumber,
                        Math::max
                ));

        return progressPage.map(progress -> {
            FlashcardDto flashcard = flashcardById.get(progress.flashcardId());
            Integer sessionNumber = sessionNumberByFlashcardId.get(progress.flashcardId());

            return FlashcardsWithStatus.builder()
                    .flashcard(flashcard)
                    .userFlashcardProgress(progress)
                    .sessionNumber(sessionNumber)
                    .build();
        });
    }

}
