package com.learnwords.deckservice.service.utils;

import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.exception.exceptions.DeckNotFoundException;
import com.learnwords.deckservice.exception.exceptions.InvalidSessionIdException;
import com.learnwords.deckservice.exception.exceptions.SessionNotFoundException;
import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
import com.learnwords.deckservice.repository.DeckEnrollmentRepository;
import com.learnwords.deckservice.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class SessionUtils {
    /**
     * Pobiera sesję jeśli użytkownik ma do niej uprawnienia.
     *
     * <p>Metoda pomocnicza weryfikująca czy użytkownik jest właścicielem sesji.
     *
     * @param sessionId ID sesji
     * @param userId ID użytkownika
     * @return sesja jeśli użytkownik ma uprawnienia
     * @throws InvalidSessionIdException gdy sessionId jest null lub pusty
     * @throws SessionNotFoundException gdy sesja o podanym ID nie istnieje
     * @throws UserPermissionsMissing gdy użytkownik nie ma uprawnień do sesji
     */
    public static Session getSessionIfUserHasPermissions(DeckEnrollmentRepository deckEnrollmentRepository, SessionRepository sessionRepository, String deckId, String userId, String sessionId) {
        if (deckId == null || deckId.isBlank()) {
            log.error("Próba dostępu do sesji z pustym deckId - userId: '{}'", userId);
            throw new InvalidSessionIdException();
        }

        DeckEnrollment deckEnrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(deckEnrollmentRepository,deckId,userId);

        log.debug("Zweryfikowano uprawnienia do talii - deckId: '{}', userId: '{}'", deckId, userId);

        Session session = sessionRepository.findById(sessionId).orElseThrow(
                () -> {
                    log.error("Sesja o ID '{}' nie istnieje - userId: '{}'", sessionId, userId);
                    return new SessionNotFoundException(sessionId);
                }
        );
        log.debug("Zweryfikowano uprawnienia do sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        return session;
    }
}
