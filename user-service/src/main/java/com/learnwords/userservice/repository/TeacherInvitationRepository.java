package com.learnwords.userservice.repository;

import com.learnwords.userservice.entity.TeacherInvitation;
import com.learnwords.userservice.enums.InvitationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TeacherInvitationRepository extends JpaRepository<TeacherInvitation, String> {

    Optional<TeacherInvitation> findByInvitationCode(String invitationCode);

    Optional<TeacherInvitation> findByInvitationCodeAndStatus(String invitationCode, InvitationStatus status);

    Page<TeacherInvitation> findByTeacherId(String teacherId, Pageable pageable);

    List<TeacherInvitation> findByTeacherIdAndStatus(String teacherId, InvitationStatus status);

    long countByTeacherId(String teacherId);

    long countByTeacherIdAndStatus(String teacherId, InvitationStatus status);

    boolean existsByInvitationCode(String invitationCode);

    @Modifying
    @Query("UPDATE TeacherInvitation ti SET ti.status = 'EXPIRED', ti.updatedAt = :now " +
            "WHERE ti.status = 'ACTIVE' AND ti.expiresAt IS NOT NULL AND ti.expiresAt < :now")
    int expireOldInvitations(@Param("now") Instant now);
}
