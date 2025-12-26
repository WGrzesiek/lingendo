package com.learnwords.userservice.service.grpc;

import com.learnwords.users.v1.*;
import io.grpc.stub.StreamObserver;

/**
 * Interfejs serwisu gRPC do zarządzania relacjami użytkowników.
 * Umożliwia innym mikroserwis sprawdzanie relacji nauczyciel-uczeń oraz znajomości.
 */
public interface UserServiceGrpc {

    void getStudentIds(GetStudentIdsRequest request, StreamObserver<GetStudentIdsResponse> responseObserver);

    void getStudents(GetStudentsRequest request, StreamObserver<GetStudentsResponse> responseObserver);

    void getTeacherIds(GetTeacherIdsRequest request, StreamObserver<GetTeacherIdsResponse> responseObserver);

    void getTeachers(GetTeachersRequest request, StreamObserver<GetTeachersResponse> responseObserver);

    void isTeacherOf(IsTeacherOfRequest request, StreamObserver<IsTeacherOfResponse> responseObserver);

    void checkTeacherStudentAccess(CheckTeacherStudentAccessRequest request,
                                    StreamObserver<CheckTeacherStudentAccessResponse> responseObserver);

    void getFriendIds(GetFriendIdsRequest request, StreamObserver<GetFriendIdsResponse> responseObserver);

    void getFriends(GetFriendsRequest request, StreamObserver<GetFriendsResponse> responseObserver);

    void areFriends(AreFriendsRequest request, StreamObserver<AreFriendsResponse> responseObserver);

    void checkFriendshipAccess(CheckFriendshipAccessRequest request,
                                StreamObserver<CheckFriendshipAccessResponse> responseObserver);

    void checkUserAccess(CheckUserAccessRequest request, StreamObserver<CheckUserAccessResponse> responseObserver);

    void getAccessibleUsers(GetAccessibleUsersRequest request, 
                             StreamObserver<GetAccessibleUsersResponse> responseObserver);

    void getUsersByIds(GetUsersByIdsRequest request, StreamObserver<GetUsersByIdsResponse> responseObserver);
}
