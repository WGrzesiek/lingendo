package com.learnwords.userservice.dtos.friendship;

/**
 * DTO odpowiedzi z podstawowymi informacjami o użytkowniku (do wyszukiwania)
 *
 * @param userId       identyfikator użytkownika
 * @param username     nazwa użytkownika
 * @param firstName    imię
 * @param lastName     nazwisko
 * @param isFriend     czy jest już znajomym
 * @param hasPendingRequest czy jest oczekujące zaproszenie
 */
public record UserSearchResponse(
        String userId,
        String username,
        String firstName,
        String lastName,
        boolean isFriend,
        boolean hasPendingRequest
) {
}
