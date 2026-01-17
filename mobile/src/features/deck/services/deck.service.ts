import apiClient from '@/lib/api/axios';
import type { PageResponse } from '@/types/common';
import type {
  DeckDto,
  DeckDetailsDto,
  CreateDeckRequest,
  CreateDeckResponse,
  DeckListItem,
  CreatedDeckListItem,
  DeckStatisticsDto,
  UserDeckCountDto,
  DeckDetailResponse,
  DecksStats,
  DeckOwnerType,
  DeckVisibility,
  UpdateDeckVisibilityRequest,
  UpdateDeckOwnerRequest,
  UpdateDeckNameRequest,
  UpdateLearnAlgorithmRequest,
  UpdateFlashcardsPerSessionRequest,
} from '../types';

const BASE_URL = '/v1/decks';
const ENROLLMENTS_URL = '/v1/decks/enrollments';
const STATS_URL = '/v1/courses';

export const deckService = {
  /**
   * Pobiera talie kursów studenta z paginacją
   */
  getMyEnrolledDecks: async (params?: {
    page?: number;
    size?: number;
  }): Promise<PageResponse<DeckListItem>> => {
    const response = await apiClient.get<PageResponse<DeckListItem>>(`${ENROLLMENTS_URL}/my`, {
      params,
    });
    console.log('[Deck Service] Pobrano talie kursów studenta:', response.data.content.length);
    return response.data;
  },

  /**
   * Pobiera listę talii utworzonych przez użytkownika z opcjonalnymi filtrami
   */
  getDecksCreatedByMe: async (params?: {
    deckVisibility?: DeckVisibility[];
    owner?: DeckOwnerType[];
    page?: number;
    size?: number;
  }): Promise<PageResponse<CreatedDeckListItem>> => {
    const response = await apiClient.get<PageResponse<CreatedDeckListItem>>(BASE_URL, { params });
    console.log(
      '[Deck Service] Pobrano talie utworzone przez użytkownika:',
      response.data.content.length
    );
    return response.data;
  },

  /**
   * Pobiera wszystkie publiczne talie od wszystkich użytkowników
   */
  getAllPublicDecks: async (params?: {
    owner?: DeckOwnerType;
    page?: number;
    size?: number;
  }): Promise<PageResponse<CreatedDeckListItem>> => {
    const response = await apiClient.get<PageResponse<CreatedDeckListItem>>(`${BASE_URL}/public`, {
      params,
    });
    console.log('[Deck Service] Pobrano publiczne talie:', response.data.content.length);
    return response.data;
  },

  /**
   * Pobiera talię po ID (podstawowe informacje)
   */
  getDeckById: async (deckId: string): Promise<DeckDto> => {
    const response = await apiClient.get<DeckDto>(`${BASE_URL}/${deckId}`);
    console.log('[Deck Service] Pobrano talię:', response.data.name);
    return response.data;
  },

  /**
   * Pobiera szczegółowe informacje o talii
   */
  getDeckDetails: async (deckId: string): Promise<DeckDetailsDto> => {
    const response = await apiClient.get<DeckDetailsDto>(`${BASE_URL}/${deckId}/details`);
    console.log('[Deck Service] Pobrano szczegóły talii:', response.data.name);
    return response.data;
  },

  /**
   * Pobiera szczegółowe informacje o talii (deck detail)
   */
  getDeckDetail: async (deckId: string): Promise<DeckDetailResponse> => {
    const response = await apiClient.get<DeckDetailResponse>(`${BASE_URL}/${deckId}`);
    console.log('[Deck Service] Pobrano deck detail:', response.data.name);
    return response.data;
  },

  /**
   * Pobiera wszystkie talie użytkownika (bez filtrów)
   */
  getUserDecks: async (): Promise<DeckDto[]> => {
    const response = await apiClient.get<DeckDto[]>(`${BASE_URL}/user`);
    console.log('[Deck Service] Pobrano talie użytkownika:', response.data.length);
    return response.data;
  },

  /**
   * Pobiera talie użytkownika z filtrami
   */
  getUserDecksFiltered: async (params?: {
    isPublic?: boolean;
    owner?: DeckOwnerType;
  }): Promise<DeckDto[]> => {
    const response = await apiClient.get<DeckDto[]>(`${BASE_URL}/user/filter`, { params });
    console.log('[Deck Service] Pobrano talie użytkownika (filtered):', response.data.length);
    return response.data;
  },

  /**
   * Pobiera liczbę talii użytkownika
   */
  getUserDeckCount: async (): Promise<UserDeckCountDto> => {
    const response = await apiClient.get<UserDeckCountDto>(`${BASE_URL}/user/count`);
    console.log('[Deck Service] Liczba talii użytkownika:', response.data.totalDecks);
    return response.data;
  },

  /**
   * Pobiera wszystkie publiczne talie (bez autoryzacji)
   */
  getPublicDecks: async (): Promise<DeckDto[]> => {
    const response = await apiClient.get<DeckDto[]>(`${BASE_URL}/public`);
    console.log('[Deck Service] Pobrano publiczne talie:', response.data.length);
    return response.data;
  },

  /**
   * Pobiera statystyki talii
   */
  getDeckStatistics: async (deckId: string): Promise<DeckStatisticsDto> => {
    const response = await apiClient.get<DeckStatisticsDto>(`${BASE_URL}/${deckId}/statistics`);
    console.log('[Deck Service] Pobrano statystyki talii:', response.data.deckName);
    return response.data;
  },

  /**
   * Pobiera statystyki moich kursów
   */
  getMyDeckStats: async (deckIds: string[]): Promise<DecksStats> => {
    const response = await apiClient.post<DecksStats>(`${STATS_URL}/my-course/stats`, { deckIds });
    console.log('[Deck Service] Pobrano statystyki moich kursów:', response.data);
    return response.data;
  },

  /**
   * Tworzy nową talię fiszek
   */
  createDeck: async (data: CreateDeckRequest): Promise<CreateDeckResponse> => {
    const response = await apiClient.post<CreateDeckResponse>(BASE_URL, data);
    console.log('[Deck Service] Talia utworzona:', response.data.deckName);
    return response.data;
  },

  /**
   * Usuwa talię (operacja nieodwracalna!)
   */
  deleteDeck: async (deckId: string): Promise<CreateDeckResponse> => {
    const response = await apiClient.delete<CreateDeckResponse>(`${BASE_URL}/${deckId}`);
    console.log('[Deck Service] Talia usunięta:', response.data.deckName);
    return response.data;
  },

  /**
   * Aktualizuje szczegóły talii
   */
  updateDeckDetails: async (deckId: string, data: DeckDetailsDto): Promise<DeckDetailsDto> => {
    const response = await apiClient.put<DeckDetailsDto>(`${BASE_URL}/${deckId}/details`, data);
    console.log('[Deck Service] Zaktualizowano talię:', response.data.name);
    return response.data;
  },

  /**
   * Zmienia widoczność talii
   */
  updateDeckVisibility: async (
    deckId: string,
    data: UpdateDeckVisibilityRequest
  ): Promise<UpdateDeckVisibilityRequest> => {
    const response = await apiClient.put<UpdateDeckVisibilityRequest>(
      `${BASE_URL}/${deckId}/visibility`,
      data
    );
    console.log('[Deck Service] Zmieniono widoczność:', data.isPublic ? 'publiczna' : 'prywatna');
    return response.data;
  },

  /**
   * Zmienia właściciela talii
   */
  updateDeckOwner: async (
    deckId: string,
    data: UpdateDeckOwnerRequest
  ): Promise<UpdateDeckOwnerRequest> => {
    const response = await apiClient.put<UpdateDeckOwnerRequest>(
      `${BASE_URL}/${deckId}/owner`,
      data
    );
    console.log('[Deck Service] Zmieniono właściciela:', data.newOwner);
    return response.data;
  },

  /**
   * Zmienia nazwę talii
   */
  updateDeckName: async (
    deckId: string,
    data: UpdateDeckNameRequest
  ): Promise<UpdateDeckNameRequest> => {
    const response = await apiClient.put<UpdateDeckNameRequest>(`${BASE_URL}/${deckId}/name`, data);
    console.log('[Deck Service] Zmieniono nazwę:', data.deckName);
    return response.data;
  },

  /**
   * Zmienia algorytm nauki talii
   */
  updateLearnAlgorithm: async (
    deckId: string,
    data: UpdateLearnAlgorithmRequest
  ): Promise<UpdateLearnAlgorithmRequest> => {
    const response = await apiClient.put<UpdateLearnAlgorithmRequest>(
      `${BASE_URL}/${deckId}/learnAlgorithm`,
      data
    );
    console.log('[Deck Service] Zmieniono algorytm:', data.learnAlgorithm);
    return response.data;
  },

  /**
   * Zmienia liczbę fiszek na sesję
   */
  updateFlashcardsPerSession: async (
    deckId: string,
    data: UpdateFlashcardsPerSessionRequest
  ): Promise<UpdateFlashcardsPerSessionRequest> => {
    const response = await apiClient.put<UpdateFlashcardsPerSessionRequest>(
      `${BASE_URL}/${deckId}/flashcardsPerSession`,
      data
    );
    console.log('[Deck Service] Zmieniono limit fiszek:', data.flashcardsPerSession);
    return response.data;
  },

  /**
   * Waliduje dostępność nazwy talii dla użytkownika
   * @returns true jeśli nazwa dostępna, false jeśli zajęta
   */
  validateDeckName: async (deckName: string): Promise<boolean> => {
    const response = await apiClient.get<boolean>(`${BASE_URL}/validate-name`, {
      params: { deckName },
    });
    console.log('[Deck Service] Walidacja nazwy:', deckName, response.data ? 'dostępna' : 'zajęta');
    return response.data;
  },
};
