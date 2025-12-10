package com.learnwords.deckservice.facade;

import com.learnwords.auth.v1.GetUserNameByIdResponse;
import com.learnwords.deckservice.dto.course.CourseHeaderInfo;
import com.learnwords.deckservice.dto.course.FlashcardsWithStatus;
import com.learnwords.deckservice.dto.deckEnrollment.DeckEnrollmentDto;
import com.learnwords.deckservice.dto.flashcard.FlashcardDto;
import com.learnwords.deckservice.dto.session.FlashcardSessionNumber;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.service.*;
import com.learnwords.deckservice.service.grpcClient.UserGrcpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
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
    private final DeckService deckService;
    private final UserGrcpClient userGrcpClient;
    private final DeckEnrollmentService deckEnrollmentService;

    public CourseViewFacade(UserProgressService userProgressService,
                            FlashcardService flashcardService,
                            SessionFlashcardService sessionFlashcardService,
                            DeckService deckService,
                            UserGrcpClient userGrcpClient,
                            DeckEnrollmentService deckEnrollmentService

    ){
        this.sessionFlashcardService = sessionFlashcardService;
        this.userProgressService = userProgressService;
        this.flashcardService = flashcardService;
        this.deckService = deckService;
        this.userGrcpClient = userGrcpClient;
        this.deckEnrollmentService = deckEnrollmentService;
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

    public CourseHeaderInfo getCourseHeaderInfo(String enrollmentId, String userId) {
        DeckEnrollmentDto enrollment = deckEnrollmentService.getEnrollment(enrollmentId, userId);
        String deckId = enrollment.getDeckId();
        Deck deck = deckService.getDeckById(deckId);
        GetUserNameByIdResponse userResponse = userGrcpClient.getUserNameById(deck.getOwnerId());
        return CourseHeaderInfo.builder()
                .deckId(deckId)
                .name(deck.getName())
                .description(deck.getDescription())
                .ownerId(deck.getOwnerId())
                .username(userResponse.getUsername())
                .ownerType(deck.getOwner())
                .visibility(deck.getVisibility())
                .languageFrom(deck.getLanguageFrom().name())
                .languageTo(deck.getLanguageTo().name())
                .build();
    }
}

