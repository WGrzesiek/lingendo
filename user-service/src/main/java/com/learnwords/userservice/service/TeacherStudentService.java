package com.learnwords.userservice.service;

import com.learnwords.userservice.dtos.teacher.*;
import org.springframework.data.domain.Page;


public interface TeacherStudentService {

    InvitationResponse createInvitation(String teacherId, CreateInvitationRequest request);
    Page<InvitationResponse> getTeacherInvitations(String teacherId, int page, int size);
    void deactivateInvitation(String teacherId, String invitationId);
    void deleteInvitation(String teacherId, String invitationId);
    Page<StudentResponse> getStudents(String teacherId, int page, int size);
    void removeStudent(String teacherId, String studentId);
    void blockStudent(String teacherId, String studentId);
    void unblockStudent(String teacherId, String studentId);
    TeacherStatsResponse getTeacherStats(String teacherId);
    TeacherResponse joinTeacher(String studentId, String invitationCode);
    Page<TeacherResponse> getMyTeachers(String studentId, int page, int size);
    void leaveTeacher(String studentId, String teacherId);
    boolean isTeacherOf(String teacherId, String studentId);
    InvitationResponse getInvitationInfo(String invitationCode);
}
