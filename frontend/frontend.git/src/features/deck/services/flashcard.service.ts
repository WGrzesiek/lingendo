import apiClient from "@/lib/api/axios";
import type { PageResponse } from "@/types/common";

/**
 * Interfejs dla pojedynczej fiszki/słówka (zmapowany z API)
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
  createdAt?: string;
}

/**
 * Odpowioedz z api
 */
interface ApiFlashcardItem {
  id: string;
  wordDto: {
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
  };
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

  const response = await apiClient.get<PageResponse<ApiFlashcardItem>>(
    `/v1/decks/${deckId}/flashcards/page`,
    {
      params: { page, size },
    }
  );

  console.log(
    "[Flashcard Service] Pobrano stronę fiszek:",
    response.data.content.length
  );

  const mappedContent: DeckFlashcard[] = (response.data.content ?? []).map(
    (item) => ({
      id: item.wordDto.id,
      word: item.wordDto.word,
      translations: item.wordDto.translations ?? [],
      sentences: item.wordDto.sentences ?? [],
      sentencesAI: item.wordDto.sentencesAI ?? [],
    })
  );

  return {
    ...response.data,
    content: mappedContent,
  };
};
