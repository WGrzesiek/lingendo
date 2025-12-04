package com.learnwords.statisticsservice.entity;

import java.time.Instant;
import java.time.LocalDate;

// To jest reprezentacja tabeli w ClickHouse
// Engine = MergeTree() ORDER BY (user_id, date)
public class StudySessionEvent {

    private String id;
    private String userId;
    private String deckId;
    private String enrollmentId;

    private Instant startTime;
    private Instant endTime;
    private Long durationSeconds;

    private int cardsReviewedCount;
    private int correctAnswersCount;
    private int wrongAnswersCount;

    private int xpEarned; // Grywalizacja

    private LocalDate date; // Do partycjonowania w ClickHouse
}
