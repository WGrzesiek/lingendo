"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { TokenStore } from "@/lib/tokenStore";

/**
 * Hook do wymuszania autoryzacji na stronie.
 * Jeśli użytkownik nie jest zalogowany, zostanie przekierowany na /login
 *
 * @deprecated Użyj zamiast tego `useProtectedRoute()` - nowszy hook wykorzystujący endpoint /me
 * @example
 * // Stary sposób:
 * useRequireAuth();
 *
 * // Nowy sposób:
 * const { user, isLoading } = useProtectedRoute();
 */
export const useRequireAuth = () => {
  const router = useRouter();

  useEffect(() => {
    const token = TokenStore.get();

    if (!token) {
      router.push("/login");
    }
  }, [router]);
};
