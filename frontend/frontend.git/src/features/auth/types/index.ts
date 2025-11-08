/**
 * Dane wymagane do logowania użytkownika
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * Dane wymagane do rejestracji nowego użytkownika
 */
export interface SignupRequest {
  email: string;
  password: string;
  name?: string;
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
