package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.DeckVisibility;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.service.DeckAccessService;
import com.learnwords.deckservice.service.DeckShareService;
import com.learnwords.deckservice.service.grpcClient.UserGrcpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementacja serwisu do sprawdzania dostępu do talii.
 * <p>
 * Logika dostępu:
 * 1. Właściciel zawsze ma dostęp
 * 2. Talie PUBLIC są dostępne dla wszystkich
 * 3. Talie PRIVATE są dostępne tylko przez DeckShare
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeckAccessServiceImpl implements DeckAccessService {

    private final UserGrcpClient userGrpcClient;
    private final DeckRepository deckRepository;
    @Lazy
    private final DeckShareService deckShareService;

    @Override
    public boolean canAccessDeck(String userId, String deckId, String deckOwnerId) {
        if (userId.equals(deckOwnerId)) {
            log.debug("Użytkownik {} jest właścicielem talii", userId);
            return true;
        }

        Deck deck = deckRepository.findById(deckId).orElse(null);
        if (deck != null && deck.getVisibility() == DeckVisibility.PUBLIC) {
            log.debug("Talia {} jest publiczna - dostęp dla wszystkich", deckId);
            return true;
        }

        boolean hasShareAccess = deckShareService.hasAccessToDeck(userId, deckId);
        log.debug("Użytkownik {} {} dostęp do talii {} przez DeckShare", 
                userId, hasShareAccess ? "ma" : "nie ma", deckId);
        return hasShareAccess;
    }

    @Override
    public boolean canAccessDeck(String userId, String deckOwnerId) {
        if (userId.equals(deckOwnerId)) {
            log.debug("Użytkownik {} jest właścicielem talii", userId);
            return true;
        }
        log.warn("Wywołano canAccessDeck bez deckId - zaleca się użycie pełnej wersji metody");
        return false;
    }

    @Override
    public boolean canEditDeck(String userId, String deckOwnerId) {
        boolean canEdit = userId.equals(deckOwnerId);
        log.debug("Użytkownik {} {} edytować talię właściciela {}", 
                userId, canEdit ? "może" : "nie może", deckOwnerId);
        return canEdit;
    }

    @Override
    public boolean canViewDeckStats(String userId, String deckOwnerId) {
        if (userId.equals(deckOwnerId)) {
            return true;
        }
        boolean isTeacher = userGrpcClient.isTeacherOf(userId, deckOwnerId);
        log.debug("Użytkownik {} {} nauczycielem właściciela {} - dostęp do statystyk: {}", 
                userId, isTeacher ? "jest" : "nie jest", deckOwnerId, isTeacher);
        return isTeacher;
    }

    @Override
    public List<String> getAccessibleDeckOwners(String userId) {
        List<String> accessibleOwners = new ArrayList<>();

        accessibleOwners.add(userId);
        
        log.debug("Użytkownik {} ma dostęp do talii {} właścicieli (tylko własne - reszta przez DeckShare)", 
                userId, accessibleOwners.size());
        return accessibleOwners;
    }

    @Override
    public List<String> getAccessibleDeckIdsByShare(String userId) {
        List<String> accessibleDeckIds = deckShareService.getAccessibleDeckIds(userId);
        log.debug("Użytkownik {} ma dostęp do {} talii przez udostępnienia", 
                userId, accessibleDeckIds.size());
        return accessibleDeckIds;
    }

    @Override
    public boolean canShareDeckWithStudent(String teacherId, String studentId) {
        boolean isTeacher = userGrpcClient.isTeacherOf(teacherId, studentId);
        log.debug("Nauczyciel {} {} udostępnić talię uczniowi {}", 
                teacherId, isTeacher ? "może" : "nie może", studentId);
        return isTeacher;
    }

    @Override
    public boolean canShareDeckWithGroup(String userId, String groupId) {
        boolean isOwner = userGrpcClient.isGroupOwner(userId, groupId);
        log.debug("Użytkownik {} {} udostępnić talię grupie {} (jest właścicielem: {})", 
                userId, isOwner ? "może" : "nie może", groupId, isOwner);
        return isOwner;
    }

    @Override
    public boolean canCopyDeck(String userId, String deckOwnerId) {
        if (userId.equals(deckOwnerId)) {
            return true;
        }

        return userGrpcClient.hasAccessToUser(userId, deckOwnerId);
    }
}

