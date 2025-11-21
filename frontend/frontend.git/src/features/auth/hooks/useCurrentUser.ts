// "use client";

// import { useState, useEffect } from "react";
// import { getCurrentUser } from "../services/auth";
// import type { User } from "../types";

// /**
//  * Hook do pobierania danych aktualnie zalogowanego użytkownika z /me
//  * Automatycznie pobiera dane przy montowaniu komponentu
//  */
// export const useCurrentUser = () => {
//   const [user, setUser] = useState<User | null>(null);
//   const [isLoading, setIsLoading] = useState(true);
//   const [error, setError] = useState<string | null>(null);

//   useEffect(() => {
//     const fetchUser = async () => {
//       try {
//         setIsLoading(true);
//         const userData = await getCurrentUser();
//         setUser(userData);
//         setError(null);
//       } catch (err) {
//         console.error("Failed to fetch current user:", err);
//         setError("Failed to load user data");
//         setUser(null);
//       } finally {
//         setIsLoading(false);
//       }
//     };

//     fetchUser();
//   }, []);

//   return { user, isLoading, error };
// };
"use client";

import { useQuery } from "@tanstack/react-query";
import { getCurrentUser } from "../services/auth";
import type { User } from "../types";

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
 */
export function useCurrentUser() {
  return useQuery<User | null>({
    queryKey: ["current-user"],
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
    staleTime: 1000 * 60 * 5,
    retry: false, // Nie retry jeśli user nie jest zalogowany
    refetchOnMount: false,
    refetchOnWindowFocus: false,
    refetchOnReconnect: true,
  });
}
