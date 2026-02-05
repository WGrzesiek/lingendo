import apiClient from '@/lib/api/axios';
import { ENDPOINTS } from '@/constants';
import type {
  CreateEnrollmentRequest,
  EnrollmentDto,
  UpdateEnrollmentSettingsRequest,
} from '../types';
import type { LearnAlgorithm, ReviewSchedule } from '@/features/deck/types';

export const enrollmentService = {
  /**
   * Zapisuje użytkownika do talii
   */
  enrollToDeck: async (deckId: string,  body: CreateEnrollmentRequest = {}): Promise<EnrollmentDto> => {
    const { data } = await apiClient.post<EnrollmentDto>(ENDPOINTS.ENROLLMENT.ENROLL(deckId),body);
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
    const { data } = await apiClient.put<EnrollmentDto>(
      ENDPOINTS.ENROLLMENT.ALGORITHM(enrollmentId, algorithm)
    );
    return data;
  },

  /**
   * Aktualizuje liczbę fiszek na sesję dla zapisu
   */
  updateFlashcardsPerSession: async (
    enrollmentId: string,
    limit: number
  ): Promise<EnrollmentDto> => {
    const { data } = await apiClient.put<EnrollmentDto>(
      ENDPOINTS.ENROLLMENT.SESSION_LIMIT(enrollmentId, limit),
    );
    return data;
  },

  /**
   * Aktualizuje harmonogram powtórek dla zapisu
   */
  updateReviewSchedule: async (
    enrollmentId: string,
    mode: ReviewSchedule
  ): Promise<EnrollmentDto> => {
    const { data } = await apiClient.put<EnrollmentDto>(
      ENDPOINTS.ENROLLMENT.REVIEW_SCHEDULE(enrollmentId, mode),
    );
    return data;
  },
};
