export interface FlashcardAnswersStats{
    enrollmentId: string;
    totalAnswers: number;
    correctAnswers: number;
    incorrectAnswers: number;
    accuracy: number;
    averageResponseTime: number;
    totalStudyTime: number;
    fastestResponse: number;
    slowestResponse: number;
    lastSessionDate: number;
    until30SecAnswers: number;
}