package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.*;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.LearnAlgorithm;

import java.util.List;

public interface DeckService {
    public boolean createDeck(String userId, CreateDeckDto createDeckDto);
    public boolean deleteDeck(String deckId);
    public String renameDeck(String deckId, String newName);
    public boolean changeDeckVisibility(String deckId, boolean isPublic);
    public DeckOwner changeDeckOwner(String deckId, DeckOwner newOwner);
    public DeckDto getDeckById(String deckId);
    List<DeckDto> getDecksByFilter(String userId, Boolean isPublic, DeckOwner owner);
    default List<DeckDto> getDecksByFilter(String userId, DeckOwner owner) {
        return getDecksByFilter(userId, null,  owner);
    }
    default List<DeckDto> getDecksByFilter(String userId) {
        return getDecksByFilter(userId, null, null);
    }
    default List<DeckDto> getDecksByFilter(boolean isPublic) {
        return getDecksByFilter(null, isPublic, null);
    }
    public DeckDetailsDto getDeckDetailsById(String deckId);
    public DeckDetailsDto editDeckDetails(String deckId, DeckDetailsDto deckDetailsDto);
    public long getTotalFlashcardsCount(String deckId);
    public void updateLearnAlgorithm(String deckId, LearnAlgorithm algorithm);
    public void updateFlashcardsPerSession(String deckId, Long count);
    public UserDeckCountDto getUserDeckCount(String userId);
    public DeckStatisticsDto getDeckStatistics(String deckId);
    public boolean isDeckNameTaken(String userId, String deckName);

}
