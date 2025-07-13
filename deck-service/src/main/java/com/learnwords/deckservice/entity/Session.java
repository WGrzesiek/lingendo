package com.learnwords.deckservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
@Getter
public class Session {
    // deck id -> id do decka
    // session id -> unikalny identyfikator sesji
    // czy sesja jest zakończona -> czy sesja została zakończona
    // czas rozpoczęcia sesji -> kiedy sesja została rozpoczęta
    // czas zakończenia sesji -> kiedy sesja została zakończona

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
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
