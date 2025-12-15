package com.learnwords.deckservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.DeckEnrollmentsFinished;
import com.learnwords.common.events.SessionFinishedEvent;
import com.learnwords.common.events.SessionStartedEvent;
import com.learnwords.deckservice.dto.facade.learn.SessionInfo;
import com.learnwords.deckservice.dto.session.SessionDto;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.enums.SessionStatus;
import com.learnwords.deckservice.enums.SessionType;
import com.learnwords.deckservice.exception.exceptions.*;
import com.learnwords.deckservice.repository.DeckEnrollmentRepository;
import com.learnwords.deckservice.repository.SessionRepository;
import com.learnwords.deckservice.service.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.SessionFlashcardService;
import com.learnwords.deckservice.service.SessionService;
import com.learnwords.deckservice.service.algorithm.AbstractAlgorithm;
import com.learnwords.deckservice.service.algorithm.AlgorithmFactory;
import com.learnwords.deckservice.service.event.GenericEventProducer;
import com.learnwords.deckservice.service.utils.DeckUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.learnwords.deckservice.service.utils.SessionUtils.getSessionIfUserHasPermissions;

/**
 * Implementacja serwisu zarządzania sesjami nauki.
 * 
 * <p>Zarządza pełnym cyklem życia sesji od inicjalizacji przez rejestrację
 * odpowiedzi do ukończenia lub porzucenia sesji.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 */
@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final DeckEnrollmentRepository deckEnrollmentRepository;
    private final SessionRepository sessionRepository;
    private final SessionFlashcardService sessionFlashcardService;
    private final AlgorithmFactory algorithmFactory;
    private final GenericEventProducer eventProducer;


    public SessionServiceImpl(
            DeckEnrollmentRepository deckEnrollmentRepository,
            SessionRepository sessionRepository,
            SessionFlashcardService sessionFlashcardService,
            AlgorithmFactory algorithmFactory,
            GenericEventProducer eventProducer) {
        this.deckEnrollmentRepository = deckEnrollmentRepository;
        this.sessionRepository = sessionRepository;
        this.sessionFlashcardService = sessionFlashcardService;
        this.algorithmFactory = algorithmFactory;
        this.eventProducer = eventProducer;
    }

    /**
     * Inicjalizuje nową sesję nauki dla talii.
     * 
     * <p>Proces inicjalizacji:
     * <ol>
     *   <li>Waliduje parametry wejściowe</li>
     *   <li>Weryfikuje uprawnienia użytkownika do talii</li>
     *   <li>Pobiera liczbę fiszek dostępnych w sesji</li>
     *   <li>Tworzy nową sesję w statusie IN_PROGRESS</li>
     *   <li>Zapisuje sesję w bazie danych</li>
     *   <li>Dodaje fiszki do sesji według wybranej strategii</li>
     * </ol>
     * 
     * @param deckId ID talii, dla której inicjalizowana jest sesja
     * @param flashcardFetchStrategy strategia wyboru fiszek do sesji
     * @param userId ID użytkownika inicjalizującego sesję
     * @return ID utworzonej sesji
     * @throws IllegalArgumentException jeśli deckId lub userId są null lub puste
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do talii
     */
    @Override
    @Transactional
    public String initializeSession(String deckId, FlashcardFetchStrategy flashcardFetchStrategy ,SessionType type, String userId) {
        log.info("Inicjalizacja sesji - deckId: '{}', userId: '{}', strategy: '{}'",
                deckId, userId, flashcardFetchStrategy.getClass().getSimpleName());

        DeckEnrollment deckEnrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(deckEnrollmentRepository, deckId, userId);
        AbstractAlgorithm algorithm = algorithmFactory.get(deckEnrollment.getPreferredAlgorithm());
        int sessionNumber = sessionRepository.countByEnrollment_Id(deckEnrollment.getId()) + 1;
        log.debug("Liczba fiszek w sesji - deckId: '{}'", deckId);

        String sessionId = UUID.randomUUID().toString();
        Session session = Session.builder()
                .id(sessionId)
                .enrollment(deckEnrollment)
                .status(SessionStatus.IN_PROGRESS)
                .type(SessionType.LEARNING)
                .startedAt(Instant.now())
                .sessionNumber(sessionNumber)
                .build();

        sessionRepository.save(session);
        log.debug("Zapisano sesję w bazie - sessionId: '{}'", session.getId());

        sessionFlashcardService.populateSessionWithFlashcards(sessionId, deckId, flashcardFetchStrategy, userId);
        log.info("Pomyślnie zainicjalizowano sesję - sessionId: '{}'",
                session.getId());
        SessionStartedEvent event = SessionStartedEvent.builder()
                .eventTime(session.getCreatedAt())
                .sessionId(sessionId)
                .userId(userId)
                .deckId(deckEnrollment.getDeck().getId())
                .deckName(deckEnrollment.getDeck().getName())
                .deckEnrollmentId(deckEnrollment.getId())
                .receivedAt(Instant.now())
                .build();
        eventProducer.send(KafkaTopic.SESSION_STARTED, event);
        return session.getId();
    }

    /**
     * Ukończa sesję nauki.
     * 
     * <p>Proces ukończenia:
     * <ol>
     *   <li>Waliduje parametry wejściowe</li>
     *   <li>Weryfikuje uprawnienia użytkownika do sesji</li>
     *   <li>Sprawdza czy sesja jest w statusie IN_PROGRESS</li>
     *   <li>Zmienia status na COMPLETED</li>
     *   <li>Ustawia completedAt na aktualny czas</li>
     *   <li>Oblicza i zapisuje czas trwania sesji (durationSeconds)</li>
     *   <li>Zapisuje zmiany w bazie danych</li>
     * </ol>
     * 
     * @param sessionId ID sesji do ukończenia
     * @param userId ID użytkownika kończącego sesję
     * @throws InvalidSessionIdException jeśli sessionId jest null lub pusty
     * @throws SessionNotFoundException jeśli sesja o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do sesji
     * @throws SessionNotActiveException jeśli sesja nie jest w statusie IN_PROGRESS
     */
    @Override
    @Transactional
    public void completeSession(String sessionId, String userId) {
        log.info("Ukończanie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        Session session = getSessionIfUserHasPermissions(sessionRepository, userId, sessionId);

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            log.error("Próba ukończenia nieaktywnej sesji - sessionId: '{}', currentStatus: '{}', userId: '{}'", 
                    sessionId, session.getStatus(), userId);
            throw new SessionNotActiveException(sessionId, session.getStatus().toString());
        }
        Instant now = Instant.now();
        session.setCompletedAt(now);
        session.setStatus(SessionStatus.COMPLETED);
        
        sessionRepository.save(session);
        log.info("Ukończono sesję - sessionId: '{}'", sessionId);
        DeckEnrollment enrollment = session.getEnrollment();
        long duration = Duration.between(session.getStartedAt(), now).getSeconds();
        enrollment.incrementCompletedSessions(duration);

        SessionFinishedEvent eventS = SessionFinishedEvent.builder()
                .eventTime(session.getCompletedAt())
                .sessionId(sessionId)
                .userId(userId)
                .deckId(enrollment.getDeck().getId())
                .deckName(enrollment.getDeck().getName())
                .deckEnrollmentId(enrollment.getId())
                .receivedAt(Instant.now())
                .build();
        eventProducer.send(KafkaTopic.SESSION_FINISHED, eventS);
        boolean hasActiveSessions = sessionRepository.existsByEnrollment_IdAndStatusIn(
                enrollment.getId(),
                List.of(SessionStatus.IN_PROGRESS, SessionStatus.PAUSED)
        );

        if (!hasActiveSessions) {
            enrollment.markCompleted();
            log.info("Ukończono naukę talii - deckEnrollmentId: '{}'", enrollment.getId());

            DeckEnrollmentsFinished enrollmentFinishedEvent = DeckEnrollmentsFinished.builder()
                    .eventTime(session.getCompletedAt())
                    .deckEnrollmentId(enrollment.getId())
                    .deckId(enrollment.getDeck().getId())
                    .deckName(enrollment.getDeck().getName())
                    .userId(userId)
                    .receivedAt(Instant.now())
                    .build();

            eventProducer.send(KafkaTopic.DECK_ENROLLMENT_FINISHED, enrollmentFinishedEvent);
        }

        deckEnrollmentRepository.save(enrollment);
    }

    /**
     * Porzuca sesję nauki przed ukończeniem.
     * 
     * <p>Zmienia status sesji na ABANDONED. Wszystkie dotychczasowe statystyki
     * (poprawne i błędne odpowiedzi) są zachowane, ale sesja nie jest liczona
     * jako pomyślnie ukończona.
     * 
     * @param sessionId ID sesji do porzucenia
     * @param userId ID użytkownika porzucającego sesję
     * @throws InvalidSessionIdException jeśli sessionId jest null lub pusty
     * @throws SessionNotFoundException jeśli sesja o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do sesji
     * @throws SessionNotActiveException jeśli sesja nie jest w statusie IN_PROGRESS
     */
    @Override
    @Transactional
    public void abandonSession(String sessionId, String userId) {
        log.info("Porzucanie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        Session session = getSessionIfUserHasPermissions(sessionRepository, userId, sessionId);
        
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            log.error("Próba porzucenia nieaktywnej sesji - sessionId: '{}', currentStatus: '{}', userId: '{}'", 
                    sessionId, session.getStatus(), userId);
            throw new SessionNotActiveException(sessionId, session.getStatus().toString());
        }
        
        session.setStatus(SessionStatus.ABANDONED);
        session.setCompletedAt(Instant.now());
        
        sessionRepository.save(session);
        log.info("Porzucono sesję - sessionId: '{}'", sessionId);
    }

    /**
     * Wstrzymuje aktywną sesję nauki.
     *
     * 
     * @param sessionId ID sesji do wstrzymania
     * @param userId ID użytkownika wstrzymującego sesję
     * @throws InvalidSessionIdException jeśli sessionId jest null lub pusty
     * @throws SessionNotFoundException jeśli sesja o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do sesji
     */
    @Override
    @Transactional
    public void pauseSession(String sessionId, String userId) {
        log.info("Wstrzymywanie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        Session session = getSessionIfUserHasPermissions(sessionRepository, userId, sessionId);
        SessionStatus previousStatus = session.getStatus();
        
        session.setStatus(SessionStatus.PAUSED);
        sessionRepository.save(session);
        
        log.info("Wstrzymano sesję - sessionId: '{}', previousStatus: '{}'", sessionId, previousStatus);
    }

    /**
     * Wznawia wstrzymaną sesję nauki.
     * 
     * <p>Zmienia status sesji z PAUSED z powrotem na IN_PROGRESS,
     * umożliwiając kontynuację nauki.
     * 
     * @param sessionId ID sesji do wznowienia
     * @param userId ID użytkownika wznawiającego sesję
     * @throws InvalidSessionIdException jeśli sessionId jest null lub pusty
     * @throws SessionNotFoundException jeśli sesja o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do sesji
     */
    @Override
    @Transactional
    public void resumeSession(String sessionId, String userId) {
        log.info("Wznawianie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        Session session = getSessionIfUserHasPermissions(sessionRepository, userId, sessionId);
        SessionStatus previousStatus = session.getStatus();
        
        session.setStatus(SessionStatus.IN_PROGRESS);
        sessionRepository.save(session);
        
        log.info("Wznowiono sesję - sessionId: '{}', previousStatus: '{}'", sessionId, previousStatus);
    }

    /**
     * Pobiera szczegółowe informacje o sesji.
     * 
     * @param sessionId ID sesji
     * @param userId ID użytkownika wykonującego operację
     * @return DTO ze szczegółowymi danymi sesji
     * @throws InvalidSessionIdException jeśli sessionId jest null lub pusty
     * @throws SessionNotFoundException jeśli sesja nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do sesji
     */
    @Override
    public SessionDto getSessionById(String sessionId, String userId) {
        log.debug("Pobieranie szczegółów sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        Session session = getSessionIfUserHasPermissions(sessionRepository, userId, sessionId);
        return SessionDto.builder()
                .id(session.getId())
                .enrollment(session.getEnrollment())
                .status(session.getStatus())
                .type(session.getType())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .sessionFlashcards(session.getSessionFlashcards())
                .correctAnswers(session.getCorrectAnswers())
                .sessionNumber(session.getSessionNumber())
                .build();
    }

    @Override
    public int getCompletedSessionsCount(String deckId, String userId){
        log.debug("Pobieranie liczby ukończonych sesji - deckId: '{}', userId: '{}'", deckId, userId);
        DeckEnrollment enrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(
                deckEnrollmentRepository, deckId, userId);
        int completedSessions = sessionRepository.countByEnrollment_IdAndStatus(
                enrollment.getId(), SessionStatus.COMPLETED);
        log.debug("Liczba ukończonych sesji - deckId: '{}', userId: '{}', completedSessions: '{}'",
                deckId, userId, completedSessions);
        return completedSessions;
    }

    @Override
    public List<SessionInfo> getSessionsInfoByDeckId(String enrollmentId, String userId) {
        log.debug("Pobieranie informacji o sesjach - enrollmentId: '{}', userId: '{}'", enrollmentId, userId);
        DeckEnrollment enrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(
                deckEnrollmentRepository, enrollmentId, userId);
        List<Session> sessions = sessionRepository.findByEnrollment_Id(enrollment.getId());
        List<SessionInfo> sessionInfos = sessions.stream()
                .map(session -> new SessionInfo(
                        session.getId(),
                        session.getSessionNumber(),
                        session.getStatus()
                ))
                .toList();
        log.debug("Pobrano informacje o sesjach - enrollmentId: '{}', userId: '{}', sessionCount: '{}'",
                enrollmentId, userId, sessionInfos.size());
        return sessionInfos;
    }

    @Override
    public void recordCorrectAnswer(String sessionId, String userId) {
        log.debug("Rejestrowanie poprawnej odpowiedzi - sessionId: '{}', userId: '{}'", sessionId, userId);
        Session session = getSessionIfUserHasPermissions(sessionRepository, userId, sessionId);
        session.setCorrectAnswers(session.getCorrectAnswers()+1);
        sessionRepository.save(session);
        log.debug("Zarejestrowano poprawną odpowiedź - sessionId: '{}', userId: '{}', totalCorrectAnswers: '{}'",
                sessionId, userId, session.getCorrectAnswers());
    }

}
