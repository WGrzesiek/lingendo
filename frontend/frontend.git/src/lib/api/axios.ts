import axios from "axios";
import { redirect } from "next/navigation";
import { TokenStore } from "@/lib/tokenStore";

const apiClient = axios.create({
  // baseURL: "http://staging.ibis-tautara.ts.net:8811/api/v1/gateway",
  baseURL: "/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true,
});

// REQUEST INTERCEPTOR - dodaje token
apiClient.interceptors.request.use(
  (config) => {
    const token = TokenStore.get();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Zapobiega wielokrotnemu refresh jednocześnie
let refreshing: Promise<string | null> | null = null;

async function refreshAccess(): Promise<string | null> {
  if (!refreshing) {
    refreshing = (async () => {
      try {
        const res = await axios.post(
          `${process.env.NEXT_PUBLIC_API_URL}/refresh`,
          {},
          { withCredentials: true }
        );
        const accessToken = res.data?.accessToken;
        TokenStore.set(accessToken ?? null);
        return accessToken ?? null;
      } catch {
        TokenStore.set(null);
        return null;
      } finally {
        refreshing = null;
      }
    })();
  }
  return refreshing;
}

// RESPONSE INTERCEPTOR - auto-refresh przy 401
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const newToken = await refreshAccess();

      if (newToken) {
        // Ponów request z nowym tokenem
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return apiClient(originalRequest);
      } else {
        // Refresh się nie udał - redirect do loginu
        redirect("/login");
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
