package com.learnwords.deckservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "flashcard")
public class Flashcard {
    @Id
    @Column(name = "word_id", nullable = false, length = 36)
    private String wordId;

    @Column(name = "correct_answers")
    private int correctAnswers = 0;

    @Column(name = "total_attempts")
    private int totalAttempts = 0;

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
