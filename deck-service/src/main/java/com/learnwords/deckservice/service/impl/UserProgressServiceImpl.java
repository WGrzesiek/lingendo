package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.dto.facade.review.ReviewCounters;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.UserFlashcardProgress;
import com.learnwords.deckservice.enums.DeckStatus;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.enums.LearningPhase;
import com.learnwords.deckservice.repository.DeckEnrollmentRepository;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.repository.FlashcardRepository;
import com.learnwords.deckservice.repository.UserFlashcardProgressRepository;
import com.learnwords.deckservice.service.UserProgressService;
import com.learnwords.deckservice.service.algorithm.AbstractAlgorithm;
import com.learnwords.deckservice.service.algorithm.Algorithm;
import com.learnwords.deckservice.service.algorithm.AlgorithmFactory;
import com.learnwords.deckservice.service.algorithm.state.AlgorithmState;
import com.learnwords.deckservice.service.evaluationService.responseResult.AlgorithmResult;
import com.learnwords.deckservice.service.evaluationService.responseResult.MaxLevel;
import com.learnwords.deckservice.service.evaluationService.responseResult.Success;
import com.learnwords.deckservice.service.utils.DeckUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.learnwords.deckservice.enums.LearnAlgorithm.GRZESIEK_ALGORITHM;
import static com.learnwords.deckservice.service.utils.DeckUtils.checkDeckEnrollmentIsExistsAndUserHasPermissions;
import static com.learnwords.deckservice.service.utils.DeckUtils.getDeckEnrollmentIfUserHasPermissions;

@Slf4j
@Service
public class UserProgressServiceImpl implements UserProgressService {
    private final DeckEnrollmentRepository deckEnrollmentRepository;
    private final FlashcardRepository flashcardRepository;
    private final UserFlashcardProgressRepository userFlashcardProgressRepository;
    private final AlgorithmFactory algorithmFactory;

    public UserProgressServiceImpl(DeckEnrollmentRepository deckEnrollmentRepository, FlashcardRepository flashcardRepository, UserFlashcardProgressRepository userFlashcardProgressRepository, AlgorithmFactory algorithmFactory) {
        this.deckEnrollmentRepository = deckEnrollmentRepository;
        this.flashcardRepository = flashcardRepository;
        this.userFlashcardProgressRepository = userFlashcardProgressRepository;
        this.algorithmFactory = algorithmFactory;
    }

    @Override
    public void setInitialFlashcardState(String deckId, Flashcard flashcard, String userId) {
        log.debug("Inicjalizacja stanu fiszki - flashcardId: '{}', deckId: '{}', userId: '{}'",
                flashcard.getId(), deckId, userId);
        DeckEnrollment enrollment = getDeckEnrollmentIfUserHasPermissions(deckEnrollmentRepository,deckId,userId);
        AbstractAlgorithm algorithm = algorithmFactory.get(enrollment.getPreferredAlgorithm());

        UserFlashcardProgress userFlashcardProgress =
                UserFlashcardProgress.builder()
                .flashcard(flashcard)
                .enrollment(enrollment)
                .algorithmState(algorithm.initialize().serialize())
                .isLearned(false)
                .isSkipped(false)
                .phase(LearningPhase.NEW)
                .repetitionCount(0)
                .userId(enrollment.getUserId())
                .build();
        userFlashcardProgressRepository.save(userFlashcardProgress);
        log.debug("Ustawiono początkowy stan algorytmu nauki - flashcardId: '{}', algorithm: '{}'",
                flashcard.getId(), algorithm);
    }

    /**
     * Resetuje postęp nauki dla wszystkich fiszek w danym enrollmencie.
     * Używane przy zmianie algorytmu nauki.
     * Resetuje również statystyki enrollment (sesje, czas nauki, nauczone fiszki).
     *
     * @param enrollment enrollment do zresetowania
     * @param newAlgorithm nowy algorytm, którego stan początkowy zostanie ustawiony
     */
    @Override
    @Transactional
    public void resetAllProgressForEnrollment(DeckEnrollment enrollment, LearnAlgorithm newAlgorithm) {
        log.info("Resetowanie postępu nauki dla enrollmentId: {}, nowy algorytm: {}", 
                enrollment.getId(), newAlgorithm);
        
        AbstractAlgorithm algorithm = algorithmFactory.get(newAlgorithm);
        AlgorithmState initialState = algorithm.initialize();
        String initialStateJson = initialState.serialize();
        
        List<UserFlashcardProgress> progressList = userFlashcardProgressRepository.findByEnrollment_Id(enrollment.getId());
        
        for (UserFlashcardProgress progress : progressList) {
            progress.setAlgorithmState(initialStateJson);
            progress.setLearned(false);
            progress.setSkipped(false);
            progress.setRepetitionCount(0);
            progress.setPhase(LearningPhase.LEARNING);
            progress.setNextReviewAt(null);
        }
        
        userFlashcardProgressRepository.saveAll(progressList);
        
        // Reset statystyk enrollment
        enrollment.setCompletedSessionsCount(0);
        enrollment.setTotalLearningTimeSeconds(0);
        enrollment.setLearnedFlashcardsCount(0);
        enrollment.setStatus(DeckStatus.NOT_STARTED);
        deckEnrollmentRepository.save(enrollment);
        
        log.info("Zresetowano postęp nauki dla {} fiszek oraz statystyki enrollment: {}", 
                progressList.size(), enrollment.getId());
    }

    /**
     * @param flashcardId
     * @param userId
     */
    @Override
    public void resetFlashcardProgress(String flashcardId, String userId) {

    }

    /**
     * @param flashcardId
     * @param learned
     * @param userId
     */
    @Override
    public void markAsLearned(String flashcardId, boolean learned, String userId) {

    }

    /**
     * @param flashcardId
     * @param skipped
     * @param userId
     */
    @Override
    public void markAsSkipped(String flashcardId, boolean skipped, String userId) {

    }

    /**
     * @param deckId
     * @param userId
     * @return
     */
    @Override
    public List<UserFlashcardProgressDto> getProgressForDeck(String deckId, String userId) {
        log.debug("Pobieranie progresu fiszek dla talii - deckId: '{}', userId: '{}'", deckId, userId);
        DeckEnrollment enrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(
                deckEnrollmentRepository, deckId, userId);
        List<UserFlashcardProgress> progresses =
                userFlashcardProgressRepository.findByEnrollment_Id(enrollment.getId());
        return progresses.stream()
                .map(progress -> UserFlashcardProgressDto.builder()
                        .id(progress.getId())
                        .flashcardId(progress.getFlashcard().getId())
                        .enrollmentId(progress.getEnrollment().getId())
                        .userId(progress.getUserId())
                        .phase(progress.getPhase())
                        .isLearned(progress.isLearned())
                        .isSkipped(progress.isSkipped())
                        .repetitionCount(progress.getRepetitionCount())
                        .nextReviewAt(progress.getNextReviewAt())
                        .algorithmState(progress.getAlgorithmState())
                        .build())
                .toList();
    }

    /**
     * @param enrollmentId
     * @param limit
     * @return
     */
    @Override
    public List<UserFlashcardProgressDto> getDueFlashcards(String enrollmentId, int limit) {
        return List.of();
    }

    /**
     * @param deckId
     * @param flashcardIds
     * @param userId
     */
    @Override
    @Transactional
    public void initializeSessionFlashcardsState(String deckId, List<String> flashcardIds, String userId) {
        log.info("Inicjalizacja stanu fiszek dla sesji - deckId: {}, userId: {}, liczba fiszek: {}",
                deckId, userId, flashcardIds != null ? flashcardIds.size() : 0);

        if (flashcardIds == null || flashcardIds.isEmpty()) {
            log.warn("initializeSessionFlashcardsState wywołane z pustą listą flashcardIds (deckId: {}, userId: {})",
                    deckId, userId);
            return;
        }

        DeckEnrollment enrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(
                deckEnrollmentRepository, deckId, userId);

        AbstractAlgorithm algorithm = algorithmFactory.get(enrollment.getPreferredAlgorithm());

        List<UserFlashcardProgress> existingProgresses =
                userFlashcardProgressRepository.findByEnrollment_Id(enrollment.getId());

        Set<String> flashcardIdsWithProgress = existingProgresses.stream()
                .map(p -> p.getFlashcard().getId())
                .collect(Collectors.toSet());


        List<String> idsToInitialize = flashcardIds.stream()
                .filter(id -> !flashcardIdsWithProgress.contains(id))
                .toList();

        if (idsToInitialize.isEmpty()) {
            log.info("Wszystkie {} fiszek w talii {} mają już stan progresu. Nic nie inicjalizuję.",
                    flashcardIds.size(), deckId);
            return;
        }

        List<Flashcard> flashcardsToInitialize = flashcardRepository.findAllById(idsToInitialize);

        if (flashcardsToInitialize.isEmpty()) {
            log.warn("Żadna z fiszek {} nie została znaleziona w bazie (deckId: {})", idsToInitialize, deckId);
            return;
        }

        List<UserFlashcardProgress> newProgresses = flashcardsToInitialize.stream()
                .map(flashcard -> UserFlashcardProgress.builder()
                        .flashcard(flashcard)
                        .enrollment(enrollment)
                        .algorithmState(algorithm.initialize().serialize())
                        .isLearned(false)
                        .isSkipped(false)
                        .phase(LearningPhase.NEW)
                        .repetitionCount(0)
                        .userId(enrollment.getUserId())
                        .build()
                )
                .toList();

        userFlashcardProgressRepository.saveAll(newProgresses);

        log.info("Zainicjalizowano progres dla {} nowych fiszek w talii {} (userId: {})",
                newProgresses.size(), deckId, userId);
    }

    @Override
    public void initializeDeckFlashcardsState(String deckId, String userId){
        log.debug("Inicjalizacja stanu fiszek w talii - deckId: '{}', userId: '{}'", deckId, userId);

        checkDeckEnrollmentIsExistsAndUserHasPermissions(deckEnrollmentRepository, deckId, userId);
        List<Flashcard> flashcards = flashcardRepository.findByDeckId(deckId);

        for (Flashcard flashcard : flashcards) {
            setInitialFlashcardState(deckId, flashcard, userId);
        }

        log.info("Zainicjalizowano stan algorytmu nauki dla fiszek w talii - deckId: '{}', flashcardCount: {}",
                deckId, flashcards.size());
    }

    @Override
    @Transactional
    public void initializeAllFlashcardsProgressForEnrollment(DeckEnrollment enrollment) {
        String deckId = enrollment.getDeck().getId();
        String userId = enrollment.getUserId();
        String enrollmentId = enrollment.getId();
        
        log.info("Inicjalizacja progressu dla wszystkich fiszek - enrollmentId: {}, deckId: {}, userId: {}",
                enrollmentId, deckId, userId);

        List<UserFlashcardProgress> existingProgresses =
                userFlashcardProgressRepository.findByEnrollment_Id(enrollmentId);

        Set<String> flashcardIdsWithProgress = existingProgresses.stream()
                .map(p -> p.getFlashcard().getId())
                .collect(Collectors.toSet());

        List<Flashcard> allFlashcards = flashcardRepository.findByDeckId(deckId);

        List<Flashcard> flashcardsToInitialize = allFlashcards.stream()
                .filter(f -> !flashcardIdsWithProgress.contains(f.getId()))
                .toList();

        if (flashcardsToInitialize.isEmpty()) {
            log.info("Wszystkie {} fiszek w talii {} mają już progres. Nic nie inicjalizuję.",
                    allFlashcards.size(), deckId);
            return;
        }

        AbstractAlgorithm algorithm = algorithmFactory.get(enrollment.getPreferredAlgorithm());

        List<UserFlashcardProgress> newProgresses = flashcardsToInitialize.stream()
                .map(flashcard -> UserFlashcardProgress.builder()
                        .flashcard(flashcard)
                        .enrollment(enrollment)
                        .algorithmState(algorithm.initialize().serialize())
                        .isLearned(false)
                        .isSkipped(false)
                        .phase(LearningPhase.NEW)
                        .repetitionCount(0)
                        .userId(userId)
                        .build())
                .toList();

        userFlashcardProgressRepository.saveAll(newProgresses);

        log.info("Zainicjalizowano progres dla {} fiszek w enrollmentId {} (deckId: {}, userId: {})",
                newProgresses.size(), enrollmentId, deckId, userId);
    }

    /**
     * @param flashcardId
     * @param userId
     * @return
     */
    @Override
    public UserFlashcardProgressDto getFlashcardProgress(String flashcardId, String userId) {
        UserFlashcardProgress progress = userFlashcardProgressRepository
                .findByFlashcard_IdAndUserId(flashcardId, userId)
                .orElseThrow(() -> new RuntimeException("Progress not found for flashcardId: " + flashcardId + ", userId: " + userId));
        return UserFlashcardProgressDto.builder()
                .id(progress.getId())
                .flashcardId(progress.getFlashcard().getId())
                .enrollmentId(progress.getEnrollment().getId())
                .userId(progress.getUserId())
                .phase(progress.getPhase())
                .isLearned(progress.isLearned())
                .isSkipped(progress.isSkipped())
                .repetitionCount(progress.getRepetitionCount())
                .nextReviewAt(progress.getNextReviewAt())
                .algorithmState(progress.getAlgorithmState())
                .lastShownAt(progress.getLastShownAt())
                .build();
    }

    @Override
    @Transactional
    public void updateProgress(UserFlashcardProgressDto progressDto, AlgorithmResult result, boolean isCorrect) {
        UserFlashcardProgress entity = userFlashcardProgressRepository.findById(progressDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Progress not found"));

        entity.setUpdatedAt(Instant.now());
        if(result instanceof Success<?>){
            AlgorithmState newState = ((Success<?>) result).newState();
            if(newState.getStep().isMaxLevel() ){
                entity.setLearned(true);
                entity.setPhase(LearningPhase.REVIEW);
                entity.setNextReviewAt(Instant.now().plusSeconds(604800));
                entity.setUpdatedAt(Instant.now());
            }
            entity.setAlgorithmState(newState.serialize());
            entity.setUpdatedAt(Instant.now());

        } else if (result instanceof MaxLevel) {
            entity.setLearned(true);
            entity.setPhase(LearningPhase.REVIEW);
            entity.setNextReviewAt(Instant.now().plusSeconds(604800));
            entity.setUpdatedAt(Instant.now());
        } else {

            entity.setUpdatedAt(Instant.now());
        }

        userFlashcardProgressRepository.save(entity);
    }

    @Override
    public void updateProgressAfterReview(UserFlashcardProgressDto progressDto, boolean isCorrect) {
        UserFlashcardProgress entity = userFlashcardProgressRepository.findById(progressDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Progress not found"));
        entity.setUpdatedAt(Instant.now());
        if (isCorrect && entity.getRepetitionCount() < 3) {
            entity.setRepetitionCount(entity.getRepetitionCount() + 1);
            switch (entity.getEnrollment().getPreferredReviewSchedule()){
                case AUTO -> {
                    switch (entity.getRepetitionCount()){
                        case 1 -> entity.setNextReviewAt(Instant.now().plusSeconds(604800));
                        case 2 -> entity.setNextReviewAt(Instant.now().plusSeconds(1209600));
                        default -> entity.setNextReviewAt(Instant.now().plusSeconds(1814400));
                    }
                }
                case NORMAL -> {
                    switch (entity.getRepetitionCount()){
                        case 1 -> entity.setNextReviewAt(Instant.now().plusSeconds(604800));
                        case 2 -> entity.setNextReviewAt(Instant.now().plusSeconds(1209600));
                        default -> entity.setNextReviewAt(Instant.now().plusSeconds(2592000));
                    }
                }
                case LIGHT -> {
                    switch (entity.getRepetitionCount()){
                        case 1 -> entity.setNextReviewAt(Instant.now().plusSeconds(259200));
                        case 2 -> entity.setNextReviewAt(Instant.now().plusSeconds(604800));
                        default -> entity.setNextReviewAt(Instant.now().plusSeconds(864000));
                    }
                }
                case INTENSE -> {
                    switch (entity.getRepetitionCount()){
                        case 1 -> entity.setNextReviewAt(Instant.now().plusSeconds(86400));
                        case 2 -> entity.setNextReviewAt(Instant.now().plusSeconds(259200));
                        default -> entity.setNextReviewAt(Instant.now().plusSeconds(604800));
                    }
                }
            }

        }
        userFlashcardProgressRepository.save(entity);

    }

    @Override
    public Page<UserFlashcardProgressDto> getProgressForEnrollment(
            String enrollmentId,
            String userId,
            Pageable pageable
    ) {
        if (userId == null || userId.isBlank()) {
            log.error("UserId jest pusty");
            throw new IllegalArgumentException("UserId nie może być pusty");
        }

        Page<UserFlashcardProgress> progresses =
                userFlashcardProgressRepository.findByEnrollment_Id(enrollmentId, pageable);

        return progresses.map(progress -> UserFlashcardProgressDto.builder()
                .id(progress.getId())
                .flashcardId(progress.getFlashcard().getId())
                .enrollmentId(progress.getEnrollment().getId())
                .userId(progress.getUserId())
                .phase(progress.getPhase())
                .isLearned(progress.isLearned())
                .isSkipped(progress.isSkipped())
                .repetitionCount(progress.getRepetitionCount())
                .nextReviewAt(progress.getNextReviewAt())
                .algorithmState(progress.getAlgorithmState())
                .build()
        );
    }

    @Override
    public Page<UserFlashcardProgressDto> getReviewProgressForEnrollment(
            String enrollmentId,
            String userId,
            Pageable pageable
    ) {
        if (userId == null || userId.isBlank()) {
            log.error("UserId jest pusty");
            throw new IllegalArgumentException("UserId nie może być pusty");
        }

        Page<UserFlashcardProgress> progresses =
                userFlashcardProgressRepository.findByEnrollment_IdAndPhase(enrollmentId, LearningPhase.REVIEW, pageable);

        return progresses.map(progress -> UserFlashcardProgressDto.builder()
                .id(progress.getId())
                .flashcardId(progress.getFlashcard().getId())
                .enrollmentId(progress.getEnrollment().getId())
                .userId(progress.getUserId())
                .phase(progress.getPhase())
                .isLearned(progress.isLearned())
                .isSkipped(progress.isSkipped())
                .repetitionCount(progress.getRepetitionCount())
                .nextReviewAt(progress.getNextReviewAt())
                .algorithmState(progress.getAlgorithmState())
                .build()
        );
    }

    @Override
    public int countWordsToReview(String enrollmentId, String userId) {
        log.debug("Pobieranie liczby słów do powtórki - enrollmentId: '{}', userId: '{}'", enrollmentId, userId);
        return userFlashcardProgressRepository.countToReview(enrollmentId,userId,LearningPhase.REVIEW, 3);
    }

    @Override
    public ReviewCounters getReviewCounters(String enrollmentId, String userId) {
        log.debug("Pobieranie liczby słów do powtórki na dziś - enrollmentId: '{}', userId: '{}'", enrollmentId, userId);
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        Instant startOfTomorrow = LocalDate.now(zone).plusDays(1).atStartOfDay(zone).toInstant();
        Instant now = Instant.now();

        return userFlashcardProgressRepository.countReviewStats(
                enrollmentId,
                userId,
                LearningPhase.REVIEW,
                now,
                startOfTomorrow
                );

    }

    @Override
    public Instant getNextNearReviewDate(String enrollmentId, String userId) {
        log.debug("Pobieranie najbliższej daty powtórki - enrollmentId: '{}', userId: '{}'", enrollmentId, userId);
        return userFlashcardProgressRepository.findNextReviewAt(
                enrollmentId,
                userId,
                Instant.now(),
                3
        ).orElse(null);
    }

    @Override
    public void updateLastShownAt(String id) {
        log.debug("Aktualizacja lastShownAt dla progresu fiszki - id: '{}'", id);
        UserFlashcardProgress entity = userFlashcardProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Progress not found"));
        Instant now = Instant.now();
        entity.setLastShownAt(now);
        entity.setUpdatedAt(now);
        userFlashcardProgressRepository.save(entity);
    }


}
