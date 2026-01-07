import { WordSentence } from "./word.types";

/**
 * Słówko ze społeczności
 */
export interface CommunityWord {
  id: string;
  word: string;
  translations: string[];
  sentences: WordSentence[];
  usageCount: number;
  author: string;
  createdAt: string;
}

/**
 * Odpowiedź API z listą słówek społeczności
 */
export interface CommunityWordsResponse {
  content: CommunityWord[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

/**
 * Filtry wyszukiwania słówek
 */
export interface WordSearchFilters {
  word?: string;
  translation?: string;
  page?: number;
  pageSize?: number;
}
