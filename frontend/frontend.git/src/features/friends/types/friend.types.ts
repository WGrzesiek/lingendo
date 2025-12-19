/**
 * Znajomy użytkownika z danymi rankingowymi
 */
export interface IFriend {
  /** ID użytkownika */
  userId: string;
  /** Nazwa użytkownika */
  username: string;
  /** Pozycja w rankingu globalnym */
  rankPosition: number;
  /** Całkowita liczba zdobytych punktów */
  totalPoints: number;
  /** Data dodania do znajomych */
  friendsSince: string;
  /** Czy użytkownik jest aktywny */
  isActive: boolean;
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
  /** Nazwa użytkownika do dodania */
  username: string;
}

/**
 * Response po dodaniu znajomego
 */
export interface AddFriendResponse {
  success: boolean;
  friend: IFriend;
  message: string;
}
