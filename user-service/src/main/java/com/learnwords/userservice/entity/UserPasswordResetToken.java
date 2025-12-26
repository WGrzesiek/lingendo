package com.learnwords.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "user_password_reset_token")
public class UserPasswordResetToken {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
