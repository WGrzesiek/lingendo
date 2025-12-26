package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.share.*;
import com.learnwords.deckservice.entity.DeckShare;
import com.learnwords.deckservice.enums.ShareTargetType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Serwis do zarządzania udostępnianiem talii.
 */
public interface DeckShareService {

    DeckShareResponse shareDeck(String userId, ShareDeckRequest request);
    BatchShareResponse shareDeckBatch(String userId, BatchShareDeckRequest request);
    DeckShareResponse shareDeckWithAllStudents(String teacherId, String deckId, String message);
    DeckShareResponse shareDeckWithAllFriends(String userId, String deckId, String message);
    DeckShareResponse shareDeckWithGroup(String userId, String deckId, String groupId, String message);
    DeckShareResponse shareDeckWithUser(String userId, String deckId, String targetUserId, String message);
    void revokeDeckShare(String userId, String shareId);
    void revokeAllDeckShares(String userId, String deckId);
    List<DeckShareResponse> getDeckShares(String userId, String deckId);
    Page<DeckShareResponse> getMyShares(String userId, int page, int size);
    Page<SharedDeckDto> getSharedWithMe(String userId, int page, int size);
    boolean hasAccessToDeck(String userId, String deckId);
    List<String> getAccessibleDeckIds(String userId);
    Optional<DeckShare> findActiveShareForUser(String userId, String deckId);
}
