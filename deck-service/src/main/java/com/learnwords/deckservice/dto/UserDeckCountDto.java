package com.learnwords.deckservice.dto;

public record UserDeckCountDto(
    String userId,
    long totalDecks,
    long publicDecks,
    long privateDecks
) {
}
