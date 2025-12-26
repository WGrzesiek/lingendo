package com.learnwords.userservice.dtos.friendship;

/**
 * DTO ze statystykami znajomych użytkownika
 *
 * @param totalFriends       łączna liczba znajomych
 * @param pendingReceived    liczba oczekujących zaproszeń (otrzymanych)
 * @param pendingSent        liczba wysłanych zaproszeń (oczekujących)
 * @param blockedUsers       liczba zablokowanych użytkowników
 */
public record FriendshipStatsResponse(
        long totalFriends,
        long pendingReceived,
        long pendingSent,
        long blockedUsers
) {
}
