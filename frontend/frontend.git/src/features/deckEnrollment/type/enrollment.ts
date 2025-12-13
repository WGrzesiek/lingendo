/**
 * Request do aktualizacji liczby fiszek na sesję
 */
export interface UpdateFlashcardsPerSessionRequest {
    limit: number;
}

export interface UpdateLearnAlgorithmRequest {
    learnAlgorithm: string;
}

export interface UpdateReviewScheduleRequest {
    reviewSchedule: string;
}