package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.CreateDeckDto;

public interface DeckService {
    public void createDeck(String userId, CreateDeckDto createDeckDto);
    public void deleteDeck(String deckId);

}
