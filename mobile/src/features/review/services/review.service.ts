import apiClient from '@/lib/api/axios';
import { ENDPOINTS } from '@/constants';
import type { ReviewHeader } from '../types';
import type { PageResponse } from '@/types/common';
import type { CourseWord, CourseContentItem } from '@/features/course/types';
import type {
  NextFlashcardRecommendation,
  FlashcardInteractionResult,
  TypingAnswer,
} from '@/features/learning';

export const reviewService = {
  /**
   * Pobiera nagłówek powtórek (statystyki)
   */
  getReviewHeader: async (enrollmentId: string): Promise<ReviewHeader> => {
    const { data } = await apiClient.get<ReviewHeader>(ENDPOINTS.REVIEW.HEADER(enrollmentId));
    console.log('[reviewService] Pobrano nagłówek powtórek:', data);
    return data;
  },

  /**
   * Pobiera listę słówek do powtórki (paginacja)
   */
  getReviewWords: async (
    enrollmentId: string,
    params?: { page?: number; size?: number }
  ): Promise<PageResponse<CourseWord>> => {
    const { data: response } = await apiClient.get<PageResponse<CourseContentItem>>(
      ENDPOINTS.REVIEW.WORDS(enrollmentId),
      { params }
    );

    const words: CourseWord[] = (response.content || []).map((item) => {
      const flashcard = item.flashcard;
      const progress = item.userFlashcardProgress;
      const session = item.sessionNumber ?? 0;

      return {
        id: flashcard.wordDto.id,
        flashcardId: flashcard.id,
        word: flashcard.wordDto.word,
        translations: flashcard.wordDto.translations,
        sentences: flashcard.wordDto.sentences,
        sentencesAI: flashcard.wordDto.sentencesAI,
        phase: progress?.phase || 'NEW',
        isLearned: progress?.isLearned || false,
        isSkipped: progress?.isSkipped || false,
        repetitionCount: progress?.repetitionCount || 0,
        nextReviewAt: progress?.nextReviewAt || null,
        algorithmState: progress?.algorithmState || '',
        sessionNumber: session,
      };
    });

    return {
      content: words,
      totalElements: response.totalElements,
      totalPages: response.totalPages,
      number: response.number,
      size: response.size,
      first: response.first,
      last: response.last,
      empty: response.empty,
    };
  },

  /**
   * Pobiera następną fiszkę do powtórki
   */
  getNextFlashcard: async (enrollmentId: string): Promise<NextFlashcardRecommendation> => {
    const { data } = await apiClient.get<NextFlashcardRecommendation>(
      ENDPOINTS.REVIEW.NEXT_FLASHCARD(enrollmentId)
    );
    console.log('[reviewService] Pobrano następną fiszkę do powtórki');
    return data;
  },

  /**
   * Przesyła odpowiedź na pytanie powtórkowe
   */
  submitAnswer: async (
    flashcardId: string,
    answer: TypingAnswer
  ): Promise<FlashcardInteractionResult> => {
    const { data } = await apiClient.post<FlashcardInteractionResult>(
      ENDPOINTS.REVIEW.SUBMIT_ANSWER(flashcardId),
      answer
    );
    console.log('[reviewService] Przesłano odpowiedź powtórki');
    return data;
  },
};
