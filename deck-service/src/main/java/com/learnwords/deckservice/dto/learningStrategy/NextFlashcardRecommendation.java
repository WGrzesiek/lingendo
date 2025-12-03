package com.learnwords.deckservice.dto.learningStrategy;

import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.service.learningStrategy.InteractionType;
import lombok.Builder;

import java.util.List;

@Builder
public record NextFlashcardRecommendation(
        String flashcardId,
        WordDto content,
        InteractionType interactionType,
        List<String> quizOptions
) {
}
