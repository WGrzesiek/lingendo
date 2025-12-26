package com.learnwords.userservice.entity;

import com.learnwords.userservice.enums.GroupStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Encja reprezentująca grupę uczniów utworzoną przez nauczyciela.
 * Nauczyciel może tworzyć grupy i dodawać do nich uczniów,
 * a następnie udostępniać materiały całym grupom.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "student_group", indexes = {
        @Index(name = "idx_student_group_teacher_id", columnList = "teacher_id"),
        @Index(name = "idx_student_group_status", columnList = "status")
})
public class StudentGroup {

    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private GroupStatus status = GroupStatus.ACTIVE;

    @Column(length = 7)
    private String color;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<GroupMember> members = new HashSet<>();

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

    public int getMemberCount() {
        return members != null ? members.size() : 0;
    }
}
