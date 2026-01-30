package com.learnwords.userservice.dtos.group;

/**
 * DTO odpowiedzi ze statystykami grup nauczyciela
 *
 * @param totalGroups     całkowita liczba grup
 * @param activeGroups    liczba aktywnych grup
 * @param archivedGroups  liczba zarchiwizowanych grup
 * @param totalMembers    całkowita liczba członków we wszystkich grupach
 * @param averageMembers  średnia liczba członków na grupę
 */
public record GroupStatsResponse(
        long totalGroups,
        long activeGroups,
        long archivedGroups,
        long totalMembers,
        double averageMembers
) {
}
