import { WordSentence } from "./word.types";

/**
 * Słówko ze społeczności
 */
export interface CommunityWord {
  /** ID słówka w bazie społeczności */
  id: string;
  /** Słówko w języku źródłowym */
  word: string;
  /** Lista tłumaczeń */
  translations: string[];
  /** Lista zdań przykładowych */
  sentences: WordSentence[];
  /** Liczba użytkowników, którzy dodali to słówko */
  usageCount: number;
  /** Autor słówka */
  author: string;
  /** Data dodania */
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
  /** Wyszukiwanie po słówku */
  word?: string;
  /** Wyszukiwanie po tłumaczeniu */
  translation?: string;
  /** Strona */
  page?: number;
  /** Rozmiar strony */
  pageSize?: number;
}
