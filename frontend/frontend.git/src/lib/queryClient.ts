import { QueryClient } from "@tanstack/react-query";

/**
 * Globalny klient React Query z konfiguracją cache'owania dla całej aplikacji
 *
 * Konfiguracja:
 * - staleTime: 1 minuta - krótki cache bez utrzymywania długo nieaktualnych danych
 * - gcTime: 10 minut - cache usuwany po 10 minutach nieużywania (dawniej cacheTime)
 * - refetchOnMount: true - odświeżaj dane po ponownym wejściu do widoku
 * - refetchOnWindowFocus: true - synchronizuj stan po powrocie do aplikacji
 * - refetchOnReconnect: "always" - ZAWSZE refetchuj gdy połączenie internetowe zostanie przywrócone
 *
 * Dzięki tej konfiguracji wielokrotne wywołania useCurrentUser() w navbar, dashboard, itp.
 * wykonają tylko JEDEN request HTTP - pozostałe użyją cache
 */
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60,
      gcTime: 1000 * 60 * 10,
      refetchOnMount: true,
      refetchOnWindowFocus: true,
      refetchOnReconnect: "always",
      retry: 1,
    },
  },
});
