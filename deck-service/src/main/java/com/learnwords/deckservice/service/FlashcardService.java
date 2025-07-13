package com.learnwords.deckservice.service;

import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.deckservice.entity.Flashcard;

public interface FlashcardService {

    void processFlashcardCreate(VocabularyDto vocabularyDto);

    void setInitialFlashcardState(String deckId, Flashcard flashcard);

}
