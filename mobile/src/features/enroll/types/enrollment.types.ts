import type { LearnAlgorithm, ReviewSchedule } from "@/features/deck/types/deck.types";

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

/**
 * Request do aktualizacji algorytmu nauki
 */
export interface UpdateLearnAlgorithmRequest {
  learnAlgorithm: LearnAlgorithm;
}

/**
 * Request do aktualizacji harmonogramu powtórek
 */
export interface UpdateReviewScheduleRequest {
  reviewSchedule: ReviewSchedule;
}

/**
 * Dane enrollment (zapis na kurs)
 */
export interface EnrollmentDto {
  id: string;
  deckId: string;
  userId: string;
  learnAlgorithm: LearnAlgorithm;
  howManyFlashcardsForOneSession: number;
  reviewSchedule: ReviewSchedule;
  createdAt: string;
  updatedAt: string;
}
