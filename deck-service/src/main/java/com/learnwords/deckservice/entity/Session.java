package com.learnwords.deckservice.entity;

import com.learnwords.deckservice.enums.SessionStatus;
import com.learnwords.deckservice.enums.SessionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name = "session")
public class Session {
    @Id
    @Column(nullable = false, unique = true, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionFlashcard> sessionFlashcards;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "total_flashcards")
    @Builder.Default
    private int totalFlashcards = 0;

    @Column(name = "correct_answers")
    @Builder.Default
    private int correctAnswers = 0;

    @Column(name = "wrong_answers")
    @Builder.Default
    private int wrongAnswers = 0;

    @Column(name = "skipped")
    @Builder.Default
    private int skipped = 0;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SessionStatus status = SessionStatus.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SessionType type;

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
