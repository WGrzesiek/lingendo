package com.learnwords.userservice.repository;

import com.learnwords.userservice.entity.TeacherStudent;
import com.learnwords.userservice.enums.TeacherStudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherStudentRepository extends JpaRepository<TeacherStudent, String> {

    Optional<TeacherStudent> findByTeacherIdAndStudentId(String teacherId, String studentId);

    Optional<TeacherStudent> findByTeacherIdAndStudentIdAndStatus(
            String teacherId, String studentId, TeacherStudentStatus status);

    boolean existsByTeacherIdAndStudentIdAndStatus(
            String teacherId, String studentId, TeacherStudentStatus status);

    @Query("SELECT ts FROM TeacherStudent ts WHERE ts.teacher.id = :teacherId AND ts.status = :status")
    Page<TeacherStudent> findStudentsByTeacherId(
            @Param("teacherId") String teacherId,
            @Param("status") TeacherStudentStatus status,
            Pageable pageable);

    List<TeacherStudent> findByTeacherIdAndStatus(String teacherId, TeacherStudentStatus status);

    @Query("SELECT ts FROM TeacherStudent ts WHERE ts.student.id = :studentId AND ts.status = :status")
    Page<TeacherStudent> findTeachersByStudentId(
            @Param("studentId") String studentId,
            @Param("status") TeacherStudentStatus status,
            Pageable pageable);

    List<TeacherStudent> findByStudentIdAndStatus(String studentId, TeacherStudentStatus status);

    long countByTeacherIdAndStatus(String teacherId, TeacherStudentStatus status);

    long countByStudentIdAndStatus(String studentId, TeacherStudentStatus status);

    boolean existsByTeacherIdAndStudentId(String teacherId, String studentId);

    @Query("SELECT ts.student.id FROM TeacherStudent ts WHERE ts.teacher.id = :teacherId AND ts.status = 'ACTIVE'")
    List<String> findStudentIdsByTeacherId(@Param("teacherId") String teacherId);

    @Query("SELECT ts.teacher.id FROM TeacherStudent ts WHERE ts.student.id = :studentId AND ts.status = 'ACTIVE'")
    List<String> findTeacherIdsByStudentId(@Param("studentId") String studentId);
}
