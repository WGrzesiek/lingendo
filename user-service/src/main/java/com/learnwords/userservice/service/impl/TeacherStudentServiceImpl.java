package com.learnwords.userservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.TeacherStudentJoinedEvent;
import com.learnwords.common.events.TeacherStudentRemovedEvent;
import com.learnwords.userservice.dtos.teacher.*;
import com.learnwords.userservice.entity.TeacherInvitation;
import com.learnwords.userservice.entity.TeacherStudent;
import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.enums.AccountType;
import com.learnwords.userservice.enums.InvitationStatus;
import com.learnwords.userservice.enums.TeacherStudentStatus;
import com.learnwords.userservice.enums.UserType;
import com.learnwords.userservice.events.GenericEventProducer;
import com.learnwords.userservice.exception.exceptions.*;
import com.learnwords.userservice.repository.TeacherInvitationRepository;
import com.learnwords.userservice.repository.TeacherStudentRepository;
import com.learnwords.userservice.repository.UserRepository;
import com.learnwords.userservice.service.TeacherStudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

/**
 * Implementacja serwisu do zarządzania relacjami nauczyciel-uczeń
 */
@Slf4j
@Service
public class TeacherStudentServiceImpl implements TeacherStudentService {

    private static final String INVITATION_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int INVITATION_CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final TeacherStudentRepository teacherStudentRepository;
    private final TeacherInvitationRepository invitationRepository;
    private final UserRepository userRepository;
//    private final TeacherStudentEventProducer eventProducer;
    private final GenericEventProducer genericEventProducer;

    @Value("${app.invitation.base-url:https://learnwords.app/join/}")
    private String invitationBaseUrl;

    public TeacherStudentServiceImpl(
            TeacherStudentRepository teacherStudentRepository,
            TeacherInvitationRepository invitationRepository,
            UserRepository userRepository,
//            TeacherStudentEventProducer eventProducer,
            GenericEventProducer genericEventProducer){
        this.teacherStudentRepository = teacherStudentRepository;
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
//        this.eventProducer = eventProducer;
        this.genericEventProducer = genericEventProducer;
    }

    // === Operacje nauczyciela - zaproszenia ===

    @Override
    @Transactional
    public InvitationResponse createInvitation(String teacherId, CreateInvitationRequest request) {
        log.info("Tworzenie zaproszenia dla nauczyciela: {}", teacherId);

        User teacher = findUserById(teacherId);
        validateTeacherRole(teacher);

        String code = generateUniqueInvitationCode();

        TeacherInvitation invitation = TeacherInvitation.builder()
                .id(UUID.randomUUID().toString())
                .invitationCode(code)
                .teacher(teacher)
                .name(request.name())
                .maxUses(request.maxUses())
                .expiresAt(request.expiresAt())
                .status(InvitationStatus.ACTIVE)
                .build();

        invitationRepository.save(invitation);
        log.info("Utworzono zaproszenie: {} dla nauczyciela: {}", code, teacherId);

        return mapToInvitationResponse(invitation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvitationResponse> getTeacherInvitations(String teacherId, int page, int size) {
        log.debug("Pobieranie zaproszeń nauczyciela: {}", teacherId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return invitationRepository.findByTeacherId(teacherId, pageable)
                .map(this::mapToInvitationResponse);
    }

    @Override
    @Transactional
    public void deactivateInvitation(String teacherId, String invitationId) {
        log.info("Dezaktywacja zaproszenia: {} przez nauczyciela: {}", invitationId, teacherId);

        TeacherInvitation invitation = findInvitationById(invitationId);
        validateInvitationOwnership(invitation, teacherId);

        invitation.setStatus(InvitationStatus.DEACTIVATED);
        invitationRepository.save(invitation);

        log.info("Dezaktywowano zaproszenie: {}", invitationId);
    }

    @Override
    @Transactional
    public void deleteInvitation(String teacherId, String invitationId) {
        log.info("Usuwanie zaproszenia: {} przez nauczyciela: {}", invitationId, teacherId);

        TeacherInvitation invitation = findInvitationById(invitationId);
        validateInvitationOwnership(invitation, teacherId);

        invitationRepository.delete(invitation);
        log.info("Usunięto zaproszenie: {}", invitationId);
    }

    // === Operacje nauczyciela - uczniowie ===

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponse> getStudents(String teacherId, int page, int size) {
        log.debug("Pobieranie uczniów nauczyciela: {}", teacherId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return teacherStudentRepository
                .findStudentsByTeacherId(teacherId, TeacherStudentStatus.ACTIVE, pageable)
                .map(this::mapToStudentResponse);
    }

    @Override
    @Transactional
    public void removeStudent(String teacherId, String studentId) {
        log.info("Usuwanie ucznia: {} przez nauczyciela: {}", studentId, teacherId);

        TeacherStudent relation = findRelation(teacherId, studentId);
        teacherStudentRepository.delete(relation);

        genericEventProducer.send(KafkaTopic.TEACHER_STUDENT_REMOVED, TeacherStudentRemovedEvent.builder()
                .eventTime(Instant.now())
                .teacherId(teacherId)
                .studentId(studentId)
                .reason("REMOVED_BY_TEACHER")
                .build());

        log.info("Usunięto relację nauczyciel-uczeń: {} - {}", teacherId, studentId);
    }

    @Override
    @Transactional
    public void blockStudent(String teacherId, String studentId) {
        log.info("Blokowanie ucznia: {} przez nauczyciela: {}", studentId, teacherId);

        TeacherStudent relation = findRelation(teacherId, studentId);
        relation.setStatus(TeacherStudentStatus.BLOCKED);
        teacherStudentRepository.save(relation);

        genericEventProducer.send(KafkaTopic.TEACHER_STUDENT_REMOVED, TeacherStudentRemovedEvent.builder()
                .eventTime(Instant.now())
                .teacherId(teacherId)
                .studentId(studentId)
                .reason("BLOCKED")
                .build());

        log.info("Zablokowano ucznia: {} przez nauczyciela: {}", studentId, teacherId);
    }

    @Override
    @Transactional
    public void unblockStudent(String teacherId, String studentId) {
        log.info("Odblokowanie ucznia: {} przez nauczyciela: {}", studentId, teacherId);

        TeacherStudent relation = teacherStudentRepository
                .findByTeacherIdAndStudentIdAndStatus(teacherId, studentId, TeacherStudentStatus.BLOCKED)
                .orElseThrow(() -> new RelationNotFoundException(
                        "Nie znaleziono zablokowanego ucznia: " + studentId));

        relation.setStatus(TeacherStudentStatus.ACTIVE);
        teacherStudentRepository.save(relation);

        log.info("Odblokowano ucznia: {} przez nauczyciela: {}", studentId, teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherStatsResponse getTeacherStats(String teacherId) {
        log.debug("Pobieranie statystyk nauczyciela: {}", teacherId);

        long activeStudents = teacherStudentRepository.countByTeacherIdAndStatus(
                teacherId, TeacherStudentStatus.ACTIVE);
        long invitedStudents = teacherStudentRepository.countByTeacherIdAndStatus(
                teacherId, TeacherStudentStatus.INVITED);
        long blockedStudents = teacherStudentRepository.countByTeacherIdAndStatus(
                teacherId, TeacherStudentStatus.BLOCKED);
        long activeInvitations = invitationRepository.countByTeacherIdAndStatus(
                teacherId, InvitationStatus.ACTIVE);
        long totalInvitations = invitationRepository.countByTeacherId(teacherId);

        return new TeacherStatsResponse(
                activeStudents,
                invitedStudents,
                blockedStudents,
                activeInvitations,
                totalInvitations
        );
    }


    @Override
    @Transactional
    public TeacherResponse joinTeacher(String studentId, String invitationCode) {
        log.info("Uczeń: {} próbuje dołączyć za pomocą kodu: {}", studentId, invitationCode);

        TeacherInvitation invitation = invitationRepository
                .findByInvitationCodeAndStatus(invitationCode, InvitationStatus.ACTIVE)
                .orElseThrow(() -> new InvalidInvitationException(
                        "Kod zaproszenia jest nieprawidłowy lub nieaktywny"));

        if (!invitation.isValid()) {
            throw new InvalidInvitationException("Zaproszenie wygasło lub zostało wykorzystane");
        }

        User student = findUserById(studentId);
        User teacher = invitation.getTeacher();

        if (teacher.getId().equals(studentId)) {
            throw new InvalidOperationException("Nie możesz dołączyć do siebie jako uczeń");
        }

        if (teacherStudentRepository.existsByTeacherIdAndStudentId(teacher.getId(), studentId)) {
            throw new RelationAlreadyExistsException(
                    "Jesteś już uczniem tego nauczyciela lub masz oczekujące zaproszenie");
        }

        TeacherStudent relation = TeacherStudent.builder()
                .id(UUID.randomUUID().toString())
                .teacher(teacher)
                .student(student)
                .status(TeacherStudentStatus.ACTIVE)
                .acceptedAt(Instant.now())
                .build();

        teacherStudentRepository.save(relation);

        invitation.incrementUses();
        invitationRepository.save(invitation);

        genericEventProducer.send(KafkaTopic.TEACHER_STUDENT_JOINED, TeacherStudentJoinedEvent.builder()
                .eventTime(Instant.now())
                .teacherId(teacher.getId())
                .studentId(studentId)
                .studentUsername(student.getUsername())
                .build());

        log.info("Uczeń: {} dołączył do nauczyciela: {}", studentId, teacher.getId());

        return mapToTeacherResponse(relation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherResponse> getMyTeachers(String studentId, int page, int size) {
        log.debug("Pobieranie nauczycieli ucznia: {}", studentId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return teacherStudentRepository
                .findTeachersByStudentId(studentId, TeacherStudentStatus.ACTIVE, pageable)
                .map(this::mapToTeacherResponse);
    }

    @Override
    @Transactional
    public void leaveTeacher(String studentId, String teacherId) {
        log.info("Uczeń: {} opuszcza nauczyciela: {}", studentId, teacherId);

        TeacherStudent relation = teacherStudentRepository
                .findByTeacherIdAndStudentId(teacherId, studentId)
                .orElseThrow(() -> new RelationNotFoundException(
                        "Nie znaleziono relacji z tym nauczycielem"));

        teacherStudentRepository.delete(relation);

        genericEventProducer.send(KafkaTopic.TEACHER_STUDENT_REMOVED, TeacherStudentRemovedEvent.builder()
                .eventTime(Instant.now())
                .teacherId(teacherId)
                .studentId(studentId)
                .reason("LEFT_BY_STUDENT")
                .build());

        log.info("Uczeń: {} opuścił nauczyciela: {}", studentId, teacherId);
    }

    // === Wspólne ===

    @Override
    @Transactional(readOnly = true)
    public boolean isTeacherOf(String teacherId, String studentId) {
        return teacherStudentRepository.existsByTeacherIdAndStudentIdAndStatus(
                teacherId, studentId, TeacherStudentStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public InvitationResponse getInvitationInfo(String invitationCode) {
        log.debug("Pobieranie informacji o zaproszeniu: {}", invitationCode);

        TeacherInvitation invitation = invitationRepository
                .findByInvitationCode(invitationCode)
                .orElseThrow(() -> new InvalidInvitationException("Nie znaleziono zaproszenia"));

        return mapToInvitationResponse(invitation);
    }

    private String generateUniqueInvitationCode() {
        String code;
        int attempts = 0;
        do {
            code = generateRandomCode();
            attempts++;
            if (attempts > 10) {
                throw new RuntimeException("Nie udało się wygenerować unikalnego kodu zaproszenia");
            }
        } while (invitationRepository.existsByInvitationCode(code));
        return code;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(INVITATION_CODE_LENGTH);
        for (int i = 0; i < INVITATION_CODE_LENGTH; i++) {
            sb.append(INVITATION_CODE_CHARS.charAt(RANDOM.nextInt(INVITATION_CODE_CHARS.length())));
        }
        return sb.toString();
    }

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika: " + userId));
    }

    private TeacherInvitation findInvitationById(String invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvalidInvitationException(
                        "Nie znaleziono zaproszenia: " + invitationId));
    }

    private TeacherStudent findRelation(String teacherId, String studentId) {
        return teacherStudentRepository
                .findByTeacherIdAndStudentId(teacherId, studentId)
                .orElseThrow(() -> new RelationNotFoundException(
                        "Nie znaleziono relacji nauczyciel-uczeń"));
    }

    private void validateTeacherRole(User user) {
        if (user.getAccountType() == AccountType.TEACHER) {
            throw new UnauthorizedOperationException("Użytkownik nie jest nauczycielem");
        }
    }

    private void validateInvitationOwnership(TeacherInvitation invitation, String teacherId) {
        if (!invitation.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedOperationException("Nie masz uprawnień do tego zaproszenia");
        }
    }

    private InvitationResponse mapToInvitationResponse(TeacherInvitation invitation) {
        return new InvitationResponse(
                invitation.getId(),
                invitation.getInvitationCode(),
                invitationBaseUrl + invitation.getInvitationCode(),
                invitation.getName(),
                invitation.getMaxUses(),
                invitation.getCurrentUses(),
                invitation.getStatus(),
                invitation.getExpiresAt(),
                invitation.getCreatedAt()
        );
    }

    private StudentResponse mapToStudentResponse(TeacherStudent relation) {
        User student = relation.getStudent();
        return new StudentResponse(
                relation.getId(),
                student.getId(),
                student.getUsername(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                relation.getStatus(),
                relation.getAcceptedAt()
        );
    }

    private TeacherResponse mapToTeacherResponse(TeacherStudent relation) {
        User teacher = relation.getTeacher();
        return new TeacherResponse(
                relation.getId(),
                teacher.getId(),
                teacher.getUsername(),
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getEmail(),
                relation.getStatus(),
                relation.getAcceptedAt()
        );
    }
}
