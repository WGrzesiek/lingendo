"use client";

import { useQuery } from "@tanstack/react-query";
import { getCurrentUser } from "../services/auth";
import type { User } from "../types";

/**
 * Klucz cache dla React Query identyfikujący zapytanie o aktualnego użytkownika
 * Używany do invalidacji cache po login/logout
 */
export const CURRENT_USER_KEY = ["current-user"] as const;

/**
 * Hook do pobierania danych aktualnie zalogowanego użytkownika z endpointu /me
 * Wykorzystuje React Query do cache'owania - wielokrotne wywołania w różnych komponentach
 * wykonują tylko JEDEN request HTTP, pozostałe pobierają dane z cache
 *
 * @returns Obiekt z React Query zawierający:
 *   - data: User | null - dane użytkownika lub null gdy niezalogowany
 *   - isLoading: boolean - czy trwa pobieranie danych
 *   - error: Error | null - błąd jeśli wystąpił
 *   - refetch: () => Promise - funkcja do ręcznego odświeżenia danych
 *
 */
export function useCurrentUser() {
  return useQuery<User | null>({
    queryKey: CURRENT_USER_KEY,
    queryFn: async () => {
      try {
        return await getCurrentUser();
      } catch (error: unknown) {
        // Jeśli 401 (unauthorized) - zwróć null zamiast error
        if (error && typeof error === "object" && "response" in error) {
          const axiosError = error as { response?: { status?: number } };
          if (axiosError.response?.status === 401) {
            return null;
          }
        }
        throw error;
      }
    },
    staleTime: 1000 * 30,
    retry: false,
    refetchOnMount: true,
    refetchOnWindowFocus: true,
    refetchOnReconnect: true,
  });
}
