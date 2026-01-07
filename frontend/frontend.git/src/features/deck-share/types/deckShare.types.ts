/**
 * Typ celu udostępnienia talii
 */
export type ShareTargetType = "GROUP" | "ALL_STUDENTS" | "ALL_FRIENDS" | "USER";

/**
 * Status udostępnienia
 */
export type ShareStatus = "ACTIVE" | "REVOKED" | "EXPIRED";

/**
 * Request do udostępnienia talii
 */
export interface ShareDeckRequest {
  targetType: ShareTargetType;
  targetId?: string;
  message?: string;
  expiresAt?: string;
}

/**
 * Request do batch udostępnienia talii
 */
export interface BatchShareDeckRequest {
  targetType: ShareTargetType;
  targetIds: string[];
  message?: string;
  expiresAt?: string;
}

/**
 * Odpowiedź z informacją o udostępnieniu
 */
export interface DeckShareResponse {
  id: string;
  deckId: string;
  deckName: string;
  ownerId: string;
  ownerName: string;
  targetType: ShareTargetType;
  targetId?: string;
  targetName?: string;
  status: ShareStatus;
  message?: string;
  sharedAt: string;
  expiresAt?: string;
  revokedAt?: string;
}

/**
 * Odpowiedź z wynikiem batch operacji
 */
export interface BatchShareResponse {
  success: string[];
  failed: string[];
  errors: string[];
  totalProcessed: number;
  successCount: number;
  failedCount: number;
}

/**
 * Talia udostępniona użytkownikowi
 */
export interface SharedDeckDto {
  deckId: string;
  deckName: string;
  description?: string;
  ownerId: string;
  ownerName: string;
  /** Jak udostępniono: GROUP, ALL_STUDENTS, ALL_FRIENDS, USER */
  sharedVia: ShareTargetType;
  /** Nazwa źródła: nazwa grupy, "Wszyscy uczniowie", itp. */
  sharedViaName?: string;
  message?: string;
  flashcardCount: number;
  languageFrom?: string;
  languageTo?: string;
  difficulty?: string;
  category?: string;
}

/**
 * Konfiguracja wyświetlania typu udostępnienia
 */
export const shareTargetConfig: Record<
  ShareTargetType,
  {
    label: string;
    description: string;
    icon: string;
  }
> = {
  GROUP: {
    label: "Grupa",
    description: "Udostępnij konkretnej grupie uczniów",
    icon: "Users",
  },
  ALL_STUDENTS: {
    label: "Wszyscy uczniowie",
    description: "Udostępnij wszystkim Twoim uczniom",
    icon: "GraduationCap",
  },
  ALL_FRIENDS: {
    label: "Wszyscy znajomi",
    description: "Udostępnij wszystkim znajomym",
    icon: "UserPlus",
  },
  USER: {
    label: "Konkretny użytkownik",
    description: "Udostępnij wybranemu użytkownikowi",
    icon: "User",
  },
};

/**
 * Konfiguracja wyświetlania statusu udostępnienia
 */
export const shareStatusConfig: Record<
  ShareStatus,
  {
    label: string;
    className: string;
  }
> = {
  ACTIVE: {
    label: "Aktywne",
    className: "bg-green-100 text-green-700 border-green-200",
  },
  REVOKED: {
    label: "Wycofane",
    className: "bg-red-100 text-red-700 border-red-200",
  },
  EXPIRED: {
    label: "Wygasłe",
    className: "bg-gray-100 text-gray-700 border-gray-200",
  },
};
