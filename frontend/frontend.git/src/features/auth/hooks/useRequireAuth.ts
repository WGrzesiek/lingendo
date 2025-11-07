"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { TokenStore } from "@/lib/tokenStore";

/**
 * Hook do wymuszania autoryzacji na stronie.
 * Jeśli użytkownik nie jest zalogowany, zostanie przekierowany na /login
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
