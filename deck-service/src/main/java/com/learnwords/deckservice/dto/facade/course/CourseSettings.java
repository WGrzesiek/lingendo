package com.learnwords.deckservice.dto.facade.course;

import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.enums.ReviewSchedule;

public record CourseSettings (
        String enrollmentId,
        LearnAlgorithm algorithm,
        Long wordsPerSession,
        ReviewSchedule reviewSchedule
){
}
