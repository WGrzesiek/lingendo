package com.learnwords.deckservice.entity;


import com.learnwords.deckservice.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "deck")
public class Deck {
    @Id
    @Column(nullable = false, unique = true, length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = true, length = 255)
    private String description;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @OneToMany(mappedBy = "deck",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true  )
    private Set<Flashcard> flashcards = new HashSet<>();

    @OneToMany(mappedBy = "deck", fetch = FetchType.LAZY)
    private Set<Session> sessions = new HashSet<>();

    @Column(name = "how_many_flashcards_for_one_session")
    @Builder.Default
    private Long howManyFlashcardsForOneSession = 20L;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @Column(name = "word_count", nullable = false)
    @Builder.Default
    private int wordCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "learn_algorithm", nullable = false)
    private LearnAlgorithm learnAlgorithm;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_from", nullable = false)
    private Language languageFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_to", nullable = false)
    private Language languageTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner", nullable = false)
    private DeckOwner owner;

    @Column(name = "last_accessed")
    private Instant lastAccessed;

    @Column(name = "total_session")
    private Long totalSessions;

    @Column(name = "session_completed")
    private Long sessionCompleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private DeckDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeckStatus status;

    @Formula("(100 * session_completed) / NULLIF(total_session, 0)")
    private Integer completionPercent;

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
