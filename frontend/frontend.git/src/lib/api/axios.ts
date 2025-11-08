import axios from "axios";

const apiClient = axios.create({
  // baseURL: "http://staging.ibis-tautara.ts.net:8811/api/v1/gateway",
  baseURL: "/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  // Ważne: withCredentials pozwala wysyłać cookies do backendu
  withCredentials: true,
});

// REQUEST INTERCEPTOR - nie potrzebny, bo token jest w cookie
// Backend automatycznie odbiera cookie z access tokenem
apiClient.interceptors.request.use(
  (config) => {
    console.log(
      "� [Axios] Wysyłam request:",
      config.method?.toUpperCase(),
      config.url
    );
    return config;
  },
  (error) => Promise.reject(error)
);

// Zapobiega wielokrotnemu refresh jednocześnie
let refreshing: Promise<void> | null = null;

async function refreshAccess(): Promise<boolean> {
  if (!refreshing) {
    refreshing = (async () => {
      try {
        // Backend odświeży access token w cookie na podstawie refresh tokenu
        await axios.post("/api/refresh", {}, { withCredentials: true });
        console.log("✅ [Axios] Token odświeżony pomyślnie");
      } catch (error) {
        console.error("❌ [Axios] Nie udało się odświeżyć tokenu");
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

// RESPONSE INTERCEPTOR - auto-refresh przy 401
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Jeśli otrzymaliśmy 401 i jeszcze nie próbowaliśmy odświeżyć
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      console.log("🔄 [Axios] Otrzymano 401, próba odświeżenia tokenu...");

      const refreshed = await refreshAccess();

      if (refreshed) {
        // Ponów request - backend ma już nowy token w cookie
        console.log("🔄 [Axios] Ponawiam oryginalny request...");
        return apiClient(originalRequest);
      } else {
        // Refresh się nie udał - przekieruj na login
        console.log("🔒 [Axios] Refresh nieudany, przekierowanie na /login");
        if (typeof window !== "undefined") {
          window.location.href = "/login";
        }
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
