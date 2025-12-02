package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.UserFlashcardProgress;
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
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.learnwords.deckservice.enums.LearnAlgorithm.GRZESIEK_ALGORITHM;
import static com.learnwords.deckservice.service.utils.DeckUtils.checkDeckEnrollmentIsExistsAndUserHasPermissions;
import static com.learnwords.deckservice.service.utils.DeckUtils.getDeckEnrollmentIfUserHasPermissions;

@Slf4j
@Service
public class UserProgressServiceImpl implements UserProgressService {
    private final DeckRepository deckRepository;
    private final DeckEnrollmentRepository deckEnrollmentRepository;
    private final FlashcardRepository flashcardRepository;
    private final UserFlashcardProgressRepository userFlashcardProgressRepository;
    private final Algorithm algorithm;
    private final AlgorithmFactory algorithmFactory;

    public UserProgressServiceImpl(DeckRepository deckRepository, DeckEnrollmentRepository deckEnrollmentRepository, FlashcardRepository flashcardRepository, UserFlashcardProgressRepository userFlashcardProgressRepository, Algorithm algorithm, AlgorithmFactory algorithmFactory) {
        this.deckRepository = deckRepository;
        this.deckEnrollmentRepository = deckEnrollmentRepository;
        this.flashcardRepository = flashcardRepository;
        this.userFlashcardProgressRepository = userFlashcardProgressRepository;
        this.algorithm = algorithm;
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
        log.debug("Ustawiono początkowy stan algorytmu nauki - flashcardId: '{}', algorithm: '{}'",
                flashcard.getId(), algorithm);
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
        return List.of();
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
    public void initializeSessionFlashcardsState(String deckId, List<String> flashcardIds, String userId) {

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
            entity.setAlgorithmState(newState.serialize());

        }

        if (result instanceof MaxLevel) {
            entity.setLearned(true);
            entity.setNextReviewAt(null);
        } else {

            entity.setLearned(false);
        }

        userFlashcardProgressRepository.save(entity);
    }
}
