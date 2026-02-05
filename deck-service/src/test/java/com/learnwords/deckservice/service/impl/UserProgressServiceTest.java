package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.UserFlashcardProgress;
import com.learnwords.deckservice.enums.*;
import com.learnwords.deckservice.exception.exceptions.DeckNotFoundException;
import com.learnwords.deckservice.repository.DeckEnrollmentRepository;
import com.learnwords.deckservice.repository.FlashcardRepository;
import com.learnwords.deckservice.repository.UserFlashcardProgressRepository;
import com.learnwords.deckservice.service.algorithm.AlgorithmFactory;
import com.learnwords.deckservice.service.algorithm.GrzesiekAlgorithm;
import com.learnwords.deckservice.service.utils.DeckUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import org.mockito.*;


import java.time.Instant;

import java.util.List;
import java.util.Optional;

import static com.learnwords.deckservice.service.utils.DeckUtils.getDeckEnrollmentIfUserHasPermissions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;



@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Epic("Decks")
@Feature("UserProgressService")
@DisplayName("UserProgressServiceImpl - testy jednostkowe")
public class UserProgressServiceTest {

    private static final String USER_ID = "user-123";
    private static final String DECK_ID = "deck-1";
    private static final String ENROLLMENT_ID = "enrollment-1";
    private static final String DECK_NAME = "English Basics";

    @InjectMocks
    UserProgressServiceImpl userProgressService;

    @Mock
    DeckEnrollmentRepository deckEnrollmentRepository;
    @Mock
    FlashcardRepository flashcardRepository;
    @Mock
    UserFlashcardProgressRepository userFlashcardProgressRepository;
    @Mock
    AlgorithmFactory algorithmFactory;

    @Mock
    DeckUtils deckUtils;

    private Flashcard flashcard;
    private Deck deck;
    private DeckEnrollment deckEnrollment;
    private UserFlashcardProgress userFlashcardProgress;
    private UserFlashcardProgressDto userFlashcardProgressDto;

    @BeforeEach
    void setUp() {

        GrzesiekAlgorithm grzesiekAlgorithm = new GrzesiekAlgorithm();
        deck = Deck.builder()
                .id(DECK_ID)
                .name(DECK_NAME)
                .description("Test deck")
                .ownerId(USER_ID)
                .visibility(DeckVisibility.PRIVATE)
                .owner(DeckOwner.I)
                .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
                .reviewSchedule(ReviewSchedule.AUTO)
                .languageFrom(Language.ENGLISH)
                .languageTo(Language.POLISH)
                .category(DeckCategory.IT)
                .difficulty(DeckDifficulty.MEDIUM)
                .wordCount(50)
                .howManyFlashcardsForOneSession(15L)
                .createdAt(Instant.now())
                .build();

        flashcard = Flashcard.builder()
                .id("flashcard-1")
                .wordId("word-1")
                .deck(deck)
                .createdAt(Instant.now())
                .build();

        deckEnrollment = DeckEnrollment.builder()
                .id(ENROLLMENT_ID)
                .userId(USER_ID)
                .deck(deck)
                .role(DeckEnrollmentRole.OWNER)
                .source(DeckEnrollmentSource.I)
                .howManyFlashcardsForOneSession(15L)
                .preferredAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
                .preferredReviewSchedule(deck.getReviewSchedule())
                .joinedAt(Instant.now())
                .build();

        userFlashcardProgress = UserFlashcardProgress.builder()
                .id("user-flashcard-progress-1")
                .flashcard(flashcard)
                .enrollment(deckEnrollment)
                .algorithmState(grzesiekAlgorithm.initialize().serialize())
                .isLearned(false)
                .isSkipped(false)
                .phase(LearningPhase.NEW)
                .repetitionCount(0)
                .userId(deckEnrollment.getUserId())
                .nextReviewAt(null)
                .build();

        userFlashcardProgressDto = UserFlashcardProgressDto.builder()
                .id("user-flashcard-progress-1")
                .flashcardId(flashcard.getId())
                .enrollmentId(ENROLLMENT_ID)
                .userId(USER_ID)
                .phase(LearningPhase.NEW)
                .isLearned(false)
                .isSkipped(false)
                .repetitionCount(0)
                .nextReviewAt(null)
                .algorithmState(grzesiekAlgorithm.initialize().serialize())
                .build();
    }

    @Test
    @Story("Inicjalizacja postępu słówka")
    @DisplayName("Inicjalizuje postęp słówka w talii")
    @Description("Tworzy i zapisuje początkowy stan postępu nauki dla nowego słówka w talii")
    @Severity(SeverityLevel.CRITICAL)
    public void initialFlashcardState_shouldPersist(){
        when(deckEnrollmentRepository.findById(DECK_ID)).thenReturn(Optional.of(deckEnrollment));
        when(algorithmFactory.get(deckEnrollment.getPreferredAlgorithm())).thenReturn(new GrzesiekAlgorithm());
        userProgressService.setInitialFlashcardState(DECK_ID, flashcard, USER_ID);
        ArgumentCaptor<UserFlashcardProgress> captor = ArgumentCaptor.forClass(UserFlashcardProgress.class);
        verify(userFlashcardProgressRepository).save(captor.capture());
        UserFlashcardProgress savedProgress = captor.getValue();
        assertThat(savedProgress.getFlashcard()).isEqualTo(flashcard);
        assertThat(savedProgress.getEnrollment()).isEqualTo(deckEnrollment);
        assertThat(savedProgress.getUserId()).isEqualTo(USER_ID);
        assertThat(savedProgress.getPhase()).isEqualTo(LearningPhase.NEW);
        assertThat(savedProgress.getRepetitionCount()).isEqualTo(0);
        assertThat(savedProgress.isLearned()).isFalse();
        assertThat(savedProgress.isSkipped()).isFalse();
    }

    @Test
    @Story("Inicjalizacja postępu słówka")
    @DisplayName("Rzuca wyjątek gdy brak zapisania do talii")
    @Description("Rzuca DeckNotFoundException gdy użytkownik nie jest zapisany do talii")
    @Severity(SeverityLevel.NORMAL)
    public void initialFlashcardState_shouldThrowException_whenDeckEnrollmentNotFound(){
        when(deckEnrollmentRepository.findById(DECK_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> {
            userProgressService.setInitialFlashcardState(DECK_ID, flashcard, USER_ID);
        }).isInstanceOf(DeckNotFoundException.class);
        verify(userFlashcardProgressRepository, never()).save(any());
    }

    @Test
    @Story("Inicjalizacja postępu słówka")
    @DisplayName("Inicjalizuje postęp nawet gdy już zapisany")
    @Description("Pozwala na ponowną inicjalizację postępu dla istniejącego zapisu")
    @Severity(SeverityLevel.NORMAL)
    public void initialFlashcardState_shouldPresist_whenDeckIdAlreadyEnrolled(){
        when(deckEnrollmentRepository.findById(DECK_ID)).thenReturn(Optional.of(deckEnrollment));
        when(algorithmFactory.get(deckEnrollment.getPreferredAlgorithm())).thenReturn(new GrzesiekAlgorithm());
        userProgressService.setInitialFlashcardState(DECK_ID, flashcard, USER_ID);
        userProgressService.setInitialFlashcardState(DECK_ID, flashcard, USER_ID);
        ArgumentCaptor<UserFlashcardProgress> captor = ArgumentCaptor.forClass(UserFlashcardProgress.class);
        verify(userFlashcardProgressRepository, times(2)).save(captor.capture());
        UserFlashcardProgress savedProgress = captor.getValue();
        assertThat(savedProgress.getFlashcard()).isEqualTo(flashcard);
        assertThat(savedProgress.getEnrollment()).isEqualTo(deckEnrollment);
        assertThat(savedProgress.getUserId()).isEqualTo(USER_ID);
        assertThat(savedProgress.getPhase()).isEqualTo(LearningPhase.NEW);
        assertThat(savedProgress.getRepetitionCount()).isEqualTo(0);
        assertThat(savedProgress.isLearned()).isFalse();
        assertThat(savedProgress.isSkipped()).isFalse();
    }

    @Test
    @Story("Resetowanie postępu")
    @DisplayName("Resetuje cały postęp dla zapisu do talii")
    @Description("Przywraca stan początkowy dla wszystkich słówek w ramach zapisu użytkownika do talii")
    @Severity(SeverityLevel.CRITICAL)
    public void resetAllProgressForEnrollment_shouldDeleteAllUserFlashcardProgresses() {
        when(algorithmFactory.get(deckEnrollment.getPreferredAlgorithm()))
                .thenReturn(new GrzesiekAlgorithm());
        when(userFlashcardProgressRepository.findByEnrollment_Id(deckEnrollment.getId()))
                .thenReturn(List.of(userFlashcardProgress));

        userProgressService.resetAllProgressForEnrollment(deckEnrollment, LearnAlgorithm.GRZESIEK_ALGORITHM);

        ArgumentCaptor<List<UserFlashcardProgress>> captor = ArgumentCaptor.forClass(List.class);
        verify(userFlashcardProgressRepository).saveAll(captor.capture());
        List<UserFlashcardProgress> savedProgresses = captor.getValue();
        assertThat(savedProgresses).hasSize(1);
        UserFlashcardProgress savedProgress = savedProgresses.getFirst();
        assertThat(savedProgress.getAlgorithmState()).isEqualTo(new GrzesiekAlgorithm().getInitialState().serialize());
        assertThat(savedProgress.getPhase()).isEqualTo(LearningPhase.LEARNING);
        assertThat(savedProgress.getRepetitionCount()).isEqualTo(0);
        assertThat(savedProgress.isLearned()).isFalse();
        assertThat(savedProgress.isSkipped()).isFalse();
        assertThat(savedProgress.getNextReviewAt()).isNull();
        assertThat(deckEnrollment.getCompletedSessionsCount()).isEqualTo(0);
        assertThat(deckEnrollment.getTotalLearningTimeSeconds()).isEqualTo(0);
        assertThat(deckEnrollment.getLearnedFlashcardsCount()).isEqualTo(0);
        assertThat(deckEnrollment.getStatus()).isEqualTo(DeckStatus.NOT_STARTED);
    }

    @Test
    @Story("Pobieranie postępu")
    @DisplayName("Pobiera stronicowaną listę postępów")
    @Description("Zwraca listę DTO z postępem nauki dla danego zapisu z uwzględnieniem stronicowania")
    @Severity(SeverityLevel.NORMAL)
    public void getProgressForEnrollment_shouldReturnProgressList(){

        Pageable pageable = PageRequest.of(
                0, 10, Sort.by(Sort.Direction.DESC, "nextReviewAt")
        );


        Page<UserFlashcardProgress> pageUserFlashcardProgress =
                new PageImpl<>(List.of(userFlashcardProgress), pageable, 1);

        when(userFlashcardProgressRepository.findByEnrollment_Id(ENROLLMENT_ID, pageable)).thenReturn(pageUserFlashcardProgress);
        Page<UserFlashcardProgressDto> result =
                userProgressService.getProgressForEnrollment(ENROLLMENT_ID, USER_ID,pageable);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst())
                .usingRecursiveComparison()
                .isEqualTo(userFlashcardProgressDto);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getSort()).isEqualTo(pageable.getSort());
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userFlashcardProgressRepository).findByEnrollment_Id(eq(ENROLLMENT_ID), pageableCaptor.capture());

        Pageable used = pageableCaptor.getValue();
        assertThat(used.getPageNumber()).isEqualTo(0);
        assertThat(used.getPageSize()).isEqualTo(10);
        assertThat(used.getSort()).isEqualTo(pageable.getSort());

    }

    @Test
    @Story("Pobieranie postępu do powtórek")
    @DisplayName("Pobiera słówka do powtórki")
    @Description("Zwraca tylko słówka w fazie powtórki (REVIEW) dla danego zapisu")
    @Severity(SeverityLevel.CRITICAL)
    void getReviewProgressForEnrollment_shouldReturnPagedDtos() {
        // given
        var progress = UserFlashcardProgress.builder()
                .id("user-flashcard-progress-2")
                .flashcard(flashcard)
                .enrollment(deckEnrollment)
                .algorithmState(new GrzesiekAlgorithm().initialize().serialize())
                .isLearned(false)
                .isSkipped(false)
                .phase(LearningPhase.REVIEW)
                .repetitionCount(0)
                .userId(deckEnrollment.getUserId())
                .nextReviewAt(null)
                .build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "nextReviewAt"));

        Page<UserFlashcardProgress> pageFromRepo =
                new PageImpl<>(List.of(progress), pageable, 1);

        when(userFlashcardProgressRepository.findByEnrollment_IdAndPhase(eq(ENROLLMENT_ID), eq(LearningPhase.REVIEW), any(Pageable.class)))
                .thenReturn(pageFromRepo);

        // when
        Page<UserFlashcardProgressDto> result =
                userProgressService.getReviewProgressForEnrollment(ENROLLMENT_ID, USER_ID, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getSort()).isEqualTo(pageable.getSort());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userFlashcardProgressRepository)
                .findByEnrollment_IdAndPhase(eq(ENROLLMENT_ID), eq(LearningPhase.REVIEW), pageableCaptor.capture());

        Pageable used = pageableCaptor.getValue();
        assertThat(used.getPageNumber()).isEqualTo(0);
        assertThat(used.getPageSize()).isEqualTo(10);
        assertThat(used.getSort()).isEqualTo(pageable.getSort());

        var dto = result.getContent().getFirst();
        assertThat(dto.id()).isEqualTo("user-flashcard-progress-2");
        assertThat(dto.flashcardId()).isEqualTo(flashcard.getId());
        assertThat(dto.enrollmentId()).isEqualTo(deckEnrollment.getId());
        assertThat(dto.phase()).isEqualTo(LearningPhase.REVIEW);
    }

}
