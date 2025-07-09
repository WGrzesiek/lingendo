package com.learnwords.deckservice.entity;


import com.learnwords.deckservice.enums.Language;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.userservice.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Flashcard> flashcards = new HashSet<>();

    @Column(name = "how_many_flashcards_for_one_session")
    @Builder.Default
    private Long howManyFlashcardsForOneSession = 20L;

//    @ElementCollection
//    @CollectionTable(
//            name = "deck_words",
//            joinColumns = @JoinColumn(name = "deck_id")
//    )
//    @Column(name = "word_id", length = 36)
//    private Set<String> wordIds = new HashSet<>();

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @Column(name = "word_count", nullable = false)
    private int wordCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "learn_algorithm", nullable = false)
    private LearnAlgorithm learnAlgorithm;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_from", nullable = false)
    private Language languageFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_to", nullable = false)
    private Language languageTo;

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
