import apiClient from '@/lib/api/axios';
import { ENDPOINTS } from '@/constants';
import type { PageResponse } from '@/types/common';
import type { DeckFlashcard, WordToAdd, CreateBatchResponse } from '../types';

export const flashcardService = {
  /**
   * Pobiera fiszki talii z paginacją
   */
  getDeckFlashcardsPage: async (
    deckId: string,
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<DeckFlashcard>> => {
    const { data } = await apiClient.get<PageResponse<DeckFlashcard>>(
      `${ENDPOINTS.DECKS.FLASHCARDS_PAGE(deckId)}?page=${page}&size=${size}`
    );
    return data;
  },

  /**
   * Tworzy batch słów dla istniejącej talii
   */
  createBatchWordsForDeck: async (
    deckId: string,
    words: WordToAdd[]
  ): Promise<CreateBatchResponse> => {
    const { data } = await apiClient.post<CreateBatchResponse>(
      ENDPOINTS.VOCABULARY.CREATE_BATCH_FOR_DECK(deckId),
      { words }
    );
    return data;
  },

  /**
   * Tworzy batch słów (nowa talia lub istniejąca)
   */
  createBatchWords: async (words: WordToAdd[], deckId?: string): Promise<CreateBatchResponse> => {
    const { data } = await apiClient.post<CreateBatchResponse>(ENDPOINTS.VOCABULARY.CREATE_BATCH, {
      words,
      deckId,
    });
    return data;
  },

  /**
   * Usuwa fiszkę
   */
  deleteFlashcard: async (flashcardId: string): Promise<void> => {
    await apiClient.delete(`/vocabulary/${flashcardId}`);
  },

  /**
   * Aktualizuje fiszkę
   */
  updateFlashcard: async (
    flashcardId: string,
    updates: Partial<Pick<DeckFlashcard, 'word' | 'translations' | 'sentences'>>
  ): Promise<DeckFlashcard> => {
    const { data } = await apiClient.patch<DeckFlashcard>(`/vocabulary/${flashcardId}`, updates);
    return data;
  },
};
