package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.dto.session.SessionDto;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.enums.SessionStatus;
import com.learnwords.deckservice.enums.SessionType;
import com.learnwords.deckservice.exception.exceptions.*;
import com.learnwords.deckservice.repository.DeckEnrollmentRepository;
import com.learnwords.deckservice.repository.FlashcardRepository;
import com.learnwords.deckservice.repository.SessionRepository;
import com.learnwords.deckservice.service.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.SessionFlashcardService;
import com.learnwords.deckservice.service.SessionService;
import com.learnwords.deckservice.service.utils.DeckUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
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
    private final FlashcardRepository flashcardRepository;


    public SessionServiceImpl(
            DeckEnrollmentRepository deckEnrollmentRepository,
            SessionRepository sessionRepository, 
            SessionFlashcardService sessionFlashcardService,
            FlashcardRepository flashcardRepository) {
        this.deckEnrollmentRepository = deckEnrollmentRepository;
        this.sessionRepository = sessionRepository;
        this.sessionFlashcardService = sessionFlashcardService;
        this.flashcardRepository = flashcardRepository;
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

        log.debug("Liczba fiszek w sesji - deckId: '{}'", deckId);

        String sessionId = UUID.randomUUID().toString();
        Session session = Session.builder()
                .id(sessionId)
                .enrollment(deckEnrollment)
                .status(SessionStatus.IN_PROGRESS)
                .type(SessionType.LEARNING)
                .startedAt(Instant.now())
                .build();

        sessionRepository.save(session);
        log.debug("Zapisano sesję w bazie - sessionId: '{}'", session.getId());

        sessionFlashcardService.populateSessionWithFlashcards(sessionId, deckId, flashcardFetchStrategy, userId);
        log.info("Pomyślnie zainicjalizowano sesję - sessionId: '{}'",
                session.getId());

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
    public void completeSession(String sessionId, String userId, String deckId) {
        log.info("Ukończanie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        Session session = getSessionIfUserHasPermissions(deckEnrollmentRepository, sessionRepository, deckId, userId, sessionId);

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
    public void abandonSession(String sessionId, String userId, String deckId) {
        log.info("Porzucanie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        Session session = getSessionIfUserHasPermissions(deckEnrollmentRepository, sessionRepository, deckId, userId, sessionId);
        
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
    public void pauseSession(String sessionId, String userId, String deckId) {
        log.info("Wstrzymywanie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        Session session = getSessionIfUserHasPermissions(deckEnrollmentRepository, sessionRepository, deckId, userId, sessionId);
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
    public void resumeSession(String sessionId, String userId, String deckId) {
        log.info("Wznawianie sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        Session session = getSessionIfUserHasPermissions(deckEnrollmentRepository, sessionRepository, deckId, userId, sessionId);
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
    public SessionDto getSessionById(String sessionId, String userId, String deckId) {
        log.debug("Pobieranie szczegółów sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        
        Session session = getSessionIfUserHasPermissions(deckEnrollmentRepository, sessionRepository, deckId, userId, sessionId);
        return SessionDto.builder()
                .id(session.getId())
                .enrollment(session.getEnrollment())
                .status(session.getStatus())
                .type(session.getType())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .sessionFlashcards(session.getSessionFlashcards())
                .build();
    }


}

//    /**
//     * Pobiera wszystkie sesje użytkownika.
//     *
//     * <p>Zwraca listę wszystkich sesji (niezależnie od statusu) utworzonych
//     * przez danego użytkownika, posortowanych według daty utworzenia.
//     *
//     * @param userId ID użytkownika, którego sesje mają zostać pobrane
//     * @return lista sesji użytkownika (może być pusta)
//     * @throws IllegalArgumentException jeśli userId jest null lub pusty
//     */
//    @Override
//    public List<SessionDto> getSessionsByUserId(String userId) {
//        log.debug("Pobieranie sesji użytkownika - userId: '{}'", userId);
//
//        if (userId == null || userId.isBlank()) {
//            log.error("Próba pobrania sesji z pustym userId");
//            throw new IllegalArgumentException("UserId nie może być pusty");
//        }
//
//        List<Session> sessions = sessionRepository.findByUserId(userId);
//        log.info("Znaleziono sesje użytkownika - userId: '{}', count: {}", userId, sessions.size());
//
//        return sessions.stream()
//                .map(SessionDto::from)
//                .toList();
//    }
//
//    /**
//     * Pobiera wszystkie sesje dla talii danego użytkownika.
//     *
//     * <p>Zwraca listę wszystkich sesji (niezależnie od statusu) utworzonych
//     * dla danej talii przez konkretnego użytkownika.
//     *
//     * @param deckId ID talii, której sesje mają zostać pobrane
//     * @param userId ID użytkownika, którego sesje mają zostać pobrane
//     * @return lista sesji talii (może być pusta)
//     * @throws IllegalArgumentException jeśli deckId lub userId są null lub puste
//     */
//    @Override
//    public List<SessionDto> getSessionsByDeckId(String deckId, String userId) {
//        log.debug("Pobieranie sesji talii - deckId: '{}', userId: '{}'", deckId, userId);
//
//        if (deckId == null || deckId.isBlank()) {
//            log.error("Próba pobrania sesji z pustym deckId - userId: '{}'", userId);
//            throw new IllegalArgumentException("DeckId nie może być pusty");
//        }
//
//        List<Session> sessions = sessionRepository.findByDeckIdAndUserId(deckId, userId);
//        log.info("Znaleziono sesje talii - deckId: '{}', userId: '{}', count: {}",
//                deckId, userId, sessions.size());
//
//        return sessions.stream()
//                .map(SessionDto::from)
//                .toList();
//    }
//
//    /**
//     * Pobiera aktywną sesję użytkownika dla talii.
//     *
//     * <p>Wyszukuje sesję w statusie IN_PROGRESS dla danego użytkownika i talii.
//     * Jeśli taka sesja istnieje, użytkownik może ją kontynuować zamiast
//     * tworzyć nową.
//     *
//     * @param userId ID użytkownika
//     * @param deckId ID talii
//     * @return Optional z aktywną sesją jeśli istnieje, pusty Optional w przeciwnym razie
//     * @throws IllegalArgumentException jeśli userId lub deckId są null lub puste
//     */
//    @Override
//    public Optional<SessionDto> getActiveSessionByUserAndDeck(String userId, String deckId) {
//        log.debug("Sprawdzanie aktywnej sesji - userId: '{}', deckId: '{}'", userId, deckId);
//
//        if (userId == null || userId.isBlank() || deckId == null || deckId.isBlank()) {
//            log.error("Próba sprawdzenia aktywnej sesji z pustymi parametrami - userId: '{}', deckId: '{}'",
//                    userId, deckId);
//            throw new IllegalArgumentException("UserId i deckId nie mogą być puste");
//        }
//
//        Optional<Session> activeSession = sessionRepository
//                .findByUserIdAndDeckIdAndStatus(userId, deckId, SessionStatus.IN_PROGRESS);
//
//        if (activeSession.isPresent()) {
//            log.info("Znaleziono aktywną sesję - userId: '{}', deckId: '{}', sessionId: '{}'",
//                    userId, deckId, activeSession.get().getId());
//        } else {
//            log.debug("Brak aktywnej sesji - userId: '{}', deckId: '{}'", userId, deckId);
//        }
//
//        return activeSession.map(SessionDto::from);
//    }
//    /**
//     * Pobiera postęp sesji jako procent ukończenia.
//     *
//     * <p>Oblicza: {@code (correctAnswers + wrongAnswers) / totalFlashcards * 100}
//     * <p>Wynik jest zaokrąglany do dwóch miejsc po przecinku.
//     *
//     * @param sessionId ID sesji
//     * @param userId ID użytkownika pobierającego postęp
//     * @return procent ukończenia (0.0 - 100.0)
//     * @throws InvalidSessionIdException jeśli sessionId jest null lub pusty
//     * @throws SessionNotFoundException jeśli sesja o podanym ID nie istnieje
//     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do sesji
//     */
//    @Override
//    public double getSessionProgress(String sessionId, String userId) {
//        log.debug("Obliczanie postępu sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
//
//        Session session = getSessionIfUserHasPermissions(sessionId, userId);
//        int answered = session.getCorrectAnswers() + session.getWrongAnswers();
//        int total = session.getTotalFlashcards();
//
//        if (total == 0) {
//            log.warn("Sesja nie zawiera fiszek - sessionId: '{}', userId: '{}'", sessionId, userId);
//            return 0.0;
//        }
//
//        double progress = (double) answered / total * 100;
//        progress = Math.round(progress * 100.0) / 100.0;
//
//        log.debug("Postęp sesji - sessionId: '{}', progress: {}%, answered: {}/{}",
//                sessionId, progress, answered, total);
//        return progress;
//    }
//
//    /**
//     * Pobiera talię jeśli użytkownik ma do niej uprawnienia.
//     *
//     * <p>Metoda pomocnicza weryfikująca czy użytkownik jest właścicielem talii
//     * lub ma przynajmniej dostęp do talii publicznej.
//     *
//     * @param deckId ID talii
//     * @param userId ID użytkownika
//     * @return talia jeśli użytkownik ma uprawnienia
//     * @throws IllegalArgumentException gdy userId lub deckId są null lub puste
//     * @throws DeckNotFoundException gdy talia o podanym ID nie istnieje
//     * @throws UserPermissionsMissing gdy użytkownik nie ma uprawnień do talii
//     */
//    private Deck getDeckIfUserHasPermissions(String deckId, String userId) {
//        if (userId == null || userId.isBlank() || deckId == null || deckId.isBlank()) {
//            log.error("Próba dostępu do talii z pustym parametrem - userId: '{}', deckId: '{}'", userId, deckId);
//            throw new IllegalArgumentException("UserId lub DeckId nie może być pusty");
//        }
//
//        Deck deck = deckRepository.findById(deckId)
//                .orElseThrow(() -> {
//                    log.error("Nie znaleziono talii - deckId: '{}', userId: '{}'", deckId, userId);
//                    return new DeckNotFoundException(deckId);
//                });
//
//        if (!deck.getUserId().equals(userId)) {
//            log.warn("Brak uprawnień do talii - userId: '{}', deckId: '{}', deckOwnerId: '{}'",
//                    userId, deckId, deck.getUserId());
//            throw new UserPermissionsMissing("Użytkownik nie ma uprawnień do tej talii");
//        }
//
//        log.debug("Zweryfikowano uprawnienia do talii - deckId: '{}', userId: '{}'", deckId, userId);
//        return deck;
//    }
//
//    /**
//     * Pobiera sesję jeśli użytkownik ma do niej uprawnienia.
//     *
//     * <p>Metoda pomocnicza weryfikująca czy użytkownik jest właścicielem sesji.
//     *
//     * @param sessionId ID sesji
//     * @param userId ID użytkownika
//     * @return sesja jeśli użytkownik ma uprawnienia
//     * @throws InvalidSessionIdException gdy sessionId jest null lub pusty
//     * @throws SessionNotFoundException gdy sesja o podanym ID nie istnieje
//     * @throws UserPermissionsMissing gdy użytkownik nie ma uprawnień do sesji
//     */
//    private Session getSessionIfUserHasPermissions(String sessionId, String userId) {
//        if (sessionId == null || sessionId.isBlank()) {
//            log.error("Próba dostępu do sesji z pustym sessionId - userId: '{}'", userId);
//            throw new InvalidSessionIdException();
//        }
//
//        Session session = sessionRepository.findById(sessionId)
//                .orElseThrow(() -> {
//                    log.error("Nie znaleziono sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
//                    return new SessionNotFoundException(sessionId);
//                });
//
//        if (!session.getUserId().equals(userId)) {
//            log.warn("Brak uprawnień do sesji - userId: '{}', sessionId: '{}', sessionOwnerId: '{}'",
//                    userId, sessionId, session.getUserId());
//            throw new UserPermissionsMissing("Użytkownik nie ma uprawnień do tej sesji");
//        }
//
//        log.debug("Zweryfikowano uprawnienia do sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
//        return session;
//    }
//}
