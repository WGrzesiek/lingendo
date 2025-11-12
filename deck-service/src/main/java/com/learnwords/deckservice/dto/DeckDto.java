package com.learnwords.deckservice.dto;

public record DeckDto(String id, String name, boolean isPublic, String userId, String ownerType, int wordCount) {
}
