/**
 * Serwis do zarządzania znajomymi - komunikacja z API
 */

import apiClient from "@/lib/api/axios";
import type {
  IFriend,
  FriendRequest,
  UserSearchResult,
  BlockedUser,
  UserStats,
  FriendEnriched,
} from "../types/friend.types";
import type { PageResponse } from "@/types/common";

const BASE_URL = "/v1/friends";

// ============================================
// TYPY ODPOWIEDZI Z API
// ============================================

/**
 * Odpowiedź API dla znajomego
 */
export interface ApiFriendResponse {
  friendshipId: string;
  friendId: string;
  username: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  status: "ACTIVE" | "BLOCKED" | "REMOVED";
  friendsSince: string;
}

/**
 * Odpowiedź API dla zaproszenia
 */
export interface ApiFriendRequestResponse {
  friendshipId: string;
  fromUserId: string;
  fromUsername: string;
  fromFirstName?: string;
  fromLastName?: string;
  toUserId: string;
  toUsername: string;
  status: "PENDING" | "ACCEPTED" | "REJECTED" | "BLOCKED";
  sentAt: string;
}

/**
 * Odpowiedź API dla statystyk znajomych
 */
export interface ApiFriendshipStatsResponse {
  totalFriends: number;
  pendingReceived: number;
  pendingSent: number;
  blockedUsers: number;
}

/**
 * Odpowiedź API dla wyszukiwania użytkowników
 */
export interface ApiUserSearchResult {
  userId: string;
  username: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  isFriend: boolean;
  hasPendingRequest: boolean;
  requestDirection?: "SENT" | "RECEIVED";
}

/**
 * Odpowiedź API dla zablokowanego użytkownika
 * (taki sam format jak ApiFriendResponse - różni się tylko status)
 */
export interface ApiBlockedUserResponse {
  friendshipId: string;
  friendId: string;
  username: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  status: string;
  friendsSince: string;
}

// ============================================
// MAPOWANIE Z API NA TYPY FRONTENDOWE
// ============================================

const mapApiFriendToFriend = (api: ApiFriendResponse): IFriend => ({
  id: api.friendshipId,
  userId: api.friendId,
  username: api.username,
  email: api.email,
  rankPosition: 0, // Do uzupełnienia z osobnego endpointu statystyk
  totalPoints: 0,
  friendsSince: api.friendsSince,
  isActive: api.status === "ACTIVE",
  lastActiveAt: undefined,
  stats: undefined,
});

const mapApiRequestToFriendRequest = (
  api: ApiFriendRequestResponse
): FriendRequest => ({
  id: api.friendshipId,
  senderId: api.fromUserId,
  senderUsername: api.fromUsername,
  receiverId: api.toUserId,
  receiverUsername: api.toUsername,
  status: api.status,
  createdAt: api.sentAt,
});

const mapApiSearchResult = (api: ApiUserSearchResult): UserSearchResult => ({
  userId: api.userId,
  username: api.username,
  firstName: api.firstName,
  lastName: api.lastName,
  isFriend: api.isFriend,
  hasPendingRequest: api.hasPendingRequest,
  requestDirection: api.requestDirection?.toLowerCase() as
    | "sent"
    | "received"
    | undefined,
});

const mapApiBlockedUser = (api: ApiBlockedUserResponse): BlockedUser => ({
  userId: api.friendId,
  username: api.username,
  blockedAt: api.friendsSince,
});

// ============================================
// SERWIS ZNAJOMYCH
// ============================================

export const friendsApiService = {
  /**
   * Pobiera listę znajomych z paginacją
   * GET /api/v1/friends
   */
  async getFriends(
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<IFriend>> {
    const response = await apiClient.get<PageResponse<ApiFriendResponse>>(
      BASE_URL,
      {
        params: {
          page,
          size,
        },
      }
    );

    return {
      ...response.data,
      content: response.data.content.map(mapApiFriendToFriend),
    };
  },

  /**
   * Pobiera wszystkich znajomych (bez paginacji)
   */
  async getAllFriends(): Promise<IFriend[]> {
    const response = await apiClient.get<ApiFriendResponse[]>(
      `${BASE_URL}/all`
    );
    return response.data.map(mapApiFriendToFriend);
  },

  /**
   * Pobiera statystyki znajomych
   */
  async getStats(): Promise<ApiFriendshipStatsResponse> {
    const response = await apiClient.get<ApiFriendshipStatsResponse>(
      `${BASE_URL}/stats`
    );
    return response.data;
  },

  /**
   * Sprawdza czy użytkownicy są znajomymi
   */
  async checkFriendship(
    otherUserId: string
  ): Promise<{ isFriend: boolean; status?: string }> {
    const response = await apiClient.get<{
      isFriend: boolean;
      status?: string;
    }>(`${BASE_URL}/check/${otherUserId}`);
    return response.data;
  },

  /**
   * Usuwa znajomego
   */
  async removeFriend(friendId: string): Promise<void> {
    await apiClient.delete(`${BASE_URL}/${friendId}`);
  },

  /**
   * Blokuje użytkownika
   */
  async blockUser(userToBlockId: string): Promise<void> {
    await apiClient.post(`${BASE_URL}/block/${userToBlockId}`);
  },

  /**
   * Odblokowuje użytkownika
   */
  async unblockUser(userToUnblockId: string): Promise<void> {
    await apiClient.post(`${BASE_URL}/unblock/${userToUnblockId}`);
  },

  /**
   * Pobiera zablokowanych użytkowników
   */
  async getBlockedUsers(): Promise<BlockedUser[]> {
    const response = await apiClient.get<PageResponse<ApiBlockedUserResponse>>(
      `${BASE_URL}/blocked`
    );
    return response.data.content.map(mapApiBlockedUser);
  },
};

// ============================================
// SERWIS ZAPROSZEŃ
// ============================================

export const friendRequestsApiService = {
  /**
   * Wysyła zaproszenie do znajomych
   */
  async sendRequest(targetUserId: string): Promise<ApiFriendRequestResponse> {
    const response = await apiClient.post<ApiFriendRequestResponse>(
      `${BASE_URL}/requests`,
      { targetUserId }
    );
    return response.data;
  },

  /**
   * Pobiera otrzymane zaproszenia
   */
  async getReceivedRequests(): Promise<FriendRequest[]> {
    const response = await apiClient.get<
      PageResponse<ApiFriendRequestResponse>
    >(`${BASE_URL}/requests/received`);
    return response.data.content.map(mapApiRequestToFriendRequest);
  },

  /**
   * Pobiera wysłane zaproszenia
   */
  async getSentRequests(): Promise<FriendRequest[]> {
    const response = await apiClient.get<
      PageResponse<ApiFriendRequestResponse>
    >(`${BASE_URL}/requests/sent`);
    return response.data.content.map(mapApiRequestToFriendRequest);
  },

  /**
   * Akceptuje zaproszenie
   */
  async acceptRequest(friendshipId: string): Promise<void> {
    await apiClient.post(`${BASE_URL}/requests/${friendshipId}/accept`);
  },

  /**
   * Odrzuca zaproszenie
   */
  async rejectRequest(friendshipId: string): Promise<void> {
    await apiClient.post(`${BASE_URL}/requests/${friendshipId}/reject`);
  },

  /**
   * Anuluje wysłane zaproszenie
   */
  async cancelRequest(friendshipId: string): Promise<void> {
    await apiClient.delete(`${BASE_URL}/requests/${friendshipId}`);
  },
};

// ============================================
// SERWIS WYSZUKIWANIA
// ============================================

export const userSearchApiService = {
  /**
   * Wyszukuje użytkowników
   */
  async searchUsers(
    query: string,
    page: number = 0,
    size: number = 20
  ): Promise<UserSearchResult[]> {
    if (!query || query.length < 2) {
      return [];
    }

    const response = await apiClient.get<PageResponse<ApiUserSearchResult>>(
      `${BASE_URL}/search`,
      {
        params: { query, page, size },
      }
    );

    return response.data.content.map(mapApiSearchResult);
  },
};

// ============================================
// SERWIS STATYSTYK ZNAJOMYCH
// ============================================

const STATS_BASE_URL = "/v1/stats/friends";

/**
 * Odpowiedź API dla statystyk użytkownika
 */
export interface ApiUserStatsResponse {
  userId: string;
  username: string;
  totalPoints: number;
  weeklyPoints: number;
  globalRank: number;
  totalSessions: number;
  streakDays: number;
  accuracy: number;
  totalCorrect: number;
  totalAnswers: number;
  lastActiveAt?: string;
}

/**
 * Odpowiedź API dla wzbogaconej listy znajomych
 */
export interface ApiFriendEnrichedResponse {
  friendId: string;
  username: string;
  totalPoints: number;
  weeklyPoints: number;
  globalRank: number;
}

export const friendsStatsApiService = {
  /**
   * Pobiera statystyki konkretnego użytkownika
   */
  async getUserStats(userId: string): Promise<UserStats> {
    const response = await apiClient.get<ApiUserStatsResponse>(
      `${STATS_BASE_URL}/user/${userId}`
    );
    return response.data;
  },

  /**
   * Pobiera wzbogaconą listę znajomych (z punktami i rankingiem)
   */
  async getFriendsEnriched(): Promise<FriendEnriched[]> {
    const response = await apiClient.get<ApiFriendEnrichedResponse[]>(
      `${STATS_BASE_URL}/enriched`
    );
    return response.data;
  },
};
