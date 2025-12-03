package com.learnwords.deckservice.dto.sessionFlashcard;

import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.Session;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class SessionFlashcardDto {
    private String sessionId;
    private Map<List<String>, List<WordDto>> flashcardWithWords;

}
