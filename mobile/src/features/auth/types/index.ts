/**
 * Dane wymagane do logowania użytkownika
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * Typ konta użytkownika
 */
export type AccountType = 'BASIC' | 'PREMIUM' | 'STUDENT' | 'TEACHER';

/**
 * Typ użytkownika (rola)
 */
export type UserType = 'NORMAL' | 'ADMIN';

/**
 * Dane wymagane do rejestracji nowego użytkownika
 */
export interface SignupRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
}

/**
 * Dane użytkownika zwracane z API
 */
export interface User {
  userId: string;
  username: string;
  accountType: AccountType;
  userType: UserType;
  isEnabled: boolean;
}

/**
 * Odpowiedź z logowania zawierająca tokeny
 */
export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
}

/**
 * Struktura błędu API
 */
export interface ApiErrorResponse {
  message: string;
  code?: string;
  status?: number;
}
