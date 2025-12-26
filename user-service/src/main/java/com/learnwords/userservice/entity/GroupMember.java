package com.learnwords.userservice.entity;

import com.learnwords.userservice.enums.GroupMemberStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Encja reprezentująca członkostwo ucznia w grupie.
 * Łączy ucznia z grupą nauczyciela.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "group_member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_group_student", columnNames = {"group_id", "student_id"})
        },
        indexes = {
                @Index(name = "idx_group_member_group_id", columnList = "group_id"),
                @Index(name = "idx_group_member_student_id", columnList = "student_id"),
                @Index(name = "idx_group_member_status", columnList = "status")
        })
public class GroupMember {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private StudentGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private GroupMemberStatus status = GroupMemberStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private Instant joinedAt;

    @Column
    private Instant removedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_id")
    private User addedBy;

    @PrePersist
    public void prePersist() {
        if (joinedAt == null) {
            joinedAt = Instant.now();
        }
    }
}
