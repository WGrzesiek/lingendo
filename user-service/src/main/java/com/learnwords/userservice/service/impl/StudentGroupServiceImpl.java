package com.learnwords.userservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.GroupCreatedEvent;
import com.learnwords.common.events.GroupMemberAddedEvent;
import com.learnwords.common.events.GroupMemberRemovedEvent;
import com.learnwords.userservice.dtos.group.*;
import com.learnwords.userservice.entity.GroupMember;
import com.learnwords.userservice.entity.StudentGroup;
import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.enums.AccountType;
import com.learnwords.userservice.enums.GroupMemberStatus;
import com.learnwords.userservice.enums.GroupStatus;
import com.learnwords.userservice.enums.TeacherStudentStatus;
import com.learnwords.userservice.events.GenericEventProducer;
import com.learnwords.userservice.exception.exceptions.*;
import com.learnwords.userservice.repository.GroupMemberRepository;
import com.learnwords.userservice.repository.StudentGroupRepository;
import com.learnwords.userservice.repository.TeacherStudentRepository;
import com.learnwords.userservice.repository.UserRepository;
import com.learnwords.userservice.service.StudentGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementacja serwisu do zarządzania grupami uczniów.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentGroupServiceImpl implements StudentGroupService {

    private final StudentGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final TeacherStudentRepository teacherStudentRepository;
    private final GenericEventProducer eventProducer;

    @Override
    @Transactional
    public GroupResponse createGroup(String teacherId, CreateGroupRequest request) {
        log.info("Tworzenie grupy '{}' dla nauczyciela: {}", request.name(), teacherId);

        User teacher = findUserById(teacherId);
        validateTeacherRole(teacher);

        StudentGroup group = StudentGroup.builder()
                .id(UUID.randomUUID().toString())
                .name(request.name())
                .description(request.description())
                .color(request.color())
                .teacher(teacher)
                .status(GroupStatus.ACTIVE)
                .build();

        groupRepository.save(group);
        log.info("Utworzono grupę: {} dla nauczyciela: {}", group.getId(), teacherId);

        eventProducer.send(KafkaTopic.GROUP_CREATED, GroupCreatedEvent.builder()
                .eventTime(Instant.now())
                .groupId(group.getId())
                .groupName(group.getName())
                .teacherId(teacherId)
                .receivedAt(Instant.now())
                .build());

        return mapToGroupResponse(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupResponse> getTeacherGroups(String teacherId, boolean includeArchived, int page, int size) {
        log.debug("Pobieranie grup nauczyciela: {}, includeArchived: {}", teacherId, includeArchived);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (includeArchived) {
            return groupRepository.findByTeacherId(teacherId, pageable)
                    .map(this::mapToGroupResponse);
        }

        return groupRepository.findByTeacherIdAndStatus(teacherId, GroupStatus.ACTIVE, pageable)
                .map(this::mapToGroupResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroup(String teacherId, String groupId) {
        log.debug("Pobieranie grupy: {} dla nauczyciela: {}", groupId, teacherId);

        StudentGroup group = findGroupByIdAndTeacher(groupId, teacherId);
        return mapToGroupResponse(group);
    }

    @Override
    @Transactional
    public GroupResponse updateGroup(String teacherId, String groupId, UpdateGroupRequest request) {
        log.info("Aktualizacja grupy: {} przez nauczyciela: {}", groupId, teacherId);

        StudentGroup group = findGroupByIdAndTeacher(groupId, teacherId);

        if (request.name() != null) {
            group.setName(request.name());
        }
        if (request.description() != null) {
            group.setDescription(request.description());
        }
        if (request.color() != null) {
            group.setColor(request.color());
        }

        groupRepository.save(group);
        log.info("Zaktualizowano grupę: {}", groupId);

        return mapToGroupResponse(group);
    }

    @Override
    @Transactional
    public void archiveGroup(String teacherId, String groupId) {
        log.info("Archiwizacja grupy: {} przez nauczyciela: {}", groupId, teacherId);

        StudentGroup group = findGroupByIdAndTeacher(groupId, teacherId);
        
        if (group.getStatus() == GroupStatus.ARCHIVED) {
            throw new InvalidOperationException("Grupa jest już zarchiwizowana");
        }

        group.setStatus(GroupStatus.ARCHIVED);
        groupRepository.save(group);

        log.info("Zarchiwizowano grupę: {}", groupId);
    }

    @Override
    @Transactional
    public void restoreGroup(String teacherId, String groupId) {
        log.info("Przywracanie grupy: {} przez nauczyciela: {}", groupId, teacherId);

        StudentGroup group = findGroupByIdAndTeacher(groupId, teacherId);

        if (group.getStatus() != GroupStatus.ARCHIVED) {
            throw new InvalidOperationException("Tylko zarchiwizowane grupy można przywrócić");
        }

        group.setStatus(GroupStatus.ACTIVE);
        groupRepository.save(group);

        log.info("Przywrócono grupę: {}", groupId);
    }

    @Override
    @Transactional
    public void deleteGroup(String teacherId, String groupId) {
        log.info("Usuwanie grupy: {} przez nauczyciela: {}", groupId, teacherId);

        StudentGroup group = findGroupByIdAndTeacher(groupId, teacherId);
        group.setStatus(GroupStatus.DELETED);
        groupRepository.save(group);

        log.info("Usunięto grupę: {}", groupId);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupStatsResponse getGroupStats(String teacherId) {
        log.debug("Pobieranie statystyk grup dla nauczyciela: {}", teacherId);

        long activeGroups = groupRepository.countByTeacherIdAndStatus(teacherId, GroupStatus.ACTIVE);
        long archivedGroups = groupRepository.countByTeacherIdAndStatus(teacherId, GroupStatus.ARCHIVED);
        long totalGroups = activeGroups + archivedGroups;

        List<String> groupIds = groupRepository.findGroupIdsByTeacherId(teacherId);
        long totalMembers = 0;
        for (String groupId : groupIds) {
            totalMembers += memberRepository.countByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE);
        }

        double averageMembers = totalGroups > 0 ? (double) totalMembers / totalGroups : 0;

        return new GroupStatsResponse(
                totalGroups,
                activeGroups,
                archivedGroups,
                totalMembers,
                averageMembers
        );
    }

    @Override
    @Transactional
    public BatchMemberOperationResponse addMembers(String teacherId, String groupId, AddMembersRequest request) {
        log.info("Dodawanie {} uczniów do grupy: {}", request.studentIds().size(), groupId);

        StudentGroup group = findGroupByIdAndTeacher(groupId, teacherId);
        validateGroupIsActive(group);

        List<String> success = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (String studentId : request.studentIds()) {
            try {
                addMemberToGroup(group, studentId, teacherId);
                success.add(studentId);
            } catch (Exception e) {
                failed.add(studentId);
                errors.add(studentId + ": " + e.getMessage());
                log.warn("Nie udało się dodać ucznia {} do grupy {}: {}", studentId, groupId, e.getMessage());
            }
        }

        log.info("Dodano {}/{} uczniów do grupy: {}", success.size(), request.studentIds().size(), groupId);

        return new BatchMemberOperationResponse(success, failed, errors);
    }

    private void addMemberToGroup(StudentGroup group, String studentId, String teacherId) {
        if (memberRepository.existsByGroupIdAndStudentIdAndStatus(group.getId(), studentId, GroupMemberStatus.ACTIVE)) {
            throw new GroupMemberAlreadyExistsException("Uczeń jest już członkiem tej grupy");
        }

        if (!teacherStudentRepository.existsByTeacherIdAndStudentIdAndStatus(
                teacherId, studentId, TeacherStudentStatus.ACTIVE)) {
            throw new RelationNotFoundException("Uczeń nie jest przypisany do tego nauczyciela");
        }

        User student = findUserById(studentId);
        User teacher = group.getTeacher();

        GroupMember member = GroupMember.builder()
                .id(UUID.randomUUID().toString())
                .group(group)
                .student(student)
                .status(GroupMemberStatus.ACTIVE)
                .addedBy(teacher)
                .build();

        memberRepository.save(member);

        eventProducer.send(KafkaTopic.GROUP_MEMBER_ADDED, GroupMemberAddedEvent.builder()
                .eventTime(Instant.now())
                .groupId(group.getId())
                .groupName(group.getName())
                .teacherId(teacherId)
                .studentId(studentId)
                .receivedAt(Instant.now())
                .build());
    }

    @Override
    @Transactional
    public BatchMemberOperationResponse removeMembers(String teacherId, String groupId, RemoveMembersRequest request) {
        log.info("Usuwanie {} uczniów z grupy: {}", request.studentIds().size(), groupId);

        StudentGroup group = findGroupByIdAndTeacher(groupId, teacherId);

        List<String> success = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (String studentId : request.studentIds()) {
            try {
                removeMemberFromGroup(group, studentId, teacherId);
                success.add(studentId);
            } catch (Exception e) {
                failed.add(studentId);
                errors.add(studentId + ": " + e.getMessage());
                log.warn("Nie udało się usunąć ucznia {} z grupy {}: {}", studentId, groupId, e.getMessage());
            }
        }

        log.info("Usunięto {}/{} uczniów z grupy: {}", success.size(), request.studentIds().size(), groupId);

        return new BatchMemberOperationResponse(success, failed, errors);
    }

    private void removeMemberFromGroup(StudentGroup group, String studentId, String teacherId) {
        GroupMember member = memberRepository.findByGroupIdAndStudentIdAndStatus(
                        group.getId(), studentId, GroupMemberStatus.ACTIVE)
                .orElseThrow(() -> new GroupMemberNotFoundException("Uczeń nie jest członkiem tej grupy"));

        member.setStatus(GroupMemberStatus.REMOVED);
        member.setRemovedAt(Instant.now());
        memberRepository.save(member);

        eventProducer.send(KafkaTopic.GROUP_MEMBER_REMOVED, GroupMemberRemovedEvent.builder()
                .eventTime(Instant.now())
                .groupId(group.getId())
                .teacherId(teacherId)
                .studentId(studentId)
                .reason("REMOVED_BY_TEACHER")
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupMemberResponse> getGroupMembers(String teacherId, String groupId, int page, int size) {
        log.debug("Pobieranie członków grupy: {} dla nauczyciela: {}", groupId, teacherId);

        findGroupByIdAndTeacher(groupId, teacherId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("joinedAt").descending());

        return memberRepository.findByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE, pageable)
                .map(this::mapToGroupMemberResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getGroupMemberIds(String groupId) {
        return memberRepository.findStudentIdsByGroupId(groupId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getStudentIdsFromGroups(List<String> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return List.of();
        }
        return memberRepository.findStudentIdsByGroupIds(groupIds);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<GroupResponse> getStudentGroups(String studentId, int page, int size) {
        log.debug("Pobieranie grup ucznia: {}", studentId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return groupRepository.findGroupsByStudentId(studentId, pageable)
                .map(this::mapToGroupResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStudentInGroup(String studentId, String groupId) {
        return memberRepository.existsByGroupIdAndStudentIdAndStatus(groupId, studentId, GroupMemberStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStudentInAnyGroup(String studentId, List<String> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return false;
        }
        return memberRepository.isStudentInAnyGroup(studentId, groupIds);
    }

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika: " + userId));
    }

    private void validateTeacherRole(User user) {
        if (user.getAccountType() != AccountType.TEACHER) {
            throw new UnauthorizedOperationException("Tylko nauczyciele mogą tworzyć grupy");
        }
    }

    private StudentGroup findGroupByIdAndTeacher(String groupId, String teacherId) {
        return groupRepository.findByIdAndTeacherId(groupId, teacherId)
                .orElseThrow(() -> new GroupNotFoundException("Nie znaleziono grupy: " + groupId));
    }

    private void validateGroupIsActive(StudentGroup group) {
        if (group.getStatus() != GroupStatus.ACTIVE) {
            throw new InvalidOperationException("Operacja dozwolona tylko dla aktywnych grup");
        }
    }

    private GroupResponse mapToGroupResponse(StudentGroup group) {
        String teacherName = group.getTeacher().getFirstName() + " " + group.getTeacher().getLastName();
        int memberCount = (int) memberRepository.countByGroupIdAndStatus(group.getId(), GroupMemberStatus.ACTIVE);

        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getColor(),
                group.getTeacher().getId(),
                teacherName,
                group.getStatus(),
                memberCount,
                group.getCreatedAt(),
                group.getUpdatedAt()
        );
    }

    private GroupMemberResponse mapToGroupMemberResponse(GroupMember member) {
        User student = member.getStudent();
        return new GroupMemberResponse(
                member.getId(),
                student.getId(),
                student.getUsername(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                member.getStatus(),
                member.getJoinedAt()
        );
    }
}
