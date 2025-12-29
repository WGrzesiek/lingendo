package com.learnwords.userservice.repository;

import com.learnwords.userservice.entity.StudentGroup;
import com.learnwords.userservice.enums.GroupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, String> {

    Page<StudentGroup> findByTeacherIdAndStatus(String teacherId, GroupStatus status, Pageable pageable);
    Page<StudentGroup> findByTeacherId(String teacherId, Pageable pageable);
    Optional<StudentGroup> findByIdAndTeacherId(String id, String teacherId);
    boolean existsByIdAndTeacherId(String id, String teacherId);
    long countByTeacherIdAndStatus(String teacherId, GroupStatus status);

    @Query("SELECT g.id FROM StudentGroup g WHERE g.teacher.id = :teacherId AND g.status = :status")
    List<String> findGroupIdsByTeacherIdAndStatus(@Param("teacherId") String teacherId, @Param("status") GroupStatus status);

    @Query("SELECT g.id FROM StudentGroup g WHERE g.teacher.id = :teacherId")
    List<String> findGroupIdsByTeacherId(@Param("teacherId") String teacherId);

    @Query("SELECT DISTINCT gm.group FROM GroupMember gm " +
           "WHERE gm.student.id = :studentId AND gm.status = 'ACTIVE' AND gm.group.status = 'ACTIVE'")
    Page<StudentGroup> findGroupsByStudentId(@Param("studentId") String studentId, Pageable pageable);

    @Query("SELECT gm.group.id FROM GroupMember gm " +
           "WHERE gm.student.id = :studentId AND gm.status = 'ACTIVE' AND gm.group.status = 'ACTIVE'")
    List<String> findGroupIdsByStudentId(@Param("studentId") String studentId);
}
