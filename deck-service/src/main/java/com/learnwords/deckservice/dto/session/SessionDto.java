package com.learnwords.deckservice.dto.session;

import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.entity.SessionFlashcard;
import com.learnwords.deckservice.enums.SessionStatus;
import com.learnwords.deckservice.enums.SessionType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record SessionDto(
        //NOTE poprawic bo zwraca za duzo danych, w zasadzie wszytsko co wie sesja
        String id,
        DeckEnrollment enrollment,
        SessionStatus status,
        SessionType type,
        Instant startedAt,
        Instant completedAt,
        List<SessionFlashcard> sessionFlashcards,
        Integer correctAnswers,
        Integer sessionNumber)
{}
