package com.learnwords.deckservice.dto.flashcard;

import com.learnwords.common.dto.WordDto;

import java.time.Instant;

public record FlashcardDto(
        String id,
        WordDto wordDto
) {}
