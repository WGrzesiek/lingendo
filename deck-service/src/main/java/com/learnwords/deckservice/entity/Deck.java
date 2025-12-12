package com.learnwords.deckservice.entity;

import com.learnwords.deckservice.enums.*;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "owner_id", nullable = false, length = 36)
    private String ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DeckVisibility visibility = DeckVisibility.PRIVATE;

    @Column(name = "word_count", nullable = false)
    @Builder.Default
    private int wordCount = 0;

    @OneToMany(mappedBy = "deck",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true  )
    private Set<Flashcard> flashcards = new HashSet<>();

    @Column(name = "how_many_flashcards_for_one_session", nullable = false)
    @Builder.Default
    private Long howManyFlashcardsForOneSession = 20L;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_from", nullable = false)
    private Language languageFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_to", nullable = false)
    private Language languageTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private DeckDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 100)
    private DeckCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name="learn_algorithm", nullable = false)
    private LearnAlgorithm learnAlgorithm;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner", nullable = false)
    private DeckOwner owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_schedule", nullable = false)
    private ReviewSchedule reviewSchedule = ReviewSchedule.AUTO;

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