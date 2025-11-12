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
public class FlashcardProgressEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String flashcardId;
    private String wordId;
    private String deckId;
    private String userId;
    private boolean isLearned;
    private boolean isSkipped;
    private int correctAnswers;
    private int totalAttempts;
    private double accuracy;
    private Instant updatedAt;
}
