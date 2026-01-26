import apiClient from "@/lib/api/axios";
import type { PageResponse } from "@/types/common";
import type {
  ShareDeckRequest,
  BatchShareDeckRequest,
  DeckShareResponse,
  BatchShareResponse,
  SharedDeckDto,
} from "../types/deckShare.types";

const BASE_URL = "/v1/decks-share";

export const deckShareService = {

  /**
   * Udostępnia talię (generyczne)
   */
  async shareDeck(
    deckId: string,
    request: ShareDeckRequest
  ): Promise<DeckShareResponse> {
    const response = await apiClient.post<DeckShareResponse>(
      `${BASE_URL}/${deckId}/share`,
      request
    );
    console.log(
      "[DeckShare Service] Udostępniono talię:",
      deckId,
      "do:",
      request.targetType
    );
    return response.data;
  },

  /**
   * Udostępnia talię do wielu celów (batch)
   */
  async shareDeckBatch(
    deckId: string,
    request: BatchShareDeckRequest
  ): Promise<BatchShareResponse> {
    const response = await apiClient.post<BatchShareResponse>(
      `${BASE_URL}/${deckId}/share/batch`,
      request
    );
    console.log(
      "[DeckShare Service] Batch udostępnienie talii:",
      deckId,
      "celów:",
      request.targetIds.length
    );
    return response.data;
  },

  /**
   * Udostępnia talię wszystkim uczniom
   */
  async shareDeckWithAllStudents(
    deckId: string,
    message?: string
  ): Promise<DeckShareResponse> {
    const response = await apiClient.post<DeckShareResponse>(
      `${BASE_URL}/${deckId}/share/students`,
      message ? { message } : undefined
    );
    console.log(
      "[DeckShare Service] Udostępniono talię wszystkim uczniom:",
      deckId
    );
    return response.data;
  },

  /**
   * Udostępnia talię wszystkim znajomym
   */
  async shareDeckWithAllFriends(
    deckId: string,
    message?: string
  ): Promise<DeckShareResponse> {
    const response = await apiClient.post<DeckShareResponse>(
      `${BASE_URL}/${deckId}/share/friends`,
      message ? { message } : undefined
    );
    console.log(
      "[DeckShare Service] Udostępniono talię wszystkim znajomym:",
      deckId
    );
    return response.data;
  },

  /**
   * Udostępnia talię grupie
   */
  async shareDeckWithGroup(
    deckId: string,
    groupId: string,
    message?: string
  ): Promise<DeckShareResponse> {
    const response = await apiClient.post<DeckShareResponse>(
      `${BASE_URL}/${deckId}/share/group/${groupId}`,
      message ? { message } : undefined
    );
    console.log("[DeckShare Service] Udostępniono talię grupie:", groupId);
    return response.data;
  },

  /**
   * Udostępnia talię konkretnemu użytkownikowi
   */
  async shareDeckWithUser(
    deckId: string,
    targetUserId: string,
    message?: string
  ): Promise<DeckShareResponse> {
    const response = await apiClient.post<DeckShareResponse>(
      `${BASE_URL}/${deckId}/share/user/${targetUserId}`,
      message ? { message } : undefined
    );
    console.log(
      "[DeckShare Service] Udostępniono talię użytkownikowi:",
      targetUserId
    );
    return response.data;
  },

  /**
   * Wycofuje udostępnienie
   */
  async revokeDeckShare(shareId: string): Promise<void> {
    await apiClient.delete(`${BASE_URL}/shares/${shareId}`);
    console.log("[DeckShare Service] Wycofano udostępnienie:", shareId);
  },

  /**
   * Wycofuje wszystkie udostępnienia talii
   */
  async revokeAllDeckShares(deckId: string): Promise<void> {
    await apiClient.delete(`${BASE_URL}/${deckId}/shares`);
    console.log(
      "[DeckShare Service] Wycofano wszystkie udostępnienia talii:",
      deckId
    );
  },

  /**
   * Pobiera udostępnienia talii
   */
  async getDeckShares(deckId: string): Promise<DeckShareResponse[]> {
    const response = await apiClient.get<DeckShareResponse[]>(
      `${BASE_URL}/${deckId}/shares`
    );
    console.log(
      "[DeckShare Service] Pobrano udostępnienia talii:",
      response.data.length
    );
    return response.data;
  },

  /**
   * Pobiera moje udostępnienia (talie udostępnione przeze mnie)
   */
  async getMyShares(
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<DeckShareResponse>> {
    const response = await apiClient.get<PageResponse<DeckShareResponse>>(
      `${BASE_URL}/my-shares`,
      { params: { page, size } }
    );
    console.log(
      "[DeckShare Service] Pobrano moje udostępnienia:",
      response.data.content.length
    );
    return response.data;
  },

  /**
   * Pobiera talie udostępnione mi
   */
  async getSharedWithMe(
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<SharedDeckDto>> {
    const response = await apiClient.get<PageResponse<SharedDeckDto>>(
      `${BASE_URL}/shared-with-me`,
      { params: { page, size } }
    );
    console.log(
      "[DeckShare Service] Pobrano talie udostępnione mi:",
      response.data.content.length
    );
    return response.data;
  },

  /**
   * Sprawdza czy użytkownik ma dostęp do talii przez udostępnienie
   */
  async hasAccessToDeck(deckId: string): Promise<boolean> {
    const response = await apiClient.get<boolean>(
      `${BASE_URL}/${deckId}/has-share-access`
    );
    return response.data;
  },
};

// Export domyślny dla wygody
export default deckShareService;
