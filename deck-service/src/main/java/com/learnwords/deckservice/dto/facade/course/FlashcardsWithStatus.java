package com.learnwords.deckservice.dto.facade.course;

import com.learnwords.deckservice.dto.flashcard.FlashcardDto;
import com.learnwords.deckservice.dto.session.FlashcardSessionNumber;
import com.learnwords.deckservice.dto.userFlashcardProgress.UserFlashcardProgressDto;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record FlashcardsWithStatus(
        FlashcardDto flashcard,
        UserFlashcardProgressDto userFlashcardProgress,
        Integer sessionNumber
) {
}
