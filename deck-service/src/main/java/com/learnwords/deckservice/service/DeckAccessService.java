package com.learnwords.deckservice.service;

import java.util.List;

/**
 * Serwis do sprawdzania dostępu do talii na podstawie relacji użytkowników i udostępnień.
 */
public interface DeckAccessService {

    boolean canAccessDeck(String userId, String deckId, String deckOwnerId);
    boolean canAccessDeck(String userId, String deckOwnerId);
    boolean canEditDeck(String userId, String deckOwnerId);
    boolean canViewDeckStats(String userId, String deckOwnerId);
    List<String> getAccessibleDeckOwners(String userId);
    List<String> getAccessibleDeckIdsByShare(String userId);
    boolean canShareDeckWithStudent(String teacherId, String studentId);
    boolean canShareDeckWithGroup(String userId, String groupId);
    boolean canCopyDeck(String userId, String deckOwnerId);
}
