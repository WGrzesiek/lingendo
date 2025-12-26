package com.learnwords.userservice.entity;

import com.learnwords.userservice.enums.TeacherStudentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "teacher_student",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_teacher_student", columnNames = {"teacher_id", "student_id"})
        },
        indexes = {
                @Index(name = "idx_teacher_student_teacher_id", columnList = "teacher_id"),
                @Index(name = "idx_teacher_student_student_id", columnList = "student_id"),
                @Index(name = "idx_teacher_student_status", columnList = "status")
        })
public class TeacherStudent {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeacherStudentStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant acceptedAt;

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
