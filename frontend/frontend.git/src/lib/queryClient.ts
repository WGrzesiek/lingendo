import { QueryClient } from "@tanstack/react-query";

/**
 * Globalny klient React Query z konfiguracją cache'owania dla całej aplikacji
 *
 * Konfiguracja:
 * - staleTime: 5 minut - dane traktowane jako świeże przez 5 minut (bez automatycznego refetchu)
 * - gcTime: 10 minut - cache usuwany po 10 minutach nieużywania (dawniej cacheTime)
 * - refetchOnMount: false - NIE refetchuj jeśli dane są świeże przy montowaniu komponentu
 * - refetchOnWindowFocus: false - NIE refetchuj automatycznie gdy wrócisz do okna przeglądarki
 * - refetchOnReconnect: "always" - ZAWSZE refetchuj gdy połączenie internetowe zostanie przywrócone
 *
 * Dzięki tej konfiguracji wielokrotne wywołania useCurrentUser() w navbar, dashboard, itp.
 * wykonają tylko JEDEN request HTTP - pozostałe użyją cache
 */
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5,
      gcTime: 1000 * 60 * 10,
      refetchOnMount: false,
      refetchOnWindowFocus: false,
      refetchOnReconnect: "always",
    },
  },
});
