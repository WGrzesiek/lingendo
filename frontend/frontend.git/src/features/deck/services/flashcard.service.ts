/**
 * Serwis do zarządzania fiszkami (flashcards)
 */

import apiClient from "@/lib/api/axios";
import type { PageResponse } from "@/types/common";

/**
 * Interfejs dla pojedynczej fiszki/słówka
 */
export interface DeckFlashcard {
  id: string;
  word: string;
  translations: string[];
  sentences: Array<{
    id: string;
    sentence: string;
    translation: string;
  }>;
  sentencesAI: Array<{
    id: string;
    sentence: string;
    translation: string;
  }>;
  createdAt: string;
}

/**
 * Pobiera stronę fiszek dla danej talii (deck)
 * Używane z infinite scroll
 */
export const getDeckFlashcardsPage = async (params: {
  deckId: string;
  page?: number;
  size?: number;
}): Promise<PageResponse<DeckFlashcard>> => {
  const { deckId, page = 0, size = 10 } = params;

  const response = await apiClient.get<PageResponse<DeckFlashcard>>(
    `/v1/decks/${deckId}/flashcards/page`,
    {
      params: { page, size },
    }
  );

  console.log(
    "[Flashcard Service] Pobrano stronę fiszek:",
    response.data.content.length
  );

  return response.data;
};
