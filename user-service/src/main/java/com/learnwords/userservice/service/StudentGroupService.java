package com.learnwords.userservice.service;

import com.learnwords.userservice.dtos.group.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Interfejs serwisu do zarządzania grupami uczniów.
 */
public interface StudentGroupService {

    GroupResponse createGroup(String teacherId, CreateGroupRequest request);
    Page<GroupResponse> getTeacherGroups(String teacherId, boolean includeArchived, int page, int size);
    GroupResponse getGroup(String teacherId, String groupId);
    GroupResponse updateGroup(String teacherId, String groupId, UpdateGroupRequest request);
    void archiveGroup(String teacherId, String groupId);
    void restoreGroup(String teacherId, String groupId);
    void deleteGroup(String teacherId, String groupId);
    GroupStatsResponse getGroupStats(String teacherId);
    BatchMemberOperationResponse addMembers(String teacherId, String groupId, AddMembersRequest request);
    BatchMemberOperationResponse removeMembers(String teacherId, String groupId, RemoveMembersRequest request);
    Page<GroupMemberResponse> getGroupMembers(String teacherId, String groupId, int page, int size);
    List<String> getGroupMemberIds(String groupId);
    List<String> getStudentIdsFromGroups(List<String> groupIds);
    Page<GroupResponse> getStudentGroups(String studentId, int page, int size);
    boolean isStudentInGroup(String studentId, String groupId);
    boolean isStudentInAnyGroup(String studentId, List<String> groupIds);
}
