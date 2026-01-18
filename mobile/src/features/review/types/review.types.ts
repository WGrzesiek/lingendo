import type { WordDto } from '@/features/course/types';

/**
 * Liczniki powtórek
 */
export interface ReviewCounters {
  totalWordsToReview: number;
  wordsForToday: number;
  overdueWords: number;
}

/**
 * Nagłówek powtórek (statystyki)
 */
export interface ReviewHeader {
  enrollmentId: string;
  counters: ReviewCounters;
}

/**
 * Słówko gotowe do powtórki
 */
export interface ReviewWord {
  id: string;
  content: WordDto;
  lastReviewAt: string;
  nextReviewAt: string;
  repetitionCount: number;
  difficultyLevel: number;
}
