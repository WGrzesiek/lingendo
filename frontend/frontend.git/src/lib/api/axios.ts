import axios from "axios";

/**
 * Skonfigurowana instancja klienta Axios do komunikacji z API
 * Automatycznie dołącza credentials (cookies) do każdego zapytania
 * Obsługuje automatyczne odświeżanie tokenów przy błędzie 401
 */
const apiClient = axios.create({
  baseURL: "http://staging.ibis-tautara.ts.net:8811/api",
  // baseURL: "/api",
  timeout: 3000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true,
});

/**
 * Interceptor requestów - loguje informacje o każdym wychodzącym zapytaniu
 */
apiClient.interceptors.request.use(
  (config) => {
    console.log(
      "[Axios] Wysyłam request:",
      config.method?.toUpperCase(),
      config.url
    );
    return config;
  },
  (error) => Promise.reject(error)
);

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
        console.log("[Axios] Token odświeżony pomyślnie");
      } catch (error) {
        console.error("[Axios] Nie udało się odświeżyć tokenu");
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
 * Jeśli odświeżenie się nie powiedzie, przekierowuje na stronę logowania
 */
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;
    const url = originalRequest?.url ?? "";

    // Jezeli blad z /me to nie odswiezamy
    if (status === 401 && url.includes("/me")) {
      return Promise.reject(error);
    }

    if (
      status === 401 &&
      !originalRequest._retry &&
      !url.includes("/login") &&
      !url.includes("/refresh")
    ) {
      originalRequest._retry = true;

      console.log("[Axios] Otrzymano 401, próba odświeżenia tokenu...");

      const refreshed = await refreshAccess();

      if (refreshed) {
        console.log("[Axios] Ponawiam oryginalny request...");
        return apiClient(originalRequest);
      } else {
        console.log("[Axios] Refresh nieudany, przekierowanie na /login");
        if (
          typeof window !== "undefined" &&
          window.location.pathname !== "/login"
        ) {
          window.location.href = "/login";
        }
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
