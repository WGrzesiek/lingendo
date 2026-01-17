import apiClient from '@/lib/api/axios';
import { ENDPOINTS } from '@/constants';
import type { EnrollmentDto, UpdateEnrollmentSettingsRequest } from '../types';
import type { LearnAlgorithm, ReviewSchedule } from '@/features/deck/types';

export const enrollmentService = {
  /**
   * Zapisuje użytkownika do talii
   */
  enrollToDeck: async (deckId: string): Promise<EnrollmentDto> => {
    const { data } = await apiClient.post<EnrollmentDto>(ENDPOINTS.ENROLLMENT.ENROLL(deckId));
    return data;
  },

  /**
   * Wypisuje użytkownika z talii
   */
  unenrollFromDeck: async (deckId: string): Promise<void> => {
    await apiClient.delete(ENDPOINTS.ENROLLMENT.UNENROLL(deckId));
  },

  /**
   * Aktualizuje algorytm nauki dla zapisu
   */
  updateLearnAlgorithm: async (
    enrollmentId: string,
    algorithm: LearnAlgorithm
  ): Promise<EnrollmentDto> => {
    const { data } = await apiClient.patch<EnrollmentDto>(
      ENDPOINTS.ENROLLMENT.ALGORITHM(enrollmentId),
      { algorithm }
    );
    return data;
  },

  /**
   * Aktualizuje liczbę fiszek na sesję dla zapisu
   */
  updateFlashcardsPerSession: async (
    enrollmentId: string,
    flashcardsPerSession: number
  ): Promise<EnrollmentDto> => {
    const { data } = await apiClient.patch<EnrollmentDto>(
      ENDPOINTS.ENROLLMENT.SESSION_LIMIT(enrollmentId),
      { flashcardsPerSession }
    );
    return data;
  },

  /**
   * Aktualizuje harmonogram powtórek dla zapisu
   */
  updateReviewSchedule: async (
    enrollmentId: string,
    reviewSchedule: ReviewSchedule
  ): Promise<EnrollmentDto> => {
    const { data } = await apiClient.patch<EnrollmentDto>(
      ENDPOINTS.ENROLLMENT.REVIEW_SCHEDULE(enrollmentId),
      { reviewSchedule }
    );
    return data;
  },

  /**
   * Aktualizuje wszystkie ustawienia zapisu naraz
   */
  updateEnrollmentSettings: async (
    enrollmentId: string,
    settings: UpdateEnrollmentSettingsRequest
  ): Promise<EnrollmentDto> => {
    const { data } = await apiClient.patch<EnrollmentDto>(`/enrollments/${enrollmentId}`, settings);
    return data;
  },
};
