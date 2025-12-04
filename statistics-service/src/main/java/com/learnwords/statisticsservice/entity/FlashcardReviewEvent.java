package com.learnwords.statisticsservice.entity;

import java.time.Instant;

public class FlashcardReviewEvent {

    private String id;
    private String userId;
    private String deckId;
    private String flashcardId;

    private Instant reviewedAt;

    private String userAnswer; // Opcjonalnie
    private boolean isCorrect;

    private int responseTimeMs; // Jak szybko odpowiedział?

    private String difficultyRating; // Np. AGAIN, HARD, GOOD, EASY (dla algorytmu)
}
