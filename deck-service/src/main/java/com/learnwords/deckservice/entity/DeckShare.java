package com.learnwords.deckservice.entity;

import com.learnwords.deckservice.enums.ShareStatus;
import com.learnwords.deckservice.enums.ShareTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Encja reprezentująca udostępnienie talii.
 * Talia może być udostępniona:
 * - konkretnej grupie uczniów (GROUP)
 * - wszystkim uczniom nauczyciela (ALL_STUDENTS)
 * - wszystkim znajomym (ALL_FRIENDS)
 * - konkretnemu użytkownikowi (USER)
 */
@Entity
@Table(name = "deck_share",
        indexes = {
                @Index(name = "idx_deck_share_deck_id", columnList = "deck_id"),
                @Index(name = "idx_deck_share_owner_id", columnList = "owner_id"),
                @Index(name = "idx_deck_share_target", columnList = "target_type, target_id"),
                @Index(name = "idx_deck_share_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_deck_share_target",
                        columnNames = {"deck_id", "target_type", "target_id"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckShare {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(name = "owner_id", nullable = false, length = 36)
    private String ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private ShareTargetType targetType;

    @Column(name = "target_id", length = 36)
    private String targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ShareStatus status = ShareStatus.ACTIVE;

    @Column(length = 255)
    private String message;

    @Column(name = "shared_at", nullable = false)
    private Instant sharedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = createdAt;
        if (sharedAt == null) {
            sharedAt = now;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public boolean isActive() {
        if (status != ShareStatus.ACTIVE) {
            return false;
        }
        if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
            return false;
        }
        return true;
    }

    public void revoke() {
        this.status = ShareStatus.REVOKED;
        this.revokedAt = Instant.now();
    }
}
