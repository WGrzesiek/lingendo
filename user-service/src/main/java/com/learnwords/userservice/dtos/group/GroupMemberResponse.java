package com.learnwords.userservice.dtos.group;

import com.learnwords.userservice.enums.GroupMemberStatus;

import java.time.Instant;

/**
 * DTO odpowiedzi z informacją o członku grupy
 *
 * @param id          identyfikator członkostwa
 * @param studentId   identyfikator ucznia
 * @param username    nazwa użytkownika ucznia
 * @param firstName   imię ucznia
 * @param lastName    nazwisko ucznia
 * @param email       email ucznia
 * @param status      status członkostwa
 * @param joinedAt    data dołączenia do grupy
 */
public record GroupMemberResponse(
        String id,
        String studentId,
        String username,
        String firstName,
        String lastName,
        String email,
        GroupMemberStatus status,
        Instant joinedAt
) {
}
