package com.learnwords.userservice.dtos.teacher;

/**
 * DTO ze statystykami nauczyciela
 *
 * @param activeStudents    liczba aktywnych uczniów
 * @param invitedStudents   liczba zaproszonych (oczekujących) uczniów
 * @param blockedStudents   liczba zablokowanych uczniów
 * @param activeInvitations liczba aktywnych zaproszeń
 * @param totalInvitations  łączna liczba zaproszeń
 */
public record TeacherStatsResponse(
        long activeStudents,
        long invitedStudents,
        long blockedStudents,
        long activeInvitations,
        long totalInvitations
) {
}
