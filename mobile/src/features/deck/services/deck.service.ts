import apiClient from '@/lib/api/axios';
import { ENDPOINTS } from '@/constants';
import type { PageResponse } from '@/types/common';
import type {
  DeckDto,
  DeckListItem,
  DeckFilters,
  DeckEnrollmentDetails,
  CreateDeckRequest,
  UpdateDeckRequest,
  DeckVisibility,
  DeckOwnerType,
  LearnAlgorithm,
} from '../types';

export const deckService = {
  /**
   * Pobiera listę talii użytkownika z zapisami
   */
  getMyEnrolledDecks: async (): Promise<PageResponse<DeckListItem>> => {
    const { data } = await apiClient.get<PageResponse<DeckListItem>>(ENDPOINTS.ENROLLMENT.MY);
    return data;
  },

  /**
   * Pobiera publiczne talie z paginacją i filtrami
   */
  getPublicDecks: async (
    page: number = 0,
    size: number = 10,
    filters?: DeckFilters
  ): Promise<PageResponse<DeckListItem>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());

    if (filters?.category) params.append('category', filters.category);
    if (filters?.sourceLanguage) params.append('sourceLanguage', filters.sourceLanguage);
    if (filters?.targetLanguage) params.append('targetLanguage', filters.targetLanguage);
    if (filters?.sortBy) params.append('sortBy', filters.sortBy);
    if (filters?.searchTerm) params.append('search', filters.searchTerm);

    const { data } = await apiClient.get<PageResponse<DeckListItem>>(
      `${ENDPOINTS.DECKS.PUBLIC}?${params.toString()}`
    );
    return data;
  },

  /**
   * Pobiera talie użytkownika z paginacją i filtrami
   */
  getUserDecks: async (
    page: number = 0,
    size: number = 10,
    filters?: DeckFilters
  ): Promise<PageResponse<DeckListItem>> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());

    if (filters?.category) params.append('category', filters.category);
    if (filters?.sourceLanguage) params.append('sourceLanguage', filters.sourceLanguage);
    if (filters?.targetLanguage) params.append('targetLanguage', filters.targetLanguage);
    if (filters?.sortBy) params.append('sortBy', filters.sortBy);
    if (filters?.searchTerm) params.append('search', filters.searchTerm);

    const { data } = await apiClient.get<PageResponse<DeckListItem>>(
      `${ENDPOINTS.DECKS.USER}?${params.toString()}`
    );
    return data;
  },

  /**
   * Pobiera talie użytkownika filtrowane po kategorii właściciela
   */
  getUserDecksFiltered: async (ownerType: DeckOwnerType): Promise<DeckListItem[]> => {
    const { data } = await apiClient.get<DeckListItem[]>(
      `${ENDPOINTS.DECKS.USER_FILTER}?ownerType=${ownerType}`
    );
    return data;
  },

  /**
   * Pobiera liczbę talii użytkownika
   */
  getUserDecksCount: async (): Promise<number> => {
    const { data } = await apiClient.get<number>(ENDPOINTS.DECKS.USER_COUNT);
    return data;
  },

  /**
   * Pobiera szczegóły talii z danymi zapisu
   */
  getDeckWithEnrollment: async (deckId: string): Promise<DeckEnrollmentDetails> => {
    const { data } = await apiClient.get<DeckEnrollmentDetails>(ENDPOINTS.DECKS.DETAILS(deckId));
    return data;
  },

  /**
   * Pobiera talię po ID
   */
  getDeckById: async (deckId: string): Promise<DeckDto> => {
    const { data } = await apiClient.get<DeckDto>(ENDPOINTS.DECKS.BY_ID(deckId));
    return data;
  },

  /**
   * Tworzy nową talię
   */
  createDeck: async (request: CreateDeckRequest): Promise<DeckDto> => {
    const { data } = await apiClient.post<DeckDto>(ENDPOINTS.DECKS.LIST, request);
    return data;
  },

  /**
   * Aktualizuje talię
   */
  updateDeck: async (deckId: string, request: UpdateDeckRequest): Promise<DeckDto> => {
    const { data } = await apiClient.put<DeckDto>(ENDPOINTS.DECKS.BY_ID(deckId), request);
    return data;
  },

  /**
   * Usuwa talię
   */
  deleteDeck: async (deckId: string): Promise<void> => {
    await apiClient.delete(ENDPOINTS.DECKS.BY_ID(deckId));
  },

  /**
   * Aktualizuje widoczność talii
   */
  updateVisibility: async (deckId: string, visibility: DeckVisibility): Promise<DeckDto> => {
    const { data } = await apiClient.patch<DeckDto>(ENDPOINTS.DECKS.VISIBILITY(deckId), {
      visibility,
    });
    return data;
  },

  /**
   * Aktualizuje właściciela talii
   */
  updateOwner: async (deckId: string, ownerType: DeckOwnerType): Promise<DeckDto> => {
    const { data } = await apiClient.patch<DeckDto>(ENDPOINTS.DECKS.OWNER(deckId), {
      ownerType,
    });
    return data;
  },

  /**
   * Aktualizuje nazwę talii
   */
  updateName: async (deckId: string, name: string): Promise<DeckDto> => {
    const { data } = await apiClient.patch<DeckDto>(ENDPOINTS.DECKS.NAME(deckId), { name });
    return data;
  },

  /**
   * Aktualizuje algorytm nauki talii
   */
  updateAlgorithm: async (deckId: string, algorithm: LearnAlgorithm): Promise<DeckDto> => {
    const { data } = await apiClient.patch<DeckDto>(ENDPOINTS.DECKS.ALGORITHM(deckId), {
      algorithm,
    });
    return data;
  },

  /**
   * Aktualizuje liczbę fiszek na sesję
   */
  updateFlashcardsPerSession: async (
    deckId: string,
    flashcardsPerSession: number
  ): Promise<DeckDto> => {
    const { data } = await apiClient.patch<DeckDto>(
      ENDPOINTS.DECKS.FLASHCARDS_PER_SESSION(deckId),
      { flashcardsPerSession }
    );
    return data;
  },

  /**
   * Sprawdza czy nazwa talii jest dostępna
   */
  validateDeckName: async (name: string): Promise<{ available: boolean }> => {
    const { data } = await apiClient.get<{ available: boolean }>(
      `${ENDPOINTS.DECKS.VALIDATE_NAME}?name=${encodeURIComponent(name)}`
    );
    return data;
  },

  /**
   * Pobiera statystyki talii
   */
  getDeckStatistics: async (deckId: string) => {
    const { data } = await apiClient.get(ENDPOINTS.DECKS.STATISTICS(deckId));
    return data;
  },
};
