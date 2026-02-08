export interface SentenceDto {
  id: string;
  sentence: string;
  translation: string;
}

export interface WordDto {
  id: string;
  word: string;
  translations: string[];
  sentences: SentenceDto[];
  sentencesAI: SentenceDto[];
}

export interface FlashcardDto {
  id: string;
  wordDto: WordDto;
}

export interface SessionNumber {
  flashcardId: string;
  sessionNumber: number;
}

export interface UserFlashcardProgressDto {
  id: string;
  flashcardId: string;
  enrollmentId: string;
  userId: string;
  phase: "NEW" | "LEARNING" | "REVIEW" | "RELEARNING";
  isLearned: boolean;
  isSkipped: boolean;
  repetitionCount: number;
  nextReviewAt: string | null;
  algorithmState: string;
}

import type { PageResponse } from "@/types/common";

export interface CourseContentItem {
  flashcard: FlashcardDto;
  userFlashcardProgress: UserFlashcardProgressDto;
  sessionNumber: number | null;
}

export type CourseWordsResponseDto = PageResponse<CourseContentItem>;

export interface CourseWord {
  id: string;
  flashcardId: string;
  word: string;
  translations: string[];
  sentences: SentenceDto[];
  sentencesAI: SentenceDto[];
  phase: "NEW" | "LEARNING" | "REVIEW" | "RELEARNING";
  isLearned: boolean;
  isSkipped: boolean;
  repetitionCount: number;
  nextReviewAt: string | null;
  algorithmState: string;
  sessionNumber: number;
}
