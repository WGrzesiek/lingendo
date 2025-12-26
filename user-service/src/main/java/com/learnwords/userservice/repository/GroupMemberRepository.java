package com.learnwords.userservice.repository;

import com.learnwords.userservice.entity.GroupMember;
import com.learnwords.userservice.enums.GroupMemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, String> {

    Optional<GroupMember> findByGroupIdAndStudentId(String groupId, String studentId);
    Optional<GroupMember> findByGroupIdAndStudentIdAndStatus(String groupId, String studentId, GroupMemberStatus status);
    boolean existsByGroupIdAndStudentIdAndStatus(String groupId, String studentId, GroupMemberStatus status);
    boolean existsByGroupIdAndStudentId(String groupId, String studentId);
    Page<GroupMember> findByGroupIdAndStatus(String groupId, GroupMemberStatus status, Pageable pageable);
    List<GroupMember> findByGroupIdAndStatus(String groupId, GroupMemberStatus status);
    long countByGroupIdAndStatus(String groupId, GroupMemberStatus status);

    @Query("SELECT gm.student.id FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.status = 'ACTIVE'")
    List<String> findStudentIdsByGroupId(@Param("groupId") String groupId);

    @Query("SELECT DISTINCT gm.student.id FROM GroupMember gm " +
           "WHERE gm.group.id IN :groupIds AND gm.status = 'ACTIVE'")
    List<String> findStudentIdsByGroupIds(@Param("groupIds") List<String> groupIds);

    @Query("SELECT gm.group.id FROM GroupMember gm WHERE gm.student.id = :studentId AND gm.status = 'ACTIVE'")
    List<String> findGroupIdsByStudentId(@Param("studentId") String studentId);

    @Query("SELECT COUNT(gm) > 0 FROM GroupMember gm " +
           "WHERE gm.student.id = :studentId AND gm.group.id IN :groupIds AND gm.status = 'ACTIVE'")
    boolean isStudentInAnyGroup(@Param("studentId") String studentId, @Param("groupIds") List<String> groupIds);

    void deleteAllByGroupId(String groupId);
}
