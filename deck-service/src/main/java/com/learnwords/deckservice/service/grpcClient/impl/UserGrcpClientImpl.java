package com.learnwords.deckservice.service.grpcClient.impl;

import com.learnwords.auth.v1.AuthServiceGrpc;
import com.learnwords.auth.v1.GetUserByIdRequest;
import com.learnwords.auth.v1.GetUserNameByIdResponse;
import com.learnwords.deckservice.service.grpcClient.UserGrcpClient;
import com.learnwords.groups.v1.*;
import com.learnwords.users.v1.*;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementacja klienta gRPC do komunikacji z user-service
 */
@Slf4j
@Component
public class UserGrcpClientImpl implements UserGrcpClient {
    
    private static final long GRPC_DEADLINE_MS = 800;

    @GrpcClient("auth")
    private AuthServiceGrpc.AuthServiceBlockingStub authStub;

    @GrpcClient("user-relations")
    private UserRelationsServiceGrpc.UserRelationsServiceBlockingStub userRelationsStub;

    @GrpcClient("student-groups")
    private StudentGroupServiceGrpc.StudentGroupServiceBlockingStub groupsStub;

    @Override
    public GetUserNameByIdResponse getUserNameById(String userId) {
        var request = GetUserByIdRequest.newBuilder()
                .setUserId(userId)
                .build();
        return authStub
                .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                .getUserNameById(request);
    }

    @Override
    public boolean hasAccessToUser(String requesterId, String targetUserId) {
        try {
            var request = CheckUserAccessRequest.newBuilder()
                    .setRequesterId(requesterId)
                    .setTargetUserId(targetUserId)
                    .build();
            
            CheckUserAccessResponse response = userRelationsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .checkUserAccess(request);
            
            return response.getHasAccess();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas sprawdzania dostępu {} -> {}: {}", 
                    requesterId, targetUserId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isTeacherOf(String teacherId, String studentId) {
        try {
            var request = IsTeacherOfRequest.newBuilder()
                    .setTeacherId(teacherId)
                    .setStudentId(studentId)
                    .build();
            
            IsTeacherOfResponse response = userRelationsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .isTeacherOf(request);
            
            return response.getIsTeacher();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas sprawdzania relacji nauczyciel-uczeń {} -> {}: {}", 
                    teacherId, studentId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean areFriends(String userId1, String userId2) {
        try {
            var request = AreFriendsRequest.newBuilder()
                    .setUserId1(userId1)
                    .setUserId2(userId2)
                    .build();
            
            AreFriendsResponse response = userRelationsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .areFriends(request);
            
            return response.getAreFriends();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas sprawdzania znajomości {} <-> {}: {}", 
                    userId1, userId2, e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getAccessibleUserIds(String userId) {
        try {
            var request = GetAccessibleUsersRequest.newBuilder()
                    .setUserId(userId)
                    .build();
            
            GetAccessibleUsersResponse response = userRelationsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .getAccessibleUsers(request);
            
            return response.getUserIdsList();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania dostępnych użytkowników dla {}: {}", 
                    userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getStudentIds(String teacherId) {
        try {
            var request = GetStudentIdsRequest.newBuilder()
                    .setTeacherId(teacherId)
                    .build();
            
            GetStudentIdsResponse response = userRelationsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .getStudentIds(request);
            
            return response.getStudentIdsList();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania uczniów nauczyciela {}: {}", 
                    teacherId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getFriendIds(String userId) {
        try {
            var request = GetFriendIdsRequest.newBuilder()
                    .setUserId(userId)
                    .build();
            
            GetFriendIdsResponse response = userRelationsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .getFriendIds(request);
            
            return response.getFriendIdsList();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania znajomych użytkownika {}: {}", 
                    userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Boolean> checkAccessBatch(String requesterId, List<String> targetUserIds) {
        try {
            var request = CheckUserAccessRequest.newBuilder()
                    .setRequesterId(requesterId)
                    .build();
            
            // Najpierw pobierz wszystkich dostępnych użytkowników
            List<String> accessibleIds = getAccessibleUserIds(requesterId);
            
            Map<String, Boolean> result = new HashMap<>();
            for (String targetId : targetUserIds) {
                result.put(targetId, accessibleIds.contains(targetId) || requesterId.equals(targetId));
            }
            return result;
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania batch dostępu dla {}: {}", 
                    requesterId, e.getMessage());
            Map<String, Boolean> result = new HashMap<>();
            targetUserIds.forEach(id -> result.put(id, false));
            return result;
        }
    }

    // === Metody dla grup ===

    @Override
    public List<String> getGroupIds(String userId) {
        try {
            // Pobierz grupy jako nauczyciel
            var teacherRequest = GetTeacherGroupIdsRequest.newBuilder()
                    .setTeacherId(userId)
                    .build();
            
            var teacherResponse = groupsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .getTeacherGroupIds(teacherRequest);
            
            // Pobierz grupy jako uczeń
            var studentRequest = GetStudentGroupIdsRequest.newBuilder()
                    .setStudentId(userId)
                    .build();
            
            var studentResponse = groupsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .getStudentGroupIds(studentRequest);
            
            List<String> allGroupIds = new java.util.ArrayList<>(teacherResponse.getGroupIdsList());
            allGroupIds.addAll(studentResponse.getGroupIdsList());
            
            return allGroupIds.stream().distinct().toList();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania grup użytkownika {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getTeacherGroupIds(String teacherId) {
        try {
            var request = GetTeacherGroupIdsRequest.newBuilder()
                    .setTeacherId(teacherId)
                    .build();
            
            var response = groupsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .getTeacherGroupIds(request);
            
            return response.getGroupIdsList();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania grup nauczyciela {}: {}", teacherId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getGroupMemberIds(String groupId) {
        try {
            var request = GetGroupMemberIdsRequest.newBuilder()
                    .setGroupId(groupId)
                    .build();
            
            var response = groupsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .getGroupMemberIds(request);
            
            return response.getStudentIdsList();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania członków grupy {}: {}", groupId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getStudentIdsFromGroups(List<String> groupIds) {
        try {
            if (groupIds == null || groupIds.isEmpty()) {
                return Collections.emptyList();
            }
            
            var request = GetStudentIdsFromGroupsRequest.newBuilder()
                    .addAllGroupIds(groupIds)
                    .build();
            
            var response = groupsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .getStudentIdsFromGroups(request);
            
            return response.getStudentIdsList();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania uczniów z grup: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isGroupOwner(String userId, String groupId) {
        try {
            var request = CheckGroupAccessRequest.newBuilder()
                    .setTeacherId(userId)
                    .addGroupIds(groupId)
                    .build();
            
            var response = groupsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .checkGroupAccess(request);
            
            return response.getAccessMapOrDefault(groupId, false);
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas sprawdzania właściciela grupy {} -> {}: {}", 
                    userId, groupId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isStudentInGroup(String studentId, String groupId) {
        try {
            var request = IsStudentInGroupRequest.newBuilder()
                    .setStudentId(studentId)
                    .setGroupId(groupId)
                    .build();
            
            var response = groupsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .isStudentInGroup(request);
            
            return response.getIsMember();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas sprawdzania członkostwa ucznia {} w grupie {}: {}", 
                    studentId, groupId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isStudentInAnyGroup(String studentId, List<String> groupIds) {
        try {
            if (groupIds == null || groupIds.isEmpty()) {
                return false;
            }
            
            var request = IsStudentInAnyGroupRequest.newBuilder()
                    .setStudentId(studentId)
                    .addAllGroupIds(groupIds)
                    .build();
            
            var response = groupsStub
                    .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                    .isStudentInAnyGroup(request);
            
            return response.getIsMember();
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas sprawdzania członkostwa ucznia {} w grupach: {}", 
                    studentId, e.getMessage());
            return false;
        }
    }
}
