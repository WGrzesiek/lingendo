package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TeacherStudentJoinedEvent(
        Instant eventTime,
        String teacherId,
        String teacherUsername,
        String studentId,
        String studentUsername,
        String invitationCode,
        Instant receivedAt
) implements DomainEvent {}
