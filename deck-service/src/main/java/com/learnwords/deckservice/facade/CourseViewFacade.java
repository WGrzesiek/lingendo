package com.learnwords.deckservice.facade;

import com.learnwords.deckservice.dto.course.FlashcardsWithStatus;
import com.learnwords.deckservice.dto.flashcard.FlashcardDto;
import com.learnwords.deckservice.dto.session.FlashcardSessionNumber;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.service.FlashcardService;
import com.learnwords.deckservice.service.SessionFlashcardService;
import com.learnwords.deckservice.service.SessionService;
import com.learnwords.deckservice.service.UserProgressService;
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
public class CourseViewFacade {

    private final UserProgressService userProgressService;
    private final FlashcardService flashcardService;
    private final SessionFlashcardService sessionFlashcardService;

    public CourseViewFacade(UserProgressService userProgressService,
                            FlashcardService flashcardService,
                            SessionFlashcardService sessionFlashcardService){
        this.sessionFlashcardService = sessionFlashcardService;
        this.userProgressService = userProgressService;
        this.flashcardService = flashcardService;
    }

    public Page<FlashcardsWithStatus> getFlashcardsForCourseView(
            String userId,
            String enrollmentId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("isLearned").descending());

        Page<UserFlashcardProgressDto> progressPage =
                userProgressService.getProgressForEnrollment(enrollmentId, userId, pageable);

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




//        return new FlashcardsWithStatus(flashcards, progresses, flashcardSessionNumbers);
//        return new FlashcardsWithStatus.builder()
//                .flashcardDto(flashcards)
//                .userFlashcardProgressDto(progresses)
//                .sessionsNumber(flashcardSessionNumbers)
//                .build();
//    }
}

