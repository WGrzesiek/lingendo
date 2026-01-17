import apiClient from '@/lib/api/axios';
import { ENDPOINTS } from '@/constants';
import type {
  NextFlashcardRecommendation,
  SubmitAnswerRequest,
  FlashcardInteractionResult,
  LearnHeaderProgress,
} from '../types';

export const learningService = {
  /**
   * Pobiera następną fiszkę do nauki
   */
  getNextFlashcard: async (sessionId: string): Promise<NextFlashcardRecommendation> => {
    const { data } = await apiClient.get<NextFlashcardRecommendation>(
      ENDPOINTS.LEARNING.NEXT_FLASHCARD(sessionId)
    );
    return data;
  },

  /**
   * Przesyła odpowiedź użytkownika na fiszkę
   */
  submitAnswer: async (
    sessionId: string,
    flashcardId: string,
    answer: SubmitAnswerRequest
  ): Promise<FlashcardInteractionResult> => {
    const { data } = await apiClient.post<FlashcardInteractionResult>(
      ENDPOINTS.LEARNING.SUBMIT_ANSWER(sessionId, flashcardId),
      answer
    );
    return data;
  },

  /**
   * Pobiera postęp sesji nauki (dla nagłówka)
   */
  getLearnHeaderProgress: async (sessionId: string): Promise<LearnHeaderProgress> => {
    const { data } = await apiClient.get<LearnHeaderProgress>(
      ENDPOINTS.LEARNING.HEADER_PROGRESS(sessionId)
    );
    return data;
  },

  /**
   * Kończy sesję nauki
   */
  completeSession: async (sessionId: string): Promise<void> => {
    await apiClient.put(ENDPOINTS.LEARNING.COMPLETE_SESSION(sessionId));
  },
};
