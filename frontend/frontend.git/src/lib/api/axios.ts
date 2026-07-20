import axios from "axios";

/**
 * Skonfigurowana instancja klienta Axios do komunikacji z API
 * Automatycznie dołącza credentials (cookies) do każdego zapytania
 * Obsługuje automatyczne odświeżanie tokenów przy błędzie 401
 */
const apiClient = axios.create({
  baseURL: "/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true,
});

/**
 * Promise przechowujący aktualny proces odświeżania tokenu
 * Zapobiega wielokrotnym jednoczesnym próbom odświeżenia
 */
let refreshing: Promise<void> | null = null;

/**
 * Odświeża access token używając refresh token z cookies
 * Zapobiega wielokrotnym jednoczesnym wywołaniom refresh
 * @returns Promise z boolean - true jeśli odświeżenie się powiodło, false w przeciwnym razie
 */
async function refreshAccess(): Promise<boolean> {
  if (!refreshing) {
    refreshing = (async () => {
      try {
        await apiClient.post("/v1/gateway/refresh", {});
      } catch (error) {
        throw error;
      } finally {
        refreshing = null;
      }
    })();
  }

  try {
    await refreshing;
    return true;
  } catch {
    return false;
  }
}

/**
 * Interceptor odpowiedzi - automatycznie odświeża token przy błędzie 401
 * Jeśli odświeżenie się powiedzie, ponawia oryginalny request
 * Jeśli odświeżenie się nie powiedzie, zwraca pierwotny błąd 401. Warstwa
 * ochrony tras decyduje wtedy o przekierowaniu, zamiast robić twardy reload.
 */
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;
    const url = originalRequest?.url ?? "";

    if (
      status === 401 &&
      !originalRequest._retry &&
      !url.includes("/login") &&
      !url.includes("/refresh")
    ) {
      originalRequest._retry = true;

      const refreshed = await refreshAccess();

      if (refreshed) {
        return apiClient(originalRequest);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
