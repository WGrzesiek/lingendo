import apiClient from "@/lib/api/axios";
import type { LoginRequest, SignupRequest, User } from "../types";

const BASE_URL = "api/v1/gateway";
/**
 * Logowanie użytkownika
 * Backend ustawia access token w httpOnly cookie
 * @param data - Dane logowania zawierające username i password
 * @returns Promise który rozwiązuje się po udanym logowaniu
 */
export const login = async (data: LoginRequest): Promise<void> => {
  await apiClient.post(`${BASE_URL}/login`, data);
  console.log("[Login] Zalogowano pomyślnie, token w cookie");
};

/**
 * Rejestracja nowego użytkownika
 * @param data - Dane rejestracyjne zawierające email, password i opcjonalnie name
 * @returns Promise który rozwiązuje się po udanej rejestracji
 */
export const signup = async (data: SignupRequest): Promise<void> => {
  await apiClient.post(`${BASE_URL}/signup`, data);
};

/**
 * Wylogowanie użytkownika
 * Backend usuwa cookie z tokenem
 * @returns Promise który rozwiązuje się po udanym wylogowaniu
 * @throws Error jeśli wylogowanie nie powiedzie się
 */
export const logout = async (): Promise<void> => {
  try {
    await apiClient.post(`${BASE_URL}/logout`);
    console.log("[Logout] Wylogowano pomyślnie");
  } catch (error) {
    console.error("[Logout] Błąd wylogowania:", error);
    throw error;
  }
};

/**
 * Pobiera dane aktualnie zalogowanego użytkownika z endpoint /me
 * Backend automatycznie sprawdza access token z cookie
 * @returns Promise z danymi użytkownika (User)
 * @throws Error jeśli użytkownik nie jest zalogowany lub token jest nieprawidłowy
 */
export const getCurrentUser = async (): Promise<User> => {
  const response = await apiClient.get<User>(`${BASE_URL}/me`);
  return response.data;
};
