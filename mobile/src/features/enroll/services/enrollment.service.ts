import apiClient from '@/lib/api/axios';
import type {
  CreateEnrollmentRequest,
  UpdateFlashcardsPerSessionRequest,
  UpdateLearnAlgorithmRequest,
  UpdateReviewScheduleRequest,
} from '../types';

const BASE_URL = '/v1/decks/enrollments';
const DECKS_URL = '/v1/decks';

export const enrollmentService = {
  /**
   * Zapisuje użytkownika na talię (kurs)
   * Body jest opcjonalne - jeśli nie podano, backend użyje wartości domyślnych z talii.
   */
  enrollToDeck: async (deckId: string, data: CreateEnrollmentRequest = {}): Promise<void> => {
    await apiClient.post(`${DECKS_URL}/${deckId}/enrollments`, data);
    console.log('[Enrollment Service] Zapisano na talię:', deckId);
  },

  /**
   * Wypisuje użytkownika z talii (kursu)
   */
  unenrollFromDeck: async (enrollmentId: string): Promise<void> => {
    await apiClient.delete(`${BASE_URL}/${enrollmentId}`);
    console.log('[Enrollment Service] Wypisano z enrollment:', enrollmentId);
  },

  /**
   * Zmienia algorytm nauki dla enrollmentu
   */
  updateLearnAlgorithm: async (
    enrollmentId: string,
    data: UpdateLearnAlgorithmRequest
  ): Promise<void> => {
    await apiClient.put(`${BASE_URL}/${enrollmentId}/algorithm?algorithm=${data.learnAlgorithm}`);
    console.log('[Enrollment Service] Zmieniono algorytm nauki:', data.learnAlgorithm);
  },

  /**
   * Zmienia liczbę fiszek na sesję (1-100)
   */
  updateFlashcardsPerSession: async (
    enrollmentId: string,
    data: UpdateFlashcardsPerSessionRequest
  ): Promise<void> => {
    await apiClient.put(`${BASE_URL}/${enrollmentId}/session-limit?limit=${data.limit}`);
    console.log('[Enrollment Service] Zmieniono limit fiszek:', data.limit);
  },

  /**
   * Zmienia harmonogram powtórek
   */
  updateReviewSchedule: async (
    enrollmentId: string,
    data: UpdateReviewScheduleRequest
  ): Promise<void> => {
    await apiClient.put(`${BASE_URL}/${enrollmentId}/review-schedule?mode=${data.reviewSchedule}`);
    console.log('[Enrollment Service] Zmieniono harmonogram powtórek:', data.reviewSchedule);
  },
};
