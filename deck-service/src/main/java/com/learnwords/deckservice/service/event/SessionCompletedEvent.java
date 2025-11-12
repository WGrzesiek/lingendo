package com.learnwords.deckservice.service.event;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SessionCompletedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String deckId;
    private String userId;
    private int totalFlashcards;
    private int correctAnswers;
    private int wrongAnswers;
    private int skipped;
    private long durationSeconds;
    private Instant completedAt;
    private Instant startedAt;
}
