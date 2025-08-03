package com.learnwords.deckservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

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
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "word_id", nullable = false, length = 36)
    private String wordId;

    @Column(name = "correct_answers")
    @Builder.Default
    private int correctAnswers = 0;

    @Column(name = "total_attempts")
    @Builder.Default
    private int totalAttempts = 0;

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(name = "is_learned", nullable = false)
    @Builder.Default
    private boolean isLearned = false;

    @Column(name = "is_skipped", nullable = false)
    @Builder.Default
    private boolean isSkipped = false;

    @Column(name = "algorithm_state", columnDefinition = "jsonb", nullable = false)
    @Type(JsonBinaryType.class)
    @Builder.Default
    private String algorithmState = "{}";

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
