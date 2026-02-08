"use client";

import { ReactNode } from "react";
import { QueryClientProvider } from "@tanstack/react-query";
import { ThemeProvider } from "@/components/theme-provider";
import { queryClient } from "@/lib/queryClient";

/**
 * Providers - Główny komponent opakowujący wszystkie client-side providery aplikacji
 *
 * Zawiera:
 * - QueryClientProvider (React Query) - zarządzanie stanem serwerowym i cache'owaniem
 * - ThemeProvider - obsługa motywów (dark/light mode)
 *
 * Używany w layout.tsx aby zachować Server Component i możliwość dodania metadata
 *
 * @param children - Komponenty potomne (cała aplikacja)
 */
export function Providers({ children }: { children: ReactNode }) {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider
        attribute="class"
        defaultTheme="system"
        enableSystem
        disableTransitionOnChange
      >
        {children}
      </ThemeProvider>
    </QueryClientProvider>
  );
}
