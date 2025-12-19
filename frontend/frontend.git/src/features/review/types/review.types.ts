import type { WordDto } from "@/types/word";

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

/**
 * Statystyki powtórek kursu
 */
export interface ReviewStats {
  totalWordsToReview: number;
  wordsForToday: number;
  overdueWords: number;
  averageDifficulty: number;
}

/**
 * Wynik powtórki słówka
 */
export interface ReviewResult {
  wordId: string;
  isCorrect: boolean;
  responseTimeMs: number;
  answeredAt: string;
}

/**
 * Sesja powtórek
 */
export interface ReviewSession {
  id: string;
  courseId: string;
  courseTitle: string;
  words: ReviewWord[];
  startedAt: string;
}

//================================

export interface ReviewCounters{
  totalWordsToReview: number
  wordsForToday: number
  overdueWords: number
}

export interface ReviewHeader{
  enrollmentId: string
  counters: ReviewCounters
}