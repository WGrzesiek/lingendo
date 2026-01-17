import apiClient from '@/lib/api/axios';
import { storage } from '@/lib/storage';
import { ENDPOINTS } from '@/constants';
import type { LoginRequest, SignupRequest, User, LoginResponse } from '../types';

/**
 * Serwis autoryzacji - Object Literal Module Pattern
 * Obsługuje logowanie, rejestrację, wylogowanie i pobieranie użytkownika
 */
export const AuthService = {
  /**
   * Logowanie użytkownika
   * Zapisuje tokeny do SecureStore
   */
  async login(data: LoginRequest): Promise<void> {
    console.log('[AuthService] Próba logowania:', data.username);

    const response = await apiClient.post<LoginResponse>(ENDPOINTS.AUTH.LOGIN, data);
    const { accessToken, refreshToken } = response.data;

    if (accessToken) {
      await storage.setAccessToken(accessToken);
      console.log('[AuthService] Access token zapisany');
    }

    if (refreshToken) {
      await storage.setRefreshToken(refreshToken);
      console.log('[AuthService] Refresh token zapisany');
    }
  },

  /**
   * Rejestracja nowego użytkownika
   */
  async signup(data: SignupRequest): Promise<void> {
    console.log('[AuthService] Próba rejestracji:', data.username);
    await apiClient.post(ENDPOINTS.AUTH.REGISTER, data);
    console.log('[AuthService] Rejestracja pomyślna');
  },

  /**
   * Wylogowanie użytkownika
   * Usuwa tokeny z SecureStore i wywołuje endpoint logout
   */
  async logout(): Promise<void> {
    try {
      console.log('[AuthService] Próba wylogowania...');
      await apiClient.post(ENDPOINTS.AUTH.LOGOUT);
    } catch (error) {
      console.error('[AuthService] Błąd podczas wylogowania (ignorowany):', error);
    } finally {
      await storage.clearAll();
      console.log('[AuthService] Wylogowano - dane lokalne wyczyszczone');
    }
  },

  /**
   * Pobiera dane aktualnie zalogowanego użytkownika
   */
  async getCurrentUser(): Promise<User> {
    const response = await apiClient.get<User>(ENDPOINTS.AUTH.ME);
    if (!response.data) {
      await storage.clearAll();
      throw new Error('Nie udało się pobrać danych użytkownika');
    }
    return response.data;
  },
} as const;
