/**
 * Typy dla modułu ustawień użytkownika
 */

/**
 * Profil użytkownika
 */
export interface UserProfile {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  userType: "NORMAL" | "ADMIN";
  accountType: "STUDENT" | "TEACHER" | "BASIC" | "PREMIUM";
  createdAt: string;
  lastLogin?: string;
  streak: number;
}

/**
 * Request do aktualizacji profilu
 */
export interface UpdateProfileRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
}

/**
 * Request do zmiany hasła
 */
export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

/**
 * Response po zmianie hasła
 */
export interface ChangePasswordResponse {
  message: string;
}
