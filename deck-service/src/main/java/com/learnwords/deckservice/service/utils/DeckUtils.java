package com.learnwords.deckservice.service.utils;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.exception.exceptions.DeckNotFoundException;
import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
import com.learnwords.deckservice.repository.DeckEnrollmentRepository;
import com.learnwords.deckservice.repository.DeckRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeckUtils {
    public static void checkDeckEnrollmentIsExistsAndUserHasPermissions(DeckEnrollmentRepository deckEnrollmentRepository, String deckId, String userId) {
        if (userId == null || userId.isBlank() || deckId == null || deckId.isBlank()) {
            log.error("Próba sprawdzenia uprawnień z pustym parametrem - userId: '{}', deckId: '{}'", userId, deckId);
            throw new IllegalArgumentException("UserId lub DeckId nie może być pusty");
        }
        DeckEnrollment deckEnrollment = deckEnrollmentRepository.findById(deckId)
                .orElseThrow(() -> {
                    log.error("Nie znaleziono talii - deckId: '{}'", deckId);
                    return new DeckNotFoundException(deckId);
                });

        if (!deckEnrollment.getUserId().equals(userId)) {
            log.warn("Brak uprawnień do talii - userId: '{}', deckId: '{}', deckOwnerId: '{}'",
                    userId, deckId, deckEnrollment.getUserId());
            throw new UserPermissionsMissing("Użytkownik nie ma uprawnień do tej talii");
        }
    }
    public static DeckEnrollment getDeckEnrollmentIfUserHasPermissions(DeckEnrollmentRepository deckEnrollmentRepository,String deckId, String userId) {
        if (userId == null || userId.isBlank() || deckId == null || deckId.isBlank()) {
            log.error("UserId lub DeckId nie może być pusty");
            throw new IllegalArgumentException("UserId lub DeckId nie może być pusty");
        }
        DeckEnrollment deckEnrollment = deckEnrollmentRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(
                        "Talia o ID '%s' nie istnieje".formatted(deckId)
                ));

        if (!deckEnrollment.getUserId().equals(userId)) {
            log.warn("Użytkownik '{}' nie ma uprawnień do talii o ID '{}'", userId, deckId);
            throw new UserPermissionsMissing("Użytkownik nie ma uprawnień do tej talii");
        }
        return deckEnrollment;
    }





    /**
     * Pobiera talię jeśli użytkownik ma do niej uprawnienia
     *
     * @param deckId ID talii
     * @param userId ID użytkownika
     * @return Deck jeśli użytkownik ma uprawnienia, wyjątek w przeciwnym razie
     * @throws IllegalArgumentException gdy userId lub deckId są puste
     * @throws DeckNotFoundException gdy talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing gdy użytkownik nie ma uprawnień do talii
     */
    public static Deck getDeckIfUserHasPermissions(DeckRepository deckRepository,String deckId, String userId) {
        if (userId == null || userId.isBlank() || deckId == null || deckId.isBlank()) {
            log.error("UserId lub DeckId nie może być pusty");
            throw new IllegalArgumentException("UserId lub DeckId nie może być pusty");
        }
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(
                        "Talia o ID '%s' nie istnieje".formatted(deckId)
                ));

        if (!deck.getOwnerId().equals(userId)) {
            log.warn("Użytkownik '{}' nie ma uprawnień do talii o ID '{}'", userId, deckId);
            throw new UserPermissionsMissing("Użytkownik nie ma uprawnień do tej talii");
        }
        return deck;
    }
}
