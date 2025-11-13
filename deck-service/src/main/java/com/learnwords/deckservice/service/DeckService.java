package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.*;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.LearnAlgorithm;

import java.util.List;

public interface DeckService {
    void createDeck(String userId, CreateDeckDto createDeckDto);
    void deleteDeck(String deckId, String userId);
    String renameDeck(String deckId, String newName, String userId);
    boolean changeDeckVisibility(String deckId, String userId, boolean isPublic);
    DeckOwner changeDeckOwner(String deckId, String userId, DeckOwner newOwner);
    DeckDto getDeckById(String deckId, String userId);
    List<DeckDto> getDecksByFilter(String userId, Boolean isPublic, DeckOwner owner);
    default List<DeckDto> getDecksByFilter(String userId, DeckOwner owner) {
        return getDecksByFilter(userId, null, owner);
    }
    default List<DeckDto> getDecksByFilter(String userId) {
        return getDecksByFilter(userId, null, null);
    }
    List<DeckDto> getPublicDecks();
    DeckDetailsDto getDeckDetailsById(String deckId, String userId);
    DeckDetailsDto editDeckDetails(String deckId, DeckDetailsDto deckDetailsDto, String userId);
    long getTotalFlashcardsCount(String deckId, String userId);
    String updateLearnAlgorithm(String deckId, LearnAlgorithm algorithm, String userId);
    Long updateFlashcardsPerSession(String deckId, Long count, String userId);
    UserDeckCountDto getUserDeckCount(String userId);
    DeckStatisticsDto getDeckStatistics(String deckId, String userId);
    boolean isDeckNameTaken(String userId, String deckName);
}
