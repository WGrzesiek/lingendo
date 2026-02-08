package com.learnwords.deckservice.service.grpcClient;

import com.learnwords.auth.v1.GetUserNameByIdResponse;

import java.util.List;
import java.util.Map;

/**
 * Klient gRPC do komunikacji z user-service
 */
public interface UserGrcpClient {
    GetUserNameByIdResponse getUserNameById(String userId);
    boolean hasAccessToUser(String requesterId, String targetUserId);
    boolean isTeacherOf(String teacherId, String studentId);boolean areFriends(String userId1, String userId2);
    List<String> getAccessibleUserIds(String userId);
    List<String> getStudentIds(String teacherId);
    List<String> getFriendIds(String userId);
    Map<String, Boolean> checkAccessBatch(String requesterId, List<String> targetUserIds);

    // === Metody dla grup ===
    List<String> getGroupIds(String userId);
    List<String> getTeacherGroupIds(String teacherId);
    List<String> getGroupMemberIds(String groupId);
    List<String> getStudentIdsFromGroups(List<String> groupIds);
    boolean isGroupOwner(String userId, String groupId);
    boolean isStudentInGroup(String studentId, String groupId);
    boolean isStudentInAnyGroup(String studentId, List<String> groupIds);
}
