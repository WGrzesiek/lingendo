package com.learnwords.userservice.dtos.friendship;

import com.learnwords.userservice.enums.FriendshipStatus;

import java.time.Instant;

/**
 * DTO odpowiedzi z informacją o przyjacielu
 *
 * @param friendshipId identyfikator relacji
 * @param friendId     identyfikator przyjaciela
 * @param username     nazwa użytkownika przyjaciela
 * @param firstName    imię przyjaciela
 * @param lastName     nazwisko przyjaciela
 * @param email        email przyjaciela
 * @param status       status relacji
 * @param friendsSince data nawiązania przyjaźni
 */
public record FriendResponse(
        String friendshipId,
        String friendId,
        String username,
        String firstName,
        String lastName,
        String email,
        FriendshipStatus status,
        Instant friendsSince
) {
}
