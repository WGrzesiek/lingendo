/**
 * Status zaproszenia do znajomych
 */
export type FriendRequestStatus =
  | "PENDING"
  | "ACCEPTED"
  | "REJECTED"
  | "BLOCKED";

/**
 * Status znajomości
 */
export type FriendshipStatus = "ACTIVE" | "BLOCKED" | "REMOVED";

/**
 * Znajomy użytkownika z danymi rankingowymi
 */
export interface IFriend {
  /** ID znajomości */
  id: string;
  userId: string;
  username: string;
  email?: string;
  rankPosition: number;
  totalPoints: number;
  friendsSince: string;
  isActive: boolean;
  lastActiveAt?: string;
  stats?: FriendStats;
}

/**
 * Statystyki znajomego
 */
export interface FriendStats {
  totalSessions: number;
  totalCorrectAnswers: number;
  totalAnswers: number;
  accuracy: number;
  streakDays: number;
  weeklyPoints: number;
}

/**
 * Zaproszenie do znajomych
 */
export interface FriendRequest {
  id: string;
  senderId: string;
  senderUsername: string;
  senderAvatarUrl?: string;
  receiverId: string;
  receiverUsername: string;
  receiverAvatarUrl?: string;
  status: FriendRequestStatus;
  message?: string;
  createdAt: string;
  updatedAt?: string;
}

/**
 * Wynik wyszukiwania użytkownika
 */
export interface UserSearchResult {
  userId: string;
  username: string;
  firstName?: string;
  lastName?: string;
  avatarUrl?: string;
  rankPosition?: number;
  totalPoints?: number;
  isFriend: boolean;
  hasPendingRequest: boolean;
  requestDirection?: "sent" | "received";
}

/**
 * Odpowiedź API z listą znajomych
 */
export interface FriendsListResponse {
  content: IFriend[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
}

/**
 * Request do dodania znajomego
 */
export interface AddFriendRequest {
  username: string;
  message?: string;
}

/**
 * Response po dodaniu znajomego
 */
export interface AddFriendResponse {
  success: boolean;
  friend: IFriend;
  message: string;
}

/**
 * Parametry paginacji dla listy znajomych
 */
export interface FriendsPaginationParams {
  page?: number;
  size?: number;
}

/**
 * Zablokowany użytkownik
 */
export interface BlockedUser {
  userId: string;
  username: string;
  avatarUrl?: string;
  blockedAt: string;
  reason?: string;
}

/**
 * Statystyki użytkownika dla widoku szczegółów znajomego
 */
export interface UserStats {
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
 * Znajomy wzbogacony o punkty i ranking
 */
export interface FriendEnriched {
  friendId: string;
  username: string;
  totalPoints: number;
  weeklyPoints: number;
  globalRank: number;
}
