package com.learnwords.userservice.service.grpc;

import com.learnwords.groups.v1.*;
import io.grpc.stub.StreamObserver;

/**
 * Interfejs serwisu gRPC do zarządzania grupami uczniów.
 */
public interface StudentGroupGrpcService {

    void getTeacherGroups(GetTeacherGroupsRequest request, StreamObserver<GetTeacherGroupsResponse> responseObserver);
    void getTeacherGroupIds(GetTeacherGroupIdsRequest request, StreamObserver<GetTeacherGroupIdsResponse> responseObserver);
    void getGroup(GetGroupRequest request, StreamObserver<GetGroupResponse> responseObserver);
    void checkGroupAccess(CheckGroupAccessRequest request, StreamObserver<CheckGroupAccessResponse> responseObserver);
    void getGroupMembers(GetGroupMembersRequest request, StreamObserver<GetGroupMembersResponse> responseObserver);
    void getGroupMemberIds(GetGroupMemberIdsRequest request, StreamObserver<GetGroupMemberIdsResponse> responseObserver);
    void getStudentIdsFromGroups(GetStudentIdsFromGroupsRequest request, StreamObserver<GetStudentIdsFromGroupsResponse> responseObserver);
    void getStudentGroups(GetStudentGroupsRequest request, StreamObserver<GetStudentGroupsResponse> responseObserver);
    void getStudentGroupIds(GetStudentGroupIdsRequest request, StreamObserver<GetStudentGroupIdsResponse> responseObserver);
    void isStudentInGroup(IsStudentInGroupRequest request, StreamObserver<IsStudentInGroupResponse> responseObserver);
    void isStudentInAnyGroup(IsStudentInAnyGroupRequest request, StreamObserver<IsStudentInAnyGroupResponse> responseObserver);
    void checkStudentGroupMembership(CheckStudentGroupMembershipRequest request, StreamObserver<CheckStudentGroupMembershipResponse> responseObserver);
}
