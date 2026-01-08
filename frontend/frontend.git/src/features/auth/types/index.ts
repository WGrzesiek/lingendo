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
export type AccountType = "BASIC" | "PREMIUM" | "STUDENT" | "TEACHER";

/**
 * Dane wymagane do rejestracji nowego użytkownika
 */
export interface SignupRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  userType: "NORMAL";
  accountType: AccountType;
}

/**
 * Dane użytkownika zwracane z API
 */
export interface User {
  userId: string;
  username: string;
  accountType: "BASIC" | "PREMIUM" | "STUDENT" | "TEACHER";
  userType: "NORMAL" | "ADMIN";
  isEnabled: boolean;
}

/**
 * Struktura błędu uwierzytelniania
 */
export interface AuthError {
  message: string;
  code?: string;
}
