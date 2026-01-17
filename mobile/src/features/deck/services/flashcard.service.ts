import apiClient from '@/lib/api/axios';
import type { PageResponse } from '@/types/common';
import type { DeckFlashcard, WordToAdd, CreateBatchResponse } from '../types';

const BASE_URL = '/v1/decks';
const VOCABULARY_URL = '/v1/vocabulary';

/**
 * Odpowiedź z API - pojedynczy element fiszki
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

export const flashcardService = {
  /**
   * Pobiera stronę fiszek dla danej talii (z paginacją)
   */
  getDeckFlashcardsPage: async (params: {
    deckId: string;
    page?: number;
    size?: number;
  }): Promise<PageResponse<DeckFlashcard>> => {
    const { deckId, page = 0, size = 10 } = params;

    const response = await apiClient.get<PageResponse<ApiFlashcardItem>>(
      `${BASE_URL}/${deckId}/flashcards/page`,
      { params: { page, size } }
    );

    console.log('[Flashcard Service] Pobrano stronę fiszek:', response.data.content.length);

    const mappedContent: DeckFlashcard[] = (response.data.content ?? []).map((item) => ({
      id: item.wordDto.id,
      word: item.wordDto.word,
      translations: item.wordDto.translations ?? [],
      sentences: item.wordDto.sentences ?? [],
      sentencesAI: item.wordDto.sentencesAI ?? [],
    }));

    return {
      ...response.data,
      content: mappedContent,
    };
  },

  /**
   * Dodaje batch słówek do konkretnego decka
   */
  createBatchWordsForDeck: async (
    deckId: string,
    words: WordToAdd[]
  ): Promise<CreateBatchResponse> => {
    const response = await apiClient.post<CreateBatchResponse>(
      `${VOCABULARY_URL}/deck/${deckId}/create-batch`,
      words
    );
    console.log(`[Flashcard Service] Dodano ${response.data.created} słówek do decka ${deckId}`);
    return response.data;
  },

  /**
   * Dodaje batch słówek do społeczności (bez powiązania z deckiem)
   */
  createBatchWordsForCommunity: async (words: WordToAdd[]): Promise<CreateBatchResponse> => {
    const response = await apiClient.post<CreateBatchResponse>(
      `${VOCABULARY_URL}/create-batch`,
      words
    );
    console.log(`[Flashcard Service] Dodano ${response.data.created} słówek do społeczności`);
    return response.data;
  },
};
