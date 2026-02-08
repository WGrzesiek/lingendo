package com.learnwords.userservice.dtos.teacher;

import com.learnwords.userservice.enums.TeacherStudentStatus;

import java.time.Instant;

/**
 * DTO odpowiedzi z informacją o uczniu nauczyciela
 *
 * @param relationId identyfikator relacji
 * @param studentId  identyfikator ucznia
 * @param username   nazwa użytkownika ucznia
 * @param firstName  imię ucznia
 * @param lastName   nazwisko ucznia
 * @param email      email ucznia
 * @param status     status relacji
 * @param joinedAt   data dołączenia (akceptacji zaproszenia)
 */
public record StudentResponse(
        String relationId,
        String studentId,
        String username,
        String firstName,
        String lastName,
        String email,
        TeacherStudentStatus status,
        Instant joinedAt
) {
}
