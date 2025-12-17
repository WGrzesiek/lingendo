package com.learnwords.statisticsservice.dto.course;

public record FlashcardAnswersStats(
        String enrollmentId,
        int totalAnswers,
        int correctAnswers,
        int incorrectAnswers,
        int accuracy,
        int averageResponseTime,
        int totalStudyTime,
        int fastestResponse,
        int slowestResponse,
        int lastSessionDate
) {
}
