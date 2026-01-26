import apiClient from "@/lib/api/axios";
import type {
  DeckShareResponse,
  ShareDeckRequestBody,
  BatchShareDeckRequestBody,
  BatchShareResponse,
  PageResponse,
} from "../types/api";


const BASE_URL = "/v1/decks-share";

export const deckShareApi = {

  /**
   * Udostępnia talię do wybranego celu
   */
  shareDeck: async (
    deckId: string,
    request: ShareDeckRequestBody
  ): Promise<DeckShareResponse> => {
    const response = await apiClient.post<DeckShareResponse>(
      `${BASE_URL}/${deckId}/share`,
      request
    );
    return response.data;
  },

  /**
   * Udostępnia talię do wielu celów (batch)
   */
  shareDeckBatch: async (
    deckId: string,
    request: BatchShareDeckRequestBody
  ): Promise<BatchShareResponse> => {
    const response = await apiClient.post<BatchShareResponse>(
      `${BASE_URL}/${deckId}/share/batch`,
      request
    );
    return response.data;
  },

  /**
   * Udostępnia talię wszystkim uczniom nauczyciela
   */
  shareDeckWithAllStudents: async (
    deckId: string,
    message?: string
  ): Promise<DeckShareResponse> => {
    const response = await apiClient.post<DeckShareResponse>(
      `${BASE_URL}/${deckId}/share/students`,
      { message }
    );
    return response.data;
  },

  /**
   * Udostępnia talię wszystkim znajomym
   */
  shareDeckWithAllFriends: async (
    deckId: string,
    message?: string
  ): Promise<DeckShareResponse> => {
    const response = await apiClient.post<DeckShareResponse>(
      `${BASE_URL}/${deckId}/share/friends`,
      { message }
    );
    return response.data;
  },

  /**
   * Udostępnia talię grupie
   */
  shareDeckWithGroup: async (
    deckId: string,
    groupId: string,
    message?: string
  ): Promise<DeckShareResponse> => {
    const response = await apiClient.post<DeckShareResponse>(
      `${BASE_URL}/${deckId}/share/group/${groupId}`,
      { message }
    );
    return response.data;
  },

  /**
   * Udostępnia talię konkretnemu użytkownikowi
   */
  shareDeckWithUser: async (
    deckId: string,
    targetUserId: string,
    message?: string
  ): Promise<DeckShareResponse> => {
    const response = await apiClient.post<DeckShareResponse>(
      `${BASE_URL}/${deckId}/share/user/${targetUserId}`,
      { message }
    );
    return response.data;
  },

  /**
   * Cofa pojedyncze udostępnienie
   */
  revokeShare: async (shareId: string): Promise<void> => {
    await apiClient.delete(`${BASE_URL}/shares/${shareId}`);
  },

  /**
   * Cofa wszystkie udostępnienia talii
   */
  revokeAllShares: async (deckId: string): Promise<void> => {
    await apiClient.delete(`${BASE_URL}/${deckId}/shares`);
  },

  /**
   * Pobiera udostępnienia talii
   */
  getDeckShares: async (deckId: string): Promise<DeckShareResponse[]> => {
    const response = await apiClient.get<DeckShareResponse[]>(
      `${BASE_URL}/${deckId}/shares`
    );
    return response.data;
  },

  /**
   * Pobiera moje udostępnienia (wszystkie talie)
   */
  getMyShares: async (
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<DeckShareResponse>> => {
    const response = await apiClient.get<PageResponse<DeckShareResponse>>(
      `${BASE_URL}/my-shares`,
      { params: { page, size } }
    );
    return response.data;
  },

  /**
   * Pobiera talie udostępnione mi
   */
  getSharedWithMe: async (
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<DeckShareResponse>> => {
    const response = await apiClient.get<PageResponse<DeckShareResponse>>(
      `${BASE_URL}/shared-with-me`,
      { params: { page, size } }
    );
    return response.data;
  },
};
