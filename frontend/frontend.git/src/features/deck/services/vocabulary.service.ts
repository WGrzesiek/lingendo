/**
 * Serwis do zarządzania słówkami (vocabulary)
 * Operacje dodawania słówek do decków i społeczności
 */

import apiClient from "@/lib/api/axios";

const BASE_URL = "/v1/vocabulary";

/**
 * Interfejs dla zdania przykładowego
 */
export interface VocabularySentence {
  sentence: string;
  translation: string;
}

/**
 * Interfejs dla słówka do dodania
 */
export interface VocabularyWord {
  word: string;
  translations: string[];
  sentences?: VocabularySentence[];
}

/**
 * Response po utworzeniu słówek
 */
export interface CreateBatchResponse {
  created: number;
  words: Array<{
    id: string;
    word: string;
  }>;
}

/**
 * Dodaje batch słówek do konkretnego decka
 * Endpoint: POST /api/v1/vocabulary/deck/{deckId}/create-batch
 */
export const createBatchWordsForDeck = async (
  deckId: string,
  words: VocabularyWord[]
): Promise<CreateBatchResponse> => {
  const response = await apiClient.post<CreateBatchResponse>(
    `${BASE_URL}/deck/${deckId}/create-batch`,
    words
  );

  console.log(
    `[Vocabulary Service] Dodano ${response.data.created} słówek do decka ${deckId}`
  );

  return response.data;
};

/**
 * Dodaje batch słówek do społeczności (bez powiązania z deckiem)
 * Endpoint: POST /api/v1/vocabulary/create-batch
 */
export const createBatchWordsForCommunity = async (
  words: VocabularyWord[]
): Promise<CreateBatchResponse> => {
  const response = await apiClient.post<CreateBatchResponse>(
    `${BASE_URL}/create-batch`,
    words
  );

  console.log(
    `[Vocabulary Service] Dodano ${response.data.created} słówek do społeczności`
  );

  return response.data;
};
