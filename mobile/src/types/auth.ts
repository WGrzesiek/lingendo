/**
 * Typy konta użytkownika
 */
export type AccountType = 'BASIC' | 'PREMIUM' | 'STUDENT' | 'TEACHER';

/**
 * Typy użytkownika (rola)
 */
export type UserType = 'NORMAL' | 'ADMIN';

/**
 * Interfejs użytkownika
 */
export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  accountType: AccountType;
  userType: UserType;
  isEnabled: boolean;
  createdAt: string;
}

/**
 * Dane logowania
 */
export interface LoginCredentials {
  username: string;
  password: string;
}

/**
 * Dane rejestracji
 */
export interface SignupData {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  accountType: AccountType;
  userType: UserType;
}
