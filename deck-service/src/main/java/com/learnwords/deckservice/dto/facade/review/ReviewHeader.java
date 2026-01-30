package com.learnwords.deckservice.dto.facade.review;

public record ReviewHeader(
    String enrollmentId,
    ReviewCounters counters
){
}
