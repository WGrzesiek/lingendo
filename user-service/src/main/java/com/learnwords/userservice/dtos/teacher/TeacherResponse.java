package com.learnwords.userservice.dtos.teacher;

import com.learnwords.userservice.enums.TeacherStudentStatus;

import java.time.Instant;

/**
 * DTO odpowiedzi z informacją o nauczycielu ucznia
 *
 * @param relationId identyfikator relacji
 * @param teacherId  identyfikator nauczyciela
 * @param username   nazwa użytkownika nauczyciela
 * @param firstName  imię nauczyciela
 * @param lastName   nazwisko nauczyciela
 * @param email      email nauczyciela
 * @param status     status relacji
 * @param joinedAt   data dołączenia
 */
public record TeacherResponse(
        String relationId,
        String teacherId,
        String username,
        String firstName,
        String lastName,
        String email,
        TeacherStudentStatus status,
        Instant joinedAt
) {
}
