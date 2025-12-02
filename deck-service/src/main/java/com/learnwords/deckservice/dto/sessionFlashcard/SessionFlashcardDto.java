package com.learnwords.deckservice.dto.sessionFlashcard;

import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.Session;
import lombok.Builder;

@Builder
public class SessionFlashcardDto {
    Session session;
    Flashcard flashcard;

}
