package com.learnwords.deckservice.service;

import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.FlashcardDto;
import com.learnwords.deckservice.dto.GetWordFromKafkaDto;
import com.learnwords.deckservice.entity.Flashcard;

import java.util.List;


public interface FlashcardService {

    public void processFlashcardCreateFromKafka(GetWordFromKafkaDto getWordFromKafkaDto);
    public void setInitialFlashcardState(String deckId, Flashcard flashcard);
    public List<FlashcardDto> getAllFlashcardsFromDeck(String deckId);
    public List<FlashcardDto> getFlashcardsFromDeckByFilter(String deckId, boolean isLearned, boolean isSkipped);
    public void updateFlashcard(String flashcardId, WordDto newWord);
    public void resetFlashcardProgress(String flashcardId);
    public void markAsLearned(String flashcardId, boolean learned);

}
