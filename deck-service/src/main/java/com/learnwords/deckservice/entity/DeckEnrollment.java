package com.learnwords.deckservice.entity;

import com.learnwords.deckservice.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "deck_enrollment")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DeckEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeckEnrollmentRole role;

    @Enumerated(EnumType.STRING)
    private DeckEnrollmentSource source;

    @Column(name = "how_many_flashcards_for_one_session")
    @Builder.Default
    private Long howManyFlashcardsForOneSession = 20L;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_algorithm")
    private LearnAlgorithm preferredAlgorithm;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DeckStatus status = DeckStatus.NOT_STARTED;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "completed_sessions_count")
    @Builder.Default
    private int completedSessionsCount = 0;

    @Column(name = "total_learning_time_seconds")
    @Builder.Default
    private long totalLearningTimeSeconds = 0;

    @Column(name = "learned_flashcards_count")
    @Builder.Default
    private int learnedFlashcardsCount = 0;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        updatedAt = createdAt;
        if (joinedAt == null)
            joinedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public void incrementCompletedSessions(long durationSeconds) {
        this.completedSessionsCount++;
        this.totalLearningTimeSeconds += durationSeconds;
        this.lastAccessedAt = Instant.now();
    }
}