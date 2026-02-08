package com.learnwords.deckservice.service.impl;

import com.learnwords.common.events.DeckEnrollmentsCreated;
import com.learnwords.deckservice.dto.deckEnrollment.CreateDeckEnrollmentDto;
import com.learnwords.deckservice.dto.facade.dashboard.StudentMyCourseListItemDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.enums.*;
import com.learnwords.deckservice.repository.DeckEnrollmentRepository;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.repository.SessionRepository;
import com.learnwords.deckservice.service.DeckShareService;
import com.learnwords.deckservice.service.UserProgressService;
import com.learnwords.deckservice.service.event.GenericEventProducer;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Epic("Decks")
@Feature("DeckEnrollmentService")
@DisplayName("DeckEnrollmentServiceImpl - testy jednostkowe")
class DeckEnrollmentServiceImplTest {

    private static final String USER_ID = "user-123";
    private static final String DECK_ID = "deck-1";
    private static final String ENROLLMENT_ID = "enrollment-1";

    @Mock
    private DeckRepository deckRepository;
    @Mock
    private DeckEnrollmentRepository deckEnrollmentRepository;
    @Mock
    private DeckShareService deckShareService;
    @Mock
    private GenericEventProducer eventProducer;
    @Mock
    private UserProgressService userProgressService;
    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private DeckEnrollmentServiceImpl deckEnrollmentService;

    @Test
    @Story("Zapisywanie do talii")
    @DisplayName("Zapis właściciela")
    @Description("Automatyczny zapis właściciela z rolą OWNER")
    @Severity(SeverityLevel.CRITICAL)
    void enrollUserToDeck_shouldEnrollOwner_whenUserIsOwner() {
        Deck deck = Deck.builder()
                .id(DECK_ID)
                .ownerId(USER_ID)
                .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
                .reviewSchedule(ReviewSchedule.AUTO)
                .name("My Deck")
                .build();

        when(deckRepository.getReferenceById(DECK_ID)).thenReturn(deck);
        
        CreateDeckEnrollmentDto dto = CreateDeckEnrollmentDto.builder()
                .howManyFlashcardsForOneSession(10L)
                .preferredAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
                .build();

        deckEnrollmentService.enrollUserToDeck(USER_ID, DECK_ID, dto);

        ArgumentCaptor<DeckEnrollment> captor = ArgumentCaptor.forClass(DeckEnrollment.class);
        verify(deckEnrollmentRepository).save(captor.capture());
        DeckEnrollment saved = captor.getValue();

        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getRole()).isEqualTo(DeckEnrollmentRole.OWNER);
        assertThat(saved.getSource()).isEqualTo(DeckEnrollmentSource.I);
        verify(userProgressService).initializeAllFlashcardsProgressForEnrollment(any(DeckEnrollment.class));
        verify(eventProducer).send(any(), any(DeckEnrollmentsCreated.class));
    }

    @Test
    @Story("Zapisywanie do talii")
    @DisplayName("Zapis do publicznej talii")
    @Description("Zapis użytkownika jako COMMUNITY_OWNER do publicznej talii")
    @Severity(SeverityLevel.CRITICAL)
    void enrollUserToDeck_shouldEnrollStudent_whenDeckIsPublic() {
        Deck deck = Deck.builder()
                .id(DECK_ID)
                .ownerId("other-user")
                .visibility(DeckVisibility.PUBLIC)
                .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
                .reviewSchedule(ReviewSchedule.AUTO)
                .name("Public Deck")
                .build();

        when(deckRepository.getReferenceById(DECK_ID)).thenReturn(deck);

        deckEnrollmentService.enrollUserToDeck(USER_ID, DECK_ID, null);

        ArgumentCaptor<DeckEnrollment> captor = ArgumentCaptor.forClass(DeckEnrollment.class);
        verify(deckEnrollmentRepository).save(captor.capture());
        DeckEnrollment saved = captor.getValue();

        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getRole()).isEqualTo(DeckEnrollmentRole.COMMUNITY_OWNER);
        assertThat(saved.getSource()).isEqualTo(DeckEnrollmentSource.COMMUNITY);
    }

    @Test
    @Story("Zarządzanie nauką")
    @DisplayName("Zmiana algorytmu")
    @Description("Aktualizuje algorytm i resetuje postęp nauki przy zmianie")
    @Severity(SeverityLevel.NORMAL)
    void updateLearnAlgorithm_shouldUpdateAndResetProgress_whenAlgorithmChanged() {
        DeckEnrollment enrollment = DeckEnrollment.builder()
                .id(ENROLLMENT_ID)
                .userId(USER_ID)
                .preferredAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
                .build();

        when(deckEnrollmentRepository.findById(ENROLLMENT_ID)).thenReturn(Optional.of(enrollment));

        deckEnrollmentService.updateLearnAlgorithm(ENROLLMENT_ID, USER_ID, LearnAlgorithm.LEITNER_ALGORITHM);

        assertThat(enrollment.getPreferredAlgorithm()).isEqualTo(LearnAlgorithm.LEITNER_ALGORITHM);
        verify(userProgressService).resetAllProgressForEnrollment(enrollment, LearnAlgorithm.LEITNER_ALGORITHM);
        verify(sessionRepository).deleteByEnrollment_Id(ENROLLMENT_ID);
        verify(deckEnrollmentRepository).save(enrollment);
    }

    @Test
    @Story("Pobieranie zapisów")
    @DisplayName("Pobieranie listy kursów")
    @Description("Zwraca stronicowaną listę kursów studenta")
    @Severity(SeverityLevel.NORMAL)
    void getStudentEnrollments_shouldReturnPage() {
        Deck deck = Deck.builder()
                .id(DECK_ID)
                .name("Deck")
                .wordCount(100)
                .howManyFlashcardsForOneSession(10L)
                .owner(DeckOwner.I)
                .difficulty(DeckDifficulty.MEDIUM)
                .category(DeckCategory.IT)
                .languageFrom(Language.ENGLISH)
                .languageTo(Language.POLISH)
                .build();

        DeckEnrollment enrollment = DeckEnrollment.builder()
                .id(ENROLLMENT_ID)
                .deck(deck)
                .completedSessionsCount(5)
                .lastAccessedAt(Instant.now())
                .build();

        when(deckEnrollmentRepository.findAllByUserId(eq(USER_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(enrollment)));

        Page<StudentMyCourseListItemDto> result = deckEnrollmentService.getStudentEnrollments(USER_ID, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        StudentMyCourseListItemDto item = result.getContent().get(0);
        assertThat(item.deckId()).isEqualTo(DECK_ID);
        assertThat(item.progressPercentage()).isEqualTo(50); // 5 sessions * 10 cards / 100 total = 50%
    }
}
