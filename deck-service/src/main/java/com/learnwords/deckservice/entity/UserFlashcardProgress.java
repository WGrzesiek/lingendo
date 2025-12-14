package com.learnwords.deckservice.entity;

import com.learnwords.deckservice.enums.LearningPhase;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.time.Instant;

@Entity
@Table(name = "user_flashcard_progress")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserFlashcardProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private DeckEnrollment enrollment;

    @Column(name = "is_learned")
    @Builder.Default
    private boolean isLearned = false;

    @Column(name = "is_skipped", nullable = false)
    @Builder.Default
    private boolean isSkipped = false;

    @Column(name = "repetition_count")
    @Builder.Default
    private int repetitionCount = 0;

    @Column(name = "algorithm_state", columnDefinition = "jsonb", nullable = false)
    @Type(JsonBinaryType.class)
    @Builder.Default
    private String algorithmState = "{}";

    @Column(name = "next_review_at")
    private Instant nextReviewAt;

    @Column(name = "learning_phase", nullable = false)
    @Enumerated(EnumType.STRING)
    private LearningPhase phase;

    @Column(name = "last_shown_at")
    private Instant lastShownAt;

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