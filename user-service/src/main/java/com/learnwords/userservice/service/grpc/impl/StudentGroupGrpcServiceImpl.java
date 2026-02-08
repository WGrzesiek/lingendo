package com.learnwords.userservice.service.grpc.impl;

import com.learnwords.groups.v1.*;
import com.learnwords.userservice.entity.GroupMember;
import com.learnwords.userservice.entity.StudentGroup;
import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.enums.GroupMemberStatus;
import com.learnwords.userservice.enums.GroupStatus;
import com.learnwords.userservice.repository.GroupMemberRepository;
import com.learnwords.userservice.repository.StudentGroupRepository;
import com.learnwords.userservice.service.grpc.StudentGroupGrpcService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementacja serwisu gRPC do zarządzania grupami uczniów.
 * Umożliwia innym mikroserwis sprawdzanie przynależności do grup i pobieranie informacji o grupach.
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class StudentGroupGrpcServiceImpl extends StudentGroupServiceGrpc.StudentGroupServiceImplBase implements StudentGroupGrpcService {

    private final StudentGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;

    @Override
    public void getTeacherGroups(GetTeacherGroupsRequest request, 
                                  StreamObserver<GetTeacherGroupsResponse> responseObserver) {
        try {
            validateUserId(request.getTeacherId());

            List<StudentGroup> groups = groupRepository
                    .findByTeacherIdAndStatus(request.getTeacherId(), GroupStatus.ACTIVE, Pageable.unpaged())
                    .getContent();

            var responseBuilder = GetTeacherGroupsResponse.newBuilder();
            groups.forEach(group -> responseBuilder.addGroups(mapToGroupBasicInfo(group)));

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

            log.debug("Pobrano {} grup dla nauczyciela {}", groups.size(), request.getTeacherId());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania grup nauczyciela {}", request.getTeacherId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania grup")));
        }
    }

    @Override
    public void getTeacherGroupIds(GetTeacherGroupIdsRequest request,
                                    StreamObserver<GetTeacherGroupIdsResponse> responseObserver) {
        try {
            validateUserId(request.getTeacherId());

            List<String> groupIds = groupRepository.findGroupIdsByTeacherId(request.getTeacherId());

            var response = GetTeacherGroupIdsResponse.newBuilder()
                    .addAllGroupIds(groupIds)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.debug("Pobrano {} ID grup dla nauczyciela {}", groupIds.size(), request.getTeacherId());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania ID grup nauczyciela {}", request.getTeacherId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania ID grup")));
        }
    }

    @Override
    public void getGroup(GetGroupRequest request, StreamObserver<GetGroupResponse> responseObserver) {
        try {
            validateGroupId(request.getGroupId());

            Optional<StudentGroup> groupOpt = groupRepository.findById(request.getGroupId());
            
            if (groupOpt.isEmpty()) {
                responseObserver.onError(new StatusRuntimeException(
                        Status.NOT_FOUND.withDescription("Nie znaleziono grupy: " + request.getGroupId())));
                return;
            }

            var response = GetGroupResponse.newBuilder()
                    .setGroup(mapToGroupBasicInfo(groupOpt.get()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania grupy {}", request.getGroupId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania grupy")));
        }
    }

    @Override
    public void checkGroupAccess(CheckGroupAccessRequest request,
                                  StreamObserver<CheckGroupAccessResponse> responseObserver) {
        try {
            validateUserId(request.getTeacherId());

            Set<String> ownedGroups = new HashSet<>(
                    groupRepository.findGroupIdsByTeacherId(request.getTeacherId()));

            log.info("Sprawdzanie dostępu do grup - teacherId: {}, sprawdzane grupy: {}, posiadane grupy: {}",
                    request.getTeacherId(), request.getGroupIdsList(), ownedGroups);

            var responseBuilder = CheckGroupAccessResponse.newBuilder();
            for (String groupId : request.getGroupIdsList()) {
                boolean hasAccess = ownedGroups.contains(groupId);
                log.debug("Grupa {} - dostęp: {}", groupId, hasAccess);
                responseBuilder.putAccessMap(groupId, hasAccess);
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania dostępu do grup dla nauczyciela {}", request.getTeacherId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas sprawdzania dostępu")));
        }
    }

    @Override
    public void getGroupMembers(GetGroupMembersRequest request,
                                 StreamObserver<GetGroupMembersResponse> responseObserver) {
        try {
            validateGroupId(request.getGroupId());

            List<GroupMember> members = memberRepository
                    .findByGroupIdAndStatus(request.getGroupId(), GroupMemberStatus.ACTIVE);

            var responseBuilder = GetGroupMembersResponse.newBuilder();
            members.forEach(member -> responseBuilder.addMembers(mapToGroupMemberInfo(member)));

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

            log.debug("Pobrano {} członków grupy {}", members.size(), request.getGroupId());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania członków grupy {}", request.getGroupId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania członków grupy")));
        }
    }

    @Override
    public void getGroupMemberIds(GetGroupMemberIdsRequest request,
                                   StreamObserver<GetGroupMemberIdsResponse> responseObserver) {
        try {
            validateGroupId(request.getGroupId());

            List<String> studentIds = memberRepository.findStudentIdsByGroupId(request.getGroupId());

            var response = GetGroupMemberIdsResponse.newBuilder()
                    .addAllStudentIds(studentIds)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.debug("Pobrano {} ID uczniów z grupy {}", studentIds.size(), request.getGroupId());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania ID uczniów z grupy {}", request.getGroupId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania ID uczniów")));
        }
    }

    @Override
    public void getStudentIdsFromGroups(GetStudentIdsFromGroupsRequest request,
                                         StreamObserver<GetStudentIdsFromGroupsResponse> responseObserver) {
        try {
            if (request.getGroupIdsList().isEmpty()) {
                responseObserver.onNext(GetStudentIdsFromGroupsResponse.newBuilder().build());
                responseObserver.onCompleted();
                return;
            }

            List<String> studentIds = memberRepository.findStudentIdsByGroupIds(request.getGroupIdsList());

            var response = GetStudentIdsFromGroupsResponse.newBuilder()
                    .addAllStudentIds(studentIds)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.debug("Pobrano {} unikalnych ID uczniów z {} grup", 
                    studentIds.size(), request.getGroupIdsList().size());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania ID uczniów z grup", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania ID uczniów")));
        }
    }

    @Override
    public void getStudentGroups(GetStudentGroupsRequest request,
                                  StreamObserver<GetStudentGroupsResponse> responseObserver) {
        try {
            validateUserId(request.getStudentId());

            List<StudentGroup> groups = groupRepository
                    .findGroupsByStudentId(request.getStudentId(), Pageable.unpaged())
                    .getContent();

            var responseBuilder = GetStudentGroupsResponse.newBuilder();
            groups.forEach(group -> responseBuilder.addGroups(mapToGroupBasicInfo(group)));

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

            log.debug("Pobrano {} grup dla ucznia {}", groups.size(), request.getStudentId());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania grup ucznia {}", request.getStudentId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania grup ucznia")));
        }
    }

    @Override
    public void getStudentGroupIds(GetStudentGroupIdsRequest request,
                                    StreamObserver<GetStudentGroupIdsResponse> responseObserver) {
        try {
            validateUserId(request.getStudentId());

            List<String> groupIds = groupRepository.findGroupIdsByStudentId(request.getStudentId());

            var response = GetStudentGroupIdsResponse.newBuilder()
                    .addAllGroupIds(groupIds)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.debug("Pobrano {} ID grup dla ucznia {}", groupIds.size(), request.getStudentId());
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania ID grup ucznia {}", request.getStudentId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas pobierania ID grup ucznia")));
        }
    }

    @Override
    public void isStudentInGroup(IsStudentInGroupRequest request,
                                  StreamObserver<IsStudentInGroupResponse> responseObserver) {
        try {
            validateUserId(request.getStudentId());
            validateGroupId(request.getGroupId());

            boolean isMember = memberRepository.existsByGroupIdAndStudentIdAndStatus(
                    request.getGroupId(), request.getStudentId(), GroupMemberStatus.ACTIVE);

            var response = IsStudentInGroupResponse.newBuilder()
                    .setIsMember(isMember)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania członkostwa ucznia {} w grupie {}", 
                    request.getStudentId(), request.getGroupId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas sprawdzania członkostwa")));
        }
    }

    @Override
    public void isStudentInAnyGroup(IsStudentInAnyGroupRequest request,
                                     StreamObserver<IsStudentInAnyGroupResponse> responseObserver) {
        try {
            validateUserId(request.getStudentId());

            if (request.getGroupIdsList().isEmpty()) {
                responseObserver.onNext(IsStudentInAnyGroupResponse.newBuilder()
                        .setIsMember(false)
                        .build());
                responseObserver.onCompleted();
                return;
            }

            boolean isMember = memberRepository.isStudentInAnyGroup(
                    request.getStudentId(), request.getGroupIdsList());

            var responseBuilder = IsStudentInAnyGroupResponse.newBuilder()
                    .setIsMember(isMember);

            if (isMember) {
                List<String> studentGroupIds = memberRepository.findGroupIdsByStudentId(request.getStudentId());
                Set<String> requestedGroups = new HashSet<>(request.getGroupIdsList());
                studentGroupIds.stream()
                        .filter(requestedGroups::contains)
                        .forEach(responseBuilder::addMemberGroupIds);
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania członkostwa ucznia {} w grupach", request.getStudentId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas sprawdzania członkostwa")));
        }
    }

    @Override
    public void checkStudentGroupMembership(CheckStudentGroupMembershipRequest request,
                                             StreamObserver<CheckStudentGroupMembershipResponse> responseObserver) {
        try {
            validateUserId(request.getStudentId());

            Set<String> studentGroups = new HashSet<>(
                    memberRepository.findGroupIdsByStudentId(request.getStudentId()));

            var responseBuilder = CheckStudentGroupMembershipResponse.newBuilder();
            for (String groupId : request.getGroupIdsList()) {
                responseBuilder.putMembershipMap(groupId, studentGroups.contains(groupId));
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania członkostwa ucznia {} w grupach", request.getStudentId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Błąd wewnętrzny podczas sprawdzania członkostwa")));
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new StatusRuntimeException(
                    Status.INVALID_ARGUMENT.withDescription("ID użytkownika nie może być puste"));
        }
    }

    private void validateGroupId(String groupId) {
        if (groupId == null || groupId.isBlank()) {
            throw new StatusRuntimeException(
                    Status.INVALID_ARGUMENT.withDescription("ID grupy nie może być puste"));
        }
    }

    private GroupBasicInfo mapToGroupBasicInfo(StudentGroup group) {
        int memberCount = (int) memberRepository.countByGroupIdAndStatus(group.getId(), GroupMemberStatus.ACTIVE);
        
        return GroupBasicInfo.newBuilder()
                .setGroupId(group.getId())
                .setName(group.getName())
                .setDescription(group.getDescription() != null ? group.getDescription() : "")
                .setTeacherId(group.getTeacher().getId())
                .setColor(group.getColor() != null ? group.getColor() : "")
                .setMemberCount(memberCount)
                .build();
    }

    private GroupMemberInfo mapToGroupMemberInfo(GroupMember member) {
        User student = member.getStudent();
        return GroupMemberInfo.newBuilder()
                .setMemberId(member.getId())
                .setStudentId(student.getId())
                .setUsername(student.getUsername())
                .setFirstName(student.getFirstName() != null ? student.getFirstName() : "")
                .setLastName(student.getLastName() != null ? student.getLastName() : "")
                .setJoinedAt(member.getJoinedAt().toEpochMilli())
                .build();
    }
}
