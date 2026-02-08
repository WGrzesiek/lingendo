package com.learnwords.userservice.dtos.friendship;

import com.learnwords.userservice.enums.FriendshipStatus;

import java.time.Instant;

/**
 * DTO odpowiedzi z informacją o zaproszeniu do znajomych
 *
 * @param friendshipId identyfikator relacji
 * @param fromUserId   identyfikator użytkownika, który wysłał zaproszenie
 * @param fromUsername nazwa użytkownika, który wysłał zaproszenie
 * @param fromFirstName imię użytkownika
 * @param fromLastName  nazwisko użytkownika
 * @param toUserId     identyfikator użytkownika, do którego wysłano zaproszenie
 * @param toUsername   nazwa użytkownika, do którego wysłano
 * @param status       status zaproszenia
 * @param sentAt       data wysłania zaproszenia
 */
public record FriendRequestResponse(
        String friendshipId,
        String fromUserId,
        String fromUsername,
        String fromFirstName,
        String fromLastName,
        String toUserId,
        String toUsername,
        FriendshipStatus status,
        Instant sentAt
) {
}
