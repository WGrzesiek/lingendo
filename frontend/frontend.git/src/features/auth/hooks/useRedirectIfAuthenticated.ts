"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { TokenStore } from "@/lib/tokenStore";

/**
 * Hook do przekierowywania zalogowanych użytkowników.
 * Jeśli użytkownik jest już zalogowany, zostanie przekierowany na /dashboard
 */
export const useRedirectIfAuthenticated = () => {
  const router = useRouter();

  useEffect(() => {
    const token = TokenStore.get();

    if (token) {
      router.push("/dashboard");
    }
  }, [router]);
};
