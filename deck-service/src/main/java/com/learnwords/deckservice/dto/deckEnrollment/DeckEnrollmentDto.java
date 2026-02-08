package com.learnwords.deckservice.dto.deckEnrollment;

import com.learnwords.deckservice.enums.DeckEnrollmentRole;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.enums.ReviewSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckEnrollmentDto {

    private String id;
    private String deckId;
    private String deckName;
    private String userId;
    private DeckEnrollmentRole role;
    private LearnAlgorithm preferredAlgorithm;
    private ReviewSchedule preferredReviewSchedule;
    private Long cardsPerSessionLimit;
    private int totalFlashcardsCount;
    private int learnedFlashcardsCount;
    private double completionPercentage;
    private Instant joinedAt;
    private Instant lastAccessedAt;
}
