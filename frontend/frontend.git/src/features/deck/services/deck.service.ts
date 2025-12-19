/**
 * Serwis do zarządzania taliami fiszek
 * Wszystkie operacje CRUD oraz filtrowanie, walidacja, statystyki
 */

import apiClient from "@/lib/api/axios";
import type {
  CreateDeckDto,
  ResponseDeckDto,
  DeckDto,
  DeckDetailsDto,
  UpdateDeckVisibilityRequest,
  UpdateDeckOwnerRequest,
  UpdateDeckNameRequest,
  UpdateLearnAlgorithmRequest,
  UpdateFlashcardsPerSessionRequest,
  DeckStatisticsDto,
  UserDeckCountDto,
  IDeckListItem,
  DeckOwnerType,
} from "../types";
import type { PageResponse } from "@/types/common";
import {
  DecksStats,
  DeckVisibility,
  ICreatedDeckListItem,
} from "@/features/deck/types/created-deck.types";

const BASE_URL = "/v1/decks";
const BASE_URL2 = "/v1/decks/enrollments";

/**
 * Pobiera talie kursów studenta z paginacją
 */
export const getIDecks = async (params?: {
  page?: number;
  size?: number;
}): Promise<PageResponse<IDeckListItem>> => {
  const response = await apiClient.get<PageResponse<IDeckListItem>>(
    `${BASE_URL2}/my`,
    { params }
  );

  console.log(
    "[Deck Service] Pobrano talie kursów studenta:",
    response.data.content.length
  );

  return response.data;
};

/**
 * Pobiera listę talii z opcjonalnymi filtrami (visibility, owner)
 * Bez filtrów zwraca wszystkie talie
 */
export const getDecksCreatedByMe = async (params?: {
  deckVisibility?: DeckVisibility[];
  owner?: DeckOwnerType;
  page?: number;
  size?: number;
}): Promise<PageResponse<ICreatedDeckListItem>> => {
  const response = await apiClient.get<PageResponse<ICreatedDeckListItem>>(
    BASE_URL,
    { params }
  );

  console.log(
    "[Deck Service] Pobrano talie z filtrami:",
    response.data.content.length
  );

  return response.data;
};

/**
 * Tworzy nową talię fiszek
 */
export const createDeck = async (
  data: CreateDeckDto
): Promise<ResponseDeckDto> => {
  const response = await apiClient.post<ResponseDeckDto>(BASE_URL, data);
  console.log("[Deck Service] Talia utworzona:", response.data.deckName);
  return response.data;
};

/**
 * Pobiera talię po ID (podstawowe informacje)
 */
export const getDeckById = async (deckId: string): Promise<DeckDto> => {
  const response = await apiClient.get<DeckDto>(`${BASE_URL}/${deckId}`);
  console.log("[Deck Service] Pobrano talię:", response.data.name);
  return response.data;
};

/**
 * Usuwa talię (operacja nieodwracalna!)
 */
export const deleteDeck = async (deckId: string): Promise<ResponseDeckDto> => {
  const response = await apiClient.delete<ResponseDeckDto>(
    `${BASE_URL}/${deckId}`
  );
  console.log("[Deck Service] Talia usunięta:", response.data.deckName);
  return response.data;
};

/**
 * Pobiera szczegółowe informacje o talii
 */
export const getDeckDetails = async (
  deckId: string
): Promise<DeckDetailsDto> => {
  const response = await apiClient.get<DeckDetailsDto>(
    `${BASE_URL}/${deckId}/details`
  );
  console.log("[Deck Service] Pobrano szczegóły talii:", response.data.name);
  return response.data;
};

/**
 * Aktualizuje szczegóły talii (pola edytowalne)
 * Pola readonly (id, userId, wordCount, timestamps) są ignorowane
 */
export const updateDeckDetails = async (
  deckId: string,
  data: DeckDetailsDto
): Promise<DeckDetailsDto> => {
  const response = await apiClient.put<DeckDetailsDto>(
    `${BASE_URL}/${deckId}/details`,
    data
  );
  console.log("[Deck Service] Zaktualizowano talię:", response.data.name);
  return response.data;
};

/**
 * Zmienia widoczność talii (publiczna/prywatna)
 */
export const updateDeckVisibility = async (
  deckId: string,
  data: UpdateDeckVisibilityRequest
): Promise<UpdateDeckVisibilityRequest> => {
  const response = await apiClient.put<UpdateDeckVisibilityRequest>(
    `${BASE_URL}/${deckId}/visibility`,
    data
  );
  console.log(
    "[Deck Service] Zmieniono widoczność talii:",
    data.isPublic ? "publiczna" : "prywatna"
  );
  return response.data;
};

/**
 * Zmienia właściciela talii (I, TEACHER, FRIEND, COMMUNITY)
 */
export const updateDeckOwner = async (
  deckId: string,
  data: UpdateDeckOwnerRequest
): Promise<UpdateDeckOwnerRequest> => {
  const response = await apiClient.put<UpdateDeckOwnerRequest>(
    `${BASE_URL}/${deckId}/owner`,
    data
  );
  console.log("[Deck Service] Zmieniono właściciela talii:", data.newOwner);
  return response.data;
};

/**
 * Zmienia nazwę talii (1-100 znaków, unikalna dla użytkownika)
 */
export const updateDeckName = async (
  deckId: string,
  data: UpdateDeckNameRequest
): Promise<UpdateDeckNameRequest> => {
  const response = await apiClient.put<UpdateDeckNameRequest>(
    `${BASE_URL}/${deckId}/name`,
    data
  );
  console.log("[Deck Service] Zmieniono nazwę talii:", data.deckName);
  return response.data;
};

/**
 * Zmienia algorytm nauki talii
 */
export const updateLearnAlgorithm = async (
  deckId: string,
  data: UpdateLearnAlgorithmRequest
): Promise<UpdateLearnAlgorithmRequest> => {
  const response = await apiClient.put<UpdateLearnAlgorithmRequest>(
    `${BASE_URL}/${deckId}/learnAlgorithm`,
    data
  );
  console.log("[Deck Service] Zmieniono algorytm nauki:", data.learnAlgorithm);
  return response.data;
};

/**
 * Zmienia liczbę fiszek na sesję (1-100)
 */
export const updateFlashcardsPerSession = async (
  deckId: string,
  data: UpdateFlashcardsPerSessionRequest
): Promise<UpdateFlashcardsPerSessionRequest> => {
  const response = await apiClient.put<UpdateFlashcardsPerSessionRequest>(
    `${BASE_URL}/${deckId}/flashcardsPerSession`,
    data
  );
  console.log(
    "[Deck Service] Zmieniono liczbę fiszek na sesję:",
    data.flashcardsPerSession
  );
  return response.data;
};

/**
 * Pobiera statystyki talii (learned/unlearned flashcards, progress, sessions)
 */
export const getDeckStatistics = async (
  deckId: string
): Promise<DeckStatisticsDto> => {
  const response = await apiClient.get<DeckStatisticsDto>(
    `${BASE_URL}/${deckId}/statistics`
  );
  console.log(
    "[Deck Service] Pobrano statystyki talii:",
    response.data.deckName
  );
  return response.data;
};

/**
 * Waliduje dostępność nazwy talii dla użytkownika
 * @returns true jeśli nazwa dostępna, false jeśli zajęta
 */
export const validateDeckName = async (deckName: string): Promise<boolean> => {
  const response = await apiClient.get<boolean>(`${BASE_URL}/validate-name`, {
    params: { deckName },
  });
  console.log(
    "[Deck Service] Walidacja nazwy:",
    deckName,
    response.data ? "dostępna" : "zajęta"
  );
  return response.data;
};

/**
 * Pobiera wszystkie talie użytkownika (bez filtrów)
 */
export const getUserDecks = async (): Promise<DeckDto[]> => {
  const response = await apiClient.get<DeckDto[]>(`${BASE_URL}/user`);
  console.log(
    "[Deck Service] Pobrano talie użytkownika:",
    response.data.length
  );
  return response.data;
};

/**
 * Pobiera talie użytkownika z filtrami (isPublic, owner)
 */
export const getUserDecksFiltered = async (params?: {
  isPublic?: boolean;
  owner?: DeckOwnerType;
}): Promise<DeckDto[]> => {
  const response = await apiClient.get<DeckDto[]>(`${BASE_URL}/user/filter`, {
    params,
  });
  console.log(
    "[Deck Service] Pobrano talie użytkownika (filtered):",
    response.data.length
  );
  return response.data;
};

/**
 * Pobiera liczbę talii użytkownika (total, public, private)
 */
export const getUserDeckCount = async (): Promise<UserDeckCountDto> => {
  const response = await apiClient.get<UserDeckCountDto>(
    `${BASE_URL}/user/count`
  );
  console.log(
    "[Deck Service] Liczba talii użytkownika:",
    response.data.totalDecks
  );
  return response.data;
};

/**
 * Pobiera wszystkie publiczne talie (bez autoryzacji)
 */
export const getPublicDecks = async (): Promise<DeckDto[]> => {
  const response = await apiClient.get<DeckDto[]>(`${BASE_URL}/public`);
  console.log("[Deck Service] Pobrano publiczne talie:", response.data.length);
  return response.data;
};
export interface getMyDeckStatsBody {
  deckIds: string[];
}

const STASTS_URL = "/v1/courses";

export const getMyDeckStats = async (
  body: getMyDeckStatsBody
): Promise<DecksStats> => {
  const response = await apiClient.post<DecksStats>(
    `${STASTS_URL}/my-course/stats`,
    body
  );
  console.log("[Deck Service] Pobrano statystyki moich kursów:", response.data);
  return response.data;
};

/**
 * Pobiera szczegółowe informacje o talii (deckdetail)
 * Response zawiera pełne informacje o decku z API
 */
export interface DeckDetailResponse {
  id: string;
  name: string;
  deckDescription: string;
  deckDifficulty: string;
  deckOwner: string;
  deckCategory: string;
  ownerId: string;
  wordCount: number;
  visibility: string;
  createdAt: string;
  updatedAt: string;
  username: string;
}

export const getDeckDetail = async (
  deckId: string
): Promise<DeckDetailResponse> => {
  const response = await apiClient.get<DeckDetailResponse>(
    `${BASE_URL}/${deckId}`
  );
  console.log(
    "[Deck Service] Pobrano szczegóły deck detail:",
    response.data.name
  );
  return response.data;
};
