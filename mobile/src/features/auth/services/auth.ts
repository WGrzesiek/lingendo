import apiClient from '../../../lib/api/axios';
import { storage } from '../../../lib/storage';
import type { LoginRequest, SignupRequest, User, LoginResponse } from '../types';

const BASE_URL = '/v1/gateway';


/**
 * Logowanie użytkownika
 * Zapisuje tokeny do SecureStore i zwraca dane użytkownika
 */
export const login = async (data: LoginRequest): Promise<void> => {
  console.log('[Auth] Próba logowania:', data.username);

  const response = await apiClient.post<LoginResponse>(`${BASE_URL}/login`, data);

  const { accessToken, refreshToken } = response.data;

  if (accessToken) {
    await storage.setAccessToken(accessToken);
    console.log('[Auth] Access token zapisany');
  }

  if (refreshToken) {
    await storage.setRefreshToken(refreshToken);
    console.log('[Auth] Refresh token zapisany');
  }
};

/**
 * Rejestracja nowego użytkownika
 */
export const signup = async (data: SignupRequest): Promise<void> => {
  console.log('[Auth] Próba rejestracji:', data.username);
  await apiClient.post('/v1/users/register', data);
  console.log('[Auth] Rejestracja pomyślna');
};

/**
 * Wylogowanie użytkownika
 * Usuwa tokeny z SecureStore i wywołuje endpoint logout
 */
export const logout = async (): Promise<void> => {
  try {
    console.log('[Auth] Próba wylogowania...');
    await apiClient.post(`${BASE_URL}/logout`);
  } catch (error) {
    console.error('[Auth] Błąd podczas wylogowania (ignorowany):', error);
  } finally {
    await storage.clearAll();
    console.log('[Auth] Wylogowano - dane lokalne wyczyszczone');
  }
};

/**
 * Pobiera dane aktualnie zalogowanego użytkownika
 */
export const getCurrentUser = async (): Promise<User> => {
  const response = await apiClient.get<User>(`${BASE_URL}/me`);
  if (!response.data) {
    await storage.clearAll();
    throw new Error('Nie udało się pobrać danych użytkownika');
  }
  return response.data;
};
