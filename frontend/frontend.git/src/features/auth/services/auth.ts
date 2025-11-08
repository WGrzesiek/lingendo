import apiClient from "@/lib/api/axios";
import type { LoginRequest, SignupRequest, User } from "../types";

/**
 * Logowanie użytkownika
 * Backend ustawia access token w httpOnly cookie
 */
export const login = async (data: LoginRequest): Promise<void> => {
  await apiClient.post("/login", data);
  console.log("✅ [Login] Zalogowano pomyślnie, token w cookie");
};

/**
 * Rejestracja nowego użytkownika
 */
export const signup = async (data: SignupRequest): Promise<void> => {
  await apiClient.post("/signup", data);
};

/**
 * Wylogowanie użytkownika
 * Backend usuwa cookie z tokenem
 */
export const logout = async (): Promise<void> => {
  try {
    await apiClient.post("/logout");
    console.log("✅ [Logout] Wylogowano pomyślnie");
  } catch (error) {
    console.error("❌ [Logout] Błąd wylogowania:", error);
    throw error;
  }
};

/**
 * Pobiera dane aktualnie zalogowanego użytkownika z endpoint /me
 * Backend automatycznie sprawdza access token z cookie
 */
export const getCurrentUser = async (): Promise<User> => {
  const response = await apiClient.get<User>("/me");
  return response.data;
};
