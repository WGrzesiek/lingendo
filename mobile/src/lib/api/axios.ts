import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { storage } from '../storage';

/**
 * Bazowy URL API
 * W produkcji zmień na właściwy adres
 */
const API_BASE_URL = 'http://staging.ibis-tautara.ts.net:8811/api';

/**
 * Skonfigurowana instancja klienta Axios do komunikacji z API
 * Automatycznie dołącza Bearer token z SecureStore
 */
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

/**
 * Interceptor requestów - automatycznie dodaje token do nagłówka Authorization
 */
apiClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    const token = await storage.getAccessToken();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    console.log('[Axios] Wysyłam request:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => Promise.reject(error)
);

/**
 * Promise przechowujący aktualny proces odświeżania tokenu
 */
let refreshingPromise: Promise<boolean> | null = null;

/**
 * Odświeża access token używając refresh token
 */
async function refreshAccessToken(): Promise<boolean> {
  if (refreshingPromise) {
    return refreshingPromise;
  }

  refreshingPromise = (async () => {
    try {
      const refreshToken = await storage.getRefreshToken();
      if (!refreshToken) {
        console.log('[Axios] Brak refresh token, nie można odświeżyć');
        return false;
      }

      console.log('[Axios] Próba odświeżenia tokenu...');

      const response = await axios.post(
        `${API_BASE_URL}/v1/gateway/refresh`,
        {},
        {
          headers: {
            Authorization: `Bearer ${refreshToken}`,
            'Content-Type': 'application/json',
          },
        }
      );

      const { accessToken, refreshToken: newRefreshToken } = response.data;

      if (accessToken) {
        await storage.setAccessToken(accessToken);
        console.log('[Axios] Access token odświeżony pomyślnie');
      }

      if (newRefreshToken) {
        await storage.setRefreshToken(newRefreshToken);
        console.log('[Axios] Refresh token zaktualizowany');
      }

      return true;
    } catch (error) {
      console.error('[Axios] Nie udało się odświeżyć tokenu:', error);
      await storage.clearAll();
      return false;
    } finally {
      refreshingPromise = null;
    }
  })();

  return refreshingPromise;
}

/**
 * Interceptor odpowiedzi - automatycznie odświeża token przy błędzie 401
 */
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
    const status = error.response?.status;
    const url = originalRequest?.url ?? '';

    if (status === 401 && url.includes('/me')) {
      return Promise.reject(error);
    }

    if (
      status === 401 &&
      !originalRequest._retry &&
      !url.includes('/login') &&
      !url.includes('/refresh') &&
      !url.includes('/register')
    ) {
      originalRequest._retry = true;

      const refreshed = await refreshAccessToken();

      if (refreshed) {
        const newToken = await storage.getAccessToken();
        if (newToken && originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
        }
        console.log('[Axios] Ponawiam oryginalny request...');
        return apiClient(originalRequest);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
