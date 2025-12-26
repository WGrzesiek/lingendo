package com.learnwords.userservice.service.grpc.impl;

import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.enums.AccessType;
import com.learnwords.userservice.repository.FriendshipRepository;
import com.learnwords.userservice.repository.TeacherStudentRepository;
import com.learnwords.userservice.repository.UserRepository;
import com.learnwords.users.v1.*;
import com.learnwords.userservice.service.grpc.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementacja serwisu gRPC do zarządzania relacjami użytkowników.
 * Umożliwia innym mikroserwis sprawdzanie relacji nauczyciel-uczeń oraz znajomości.
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserRelationsServiceGrpcImpl extends UserRelationsServiceGrpc.UserRelationsServiceImplBase implements UserServiceGrpc {

    private final TeacherStudentRepository teacherStudentRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    // === Teacher-Student ===

    @Override
    public void getStudentIds(GetStudentIdsRequest request, StreamObserver<GetStudentIdsResponse> responseObserver) {
        try {
            validateUserId(request.getTeacherId());
            
            List<String> studentIds = teacherStudentRepository.findStudentIdsByTeacherId(request.getTeacherId());
            
            var response = GetStudentIdsResponse.newBuilder()
                    .addAllStudentIds(studentIds)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            log.debug("Pobrano {} uczniów dla nauczyciela {}", studentIds.size(), request.getTeacherId());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania uczniów dla nauczyciela {}", request.getTeacherId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania uczniów")));
        }
    }

    @Override
    public void getStudents(GetStudentsRequest request, StreamObserver<GetStudentsResponse> responseObserver) {
        try {
            validateUserId(request.getTeacherId());
            
            List<String> studentIds = teacherStudentRepository.findStudentIdsByTeacherId(request.getTeacherId());
            List<User> students = userRepository.findAllById(String.valueOf(studentIds));
            
            var responseBuilder = GetStudentsResponse.newBuilder();
            students.forEach(student -> responseBuilder.addStudents(mapToUserBasicInfo(student)));
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania danych uczniów dla nauczyciela {}", request.getTeacherId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania danych uczniów")));
        }
    }

    @Override
    public void getTeacherIds(GetTeacherIdsRequest request, StreamObserver<GetTeacherIdsResponse> responseObserver) {
        try {
            validateUserId(request.getStudentId());
            
            List<String> teacherIds = teacherStudentRepository.findTeacherIdsByStudentId(request.getStudentId());
            
            var response = GetTeacherIdsResponse.newBuilder()
                    .addAllTeacherIds(teacherIds)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            log.debug("Pobrano {} nauczycieli dla ucznia {}", teacherIds.size(), request.getStudentId());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania nauczycieli dla ucznia {}", request.getStudentId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania nauczycieli")));
        }
    }

    @Override
    public void getTeachers(GetTeachersRequest request, StreamObserver<GetTeachersResponse> responseObserver) {
        try {
            validateUserId(request.getStudentId());
            
            List<String> teacherIds = teacherStudentRepository.findTeacherIdsByStudentId(request.getStudentId());
            List<User> teachers = userRepository.findAllById(String.valueOf(teacherIds));
            
            var responseBuilder = GetTeachersResponse.newBuilder();
            teachers.forEach(teacher -> responseBuilder.addTeachers(mapToUserBasicInfo(teacher)));
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania danych nauczycieli dla ucznia {}", request.getStudentId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania danych nauczycieli")));
        }
    }

    @Override
    public void isTeacherOf(IsTeacherOfRequest request, StreamObserver<IsTeacherOfResponse> responseObserver) {
        try {
            validateUserId(request.getTeacherId());
            validateUserId(request.getStudentId());
            
            boolean isTeacher = teacherStudentRepository.existsByTeacherIdAndStudentId(
                    request.getTeacherId(), request.getStudentId());
            
            var response = IsTeacherOfResponse.newBuilder()
                    .setIsTeacher(isTeacher)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania relacji nauczyciel-uczeń {} -> {}", 
                    request.getTeacherId(), request.getStudentId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas sprawdzania relacji")));
        }
    }

    @Override
    public void checkTeacherStudentAccess(CheckTeacherStudentAccessRequest request, 
                                          StreamObserver<CheckTeacherStudentAccessResponse> responseObserver) {
        try {
            validateUserId(request.getTeacherId());
            
            Set<String> accessibleStudents = new HashSet<>(
                    teacherStudentRepository.findStudentIdsByTeacherId(request.getTeacherId()));
            
            var responseBuilder = CheckTeacherStudentAccessResponse.newBuilder();
            for (String studentId : request.getStudentIdsList()) {
                responseBuilder.putAccessMap(studentId, accessibleStudents.contains(studentId));
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania dostępu batch dla nauczyciela {}", request.getTeacherId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas sprawdzania dostępu")));
        }
    }

    // === Friendship ===

    @Override
    public void getFriendIds(GetFriendIdsRequest request, StreamObserver<GetFriendIdsResponse> responseObserver) {
        try {
            validateUserId(request.getUserId());
            
            List<String> friendIds = friendshipRepository.findFriendIdsByUserId(request.getUserId());
            
            var response = GetFriendIdsResponse.newBuilder()
                    .addAllFriendIds(friendIds)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            log.debug("Pobrano {} znajomych dla użytkownika {}", friendIds.size(), request.getUserId());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania znajomych dla użytkownika {}", request.getUserId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania znajomych")));
        }
    }

    @Override
    public void getFriends(GetFriendsRequest request, StreamObserver<GetFriendsResponse> responseObserver) {
        try {
            validateUserId(request.getUserId());
            
            List<String> friendIds = friendshipRepository.findFriendIdsByUserId(request.getUserId());
            List<User> friends = userRepository.findAllById(String.valueOf(friendIds));
            
            var responseBuilder = GetFriendsResponse.newBuilder();
            friends.forEach(friend -> responseBuilder.addFriends(mapToUserBasicInfo(friend)));
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania danych znajomych dla użytkownika {}", request.getUserId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania danych znajomych")));
        }
    }

    @Override
    public void areFriends(AreFriendsRequest request, StreamObserver<AreFriendsResponse> responseObserver) {
        try {
            validateUserId(request.getUserId1());
            validateUserId(request.getUserId2());
            
            boolean areFriends = friendshipRepository.areFriends(request.getUserId1(), request.getUserId2());
            
            var response = AreFriendsResponse.newBuilder()
                    .setAreFriends(areFriends)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania znajomości {} <-> {}", 
                    request.getUserId1(), request.getUserId2(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas sprawdzania znajomości")));
        }
    }

    @Override
    public void checkFriendshipAccess(CheckFriendshipAccessRequest request, 
                                       StreamObserver<CheckFriendshipAccessResponse> responseObserver) {
        try {
            validateUserId(request.getUserId());
            
            Set<String> friends = new HashSet<>(friendshipRepository.findFriendIdsByUserId(request.getUserId()));
            
            var responseBuilder = CheckFriendshipAccessResponse.newBuilder();
            for (String targetId : request.getOtherUserIdsList()) {
                responseBuilder.putFriendshipMap(targetId, friends.contains(targetId));
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania znajomości batch dla użytkownika {}", request.getUserId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas sprawdzania znajomości")));
        }
    }

    // === Combined Access ===

    @Override
    public void checkUserAccess(CheckUserAccessRequest request, StreamObserver<CheckUserAccessResponse> responseObserver) {
        try {
            validateUserId(request.getRequesterId());
            validateUserId(request.getOwnerId());
            
            String requesterId = request.getRequesterId();
            String targetId = request.getOwnerId();
            
            // Sprawdź czy to ten sam użytkownik
            if (requesterId.equals(targetId)) {
                responseObserver.onNext(buildAccessResponse(true, AccessType.SELF));
                responseObserver.onCompleted();
                return;
            }
            
            boolean isFriend = friendshipRepository.areFriends(requesterId, targetId);
            boolean isTeacher = teacherStudentRepository.existsByTeacherIdAndStudentId(requesterId, targetId);
            boolean isStudent = teacherStudentRepository.existsByTeacherIdAndStudentId(targetId, requesterId);
            
            boolean hasAccess = isFriend || isTeacher || isStudent;
            
            AccessType accessType = AccessType.NONE;
            if (isFriend) accessType = AccessType.FRIEND;
            else if (isTeacher) accessType = AccessType.TEACHER;
            else if (isStudent) accessType = AccessType.STUDENT;
            
            responseObserver.onNext(buildAccessResponse(hasAccess, accessType));
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania dostępu {} -> {}", 
                    request.getRequesterId(), request.getOwnerId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas sprawdzania dostępu")));
        }
    }

    @Override
    public void getAccessibleUsers(GetAccessibleUsersRequest request, 
                                    StreamObserver<GetAccessibleUsersResponse> responseObserver) {
        try {
            validateUserId(request.getUserId());
            
            String userId = request.getUserId();
            Set<String> accessibleUserTIds = new HashSet<>();
            Set<String> accessibleUserSIds = new HashSet<>();
            Set<String> accessibleUserFIds = new HashSet<>();

            accessibleUserTIds.addAll(teacherStudentRepository.findStudentIdsByTeacherId(userId));
            
            accessibleUserSIds.addAll(teacherStudentRepository.findTeacherIdsByStudentId(userId));
            
            accessibleUserFIds.addAll(friendshipRepository.findFriendIdsByUserId(userId));

            var response = GetAccessibleUsersResponse.newBuilder()
                    .addAllTeacherIds(accessibleUserTIds)
                    .addAllStudentIds(accessibleUserSIds)
                    .addAllFriendIds(accessibleUserFIds)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            log.debug("Pobrano dostępnych użytkowników dla {}", userId);
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania dostępnych użytkowników dla {}", request.getUserId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania dostępnych użytkowników")));
        }
    }

    // === User Info ===

    @Override
    public void getUsersByIds(GetUsersByIdsRequest request, StreamObserver<GetUsersByIdsResponse> responseObserver) {
        try {
            if (request.getUserIdsList().isEmpty()) {
                responseObserver.onNext(GetUsersByIdsResponse.getDefaultInstance());
                responseObserver.onCompleted();
                return;
            }
            
            List<User> users = userRepository.findAllById(request.getUserIdsList().toString());
            
            var responseBuilder = GetUsersByIdsResponse.newBuilder();
            users.forEach(user -> responseBuilder.addUsers(mapToUserBasicInfo(user)));
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Błąd podczas pobierania użytkowników po ID", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania użytkowników")));
        }
    }

    // === Helper Methods ===

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new StatusRuntimeException(
                    Status.INVALID_ARGUMENT.withDescription("ID użytkownika jest wymagane"));
        }
    }

    private UserBasicInfo mapToUserBasicInfo(User user) {
        return UserBasicInfo.newBuilder()
                .setUserId(user.getId())
                .setUsername(user.getUsername())
                .setFirstName(user.getFirstName() != null ? user.getFirstName() : "")
                .setLastName(user.getLastName() != null ? user.getLastName() : "")
                .setEmail(user.getEmail())
                .build();
    }

    private CheckUserAccessResponse buildAccessResponse(boolean hasAccess, AccessType accessType) {
        return CheckUserAccessResponse.newBuilder()
                .setHasAccess(hasAccess)
//                .setIsFriend(isFriend)
//                .setIsTeacherStudent(isTeacherStudent)
                .setAccessType(accessType.name())
                .build();
    }
}
