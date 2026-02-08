package com.learnwords.userservice.entity;

import com.learnwords.userservice.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Encja reprezentująca zaproszenie nauczyciela do ucznia.
 * Nauczyciel generuje unikalny kod zaproszenia, który uczeń może wykorzystać do dołączenia.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "teacher_invitation", indexes = {
        @Index(name = "idx_invitation_code", columnList = "invitationCode", unique = true),
        @Index(name = "idx_invitation_teacher", columnList = "teacher_id"),
        @Index(name = "idx_invitation_status", columnList = "status")
})
public class TeacherInvitation {

    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 32)
    private String invitationCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(length = 100)
    private String name;

    @Column
    private Integer maxUses;

    @Column(nullable = false)
    @Builder.Default
    private int currentUses = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.ACTIVE;

    @Column
    private Instant expiresAt;

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

    public boolean isValid() {
        if (status != InvitationStatus.ACTIVE) {
            return false;
        }
        if (expiresAt != null && Instant.now().isAfter(expiresAt)) {
            return false;
        }
        if (maxUses != null && currentUses >= maxUses) {
            return false;
        }
        return true;
    }

    public void incrementUses() {
        this.currentUses++;
        if (maxUses != null && currentUses >= maxUses) {
            this.status = InvitationStatus.EXHAUSTED;
        }
    }
}
