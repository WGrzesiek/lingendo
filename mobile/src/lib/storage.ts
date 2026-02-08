import * as SecureStore from 'expo-secure-store';
import { STORAGE_KEYS } from '@/constants';

/**
 * Serwis do bezpiecznego przechowywania danych na urządzeniu
 * Używa expo-secure-store (Keychain na iOS, Keystore na Android)
 * Object Literal Module Pattern
 */
export const storage = {
  /**
   * Zapisuje access token
   */
  async setAccessToken(token: string): Promise<void> {
    await SecureStore.setItemAsync(STORAGE_KEYS.ACCESS_TOKEN, token);
  },

  /**
   * Pobiera access token
   */
  async getAccessToken(): Promise<string | null> {
    return await SecureStore.getItemAsync(STORAGE_KEYS.ACCESS_TOKEN);
  },

  /**
   * Usuwa access token
   */
  async removeAccessToken(): Promise<void> {
    await SecureStore.deleteItemAsync(STORAGE_KEYS.ACCESS_TOKEN);
  },

  /**
   * Zapisuje refresh token
   */
  async setRefreshToken(token: string): Promise<void> {
    await SecureStore.setItemAsync(STORAGE_KEYS.REFRESH_TOKEN, token);
  },

  /**
   * Pobiera refresh token
   */
  async getRefreshToken(): Promise<string | null> {
    return await SecureStore.getItemAsync(STORAGE_KEYS.REFRESH_TOKEN);
  },

  /**
   * Usuwa refresh token
   */
  async removeRefreshToken(): Promise<void> {
    await SecureStore.deleteItemAsync(STORAGE_KEYS.REFRESH_TOKEN);
  },

  /**
   * Zapisuje dane użytkownika jako JSON
   */
  async setUser<T>(user: T): Promise<void> {
    await SecureStore.setItemAsync(STORAGE_KEYS.USER, JSON.stringify(user));
  },

  /**
   * Pobiera dane użytkownika
   */
  async getUser<T>(): Promise<T | null> {
    const userJson = await SecureStore.getItemAsync(STORAGE_KEYS.USER);
    if (!userJson) return null;
    try {
      return JSON.parse(userJson) as T;
    } catch {
      return null;
    }
  },

  /**
   * Usuwa dane użytkownika
   */
  async removeUser(): Promise<void> {
    await SecureStore.deleteItemAsync(STORAGE_KEYS.USER);
  },

  /**
   * Czyści wszystkie dane (auth)
   */
  async clearAll(): Promise<void> {
    await Promise.all([
      SecureStore.deleteItemAsync(STORAGE_KEYS.ACCESS_TOKEN),
      SecureStore.deleteItemAsync(STORAGE_KEYS.REFRESH_TOKEN),
      SecureStore.deleteItemAsync(STORAGE_KEYS.USER),
    ]);
  },
} as const;
