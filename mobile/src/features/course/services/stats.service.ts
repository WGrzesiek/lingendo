import apiClient from '@/lib/api/axios';
import { ENDPOINTS } from '@/constants';
import type { FlashcardAnswersStats } from '../types';

export const statsService = {
  /**
   * Pobiera statystyki odpowiedzi dla fiszek w kursie
   */
  getFlashcardAnswersStats: async (enrollmentId: string): Promise<FlashcardAnswersStats> => {
    const { data } = await apiClient.get<FlashcardAnswersStats>(
      ENDPOINTS.COURSES.FLASHCARD_STATS(enrollmentId)
    );
    return data;
  },
};
