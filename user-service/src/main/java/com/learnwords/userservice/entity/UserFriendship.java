package com.learnwords.userservice.entity;

import com.learnwords.userservice.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "user_friendship",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_friendship", columnNames = {"user_id_1", "user_id_2"})
        },
        indexes = {
                @Index(name = "idx_user_friendship_user1", columnList = "user_id_1"),
                @Index(name = "idx_user_friendship_user2", columnList = "user_id_2"),
                @Index(name = "idx_user_friendship_status", columnList = "status")
        })
public class UserFriendship {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id_1", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id_2", nullable = false)
    private User user2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FriendshipStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by_user_id", nullable = false)
    private User requestedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
