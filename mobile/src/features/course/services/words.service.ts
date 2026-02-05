import apiClient from '@/lib/api/axios';
import { ENDPOINTS } from '@/constants';
import type { PageResponse } from '@/types/common';
import type { CourseWord, CourseContentItem } from '../types';


export const wordsService = {
  /**
   * Pobiera paginowaną listę słówek dla kursu
   * Mapuje CourseContentItem na CourseWord
   */
  getCourseWords: async (
    enrollmentId: string,
    params?: { page?: number; size?: number }
  ): Promise<PageResponse<CourseWord>> => {
    const { data } = await apiClient.get<PageResponse<CourseContentItem>>(
      ENDPOINTS.COURSES.WORDS(enrollmentId),
      { params }
    );

    // Mapowanie elementów content[] na CourseWord
    const words: CourseWord[] = (data.content || []).map((item) => {
      const flashcard = item.flashcard;
      const progress = item.userFlashcardProgress;
      const session = item.sessionNumber ?? 0;
      const wordDto = flashcard?.wordDto;

      return {
        id: wordDto?.id ?? flashcard?.id ?? '',
        flashcardId: flashcard?.id ?? '',
        word: wordDto?.word ?? '',
        translations: wordDto?.translations ?? [],
        sentences: wordDto?.sentences ?? [],
        sentencesAI: wordDto?.sentencesAI ?? [],
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
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      number: data.number,
      size: data.size,
      first: data.first,
      last: data.last,
      empty: data.empty,
    };
  },
};
