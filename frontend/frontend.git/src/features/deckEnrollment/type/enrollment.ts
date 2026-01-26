/**
 * Dostępne algorytmy nauki
 */
export type LearnAlgorithm =
  | "GRZESIEK_ALGORITHM"
  | "LEITNER_ALGORITHM"
  | "TEST_ALGORITHM";

/**
 * Request do zapisu na talię (enrollment).
 * Wszystkie pola są opcjonalne - jeśli nie podano, backend użyje wartości domyślnych z talii.
 */
export interface CreateEnrollmentRequest {
  preferredAlgorithm?: LearnAlgorithm;
  howManyFlashcardsForOneSession?: number;
}

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
