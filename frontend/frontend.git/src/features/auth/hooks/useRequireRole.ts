"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useCurrentUser } from "./useCurrentUser";
import type { User } from "../types";

/**
 * Hook do wymuszania określonego accountType na stronie.
 * Jeśli użytkownik nie ma odpowiedniego typu konta, zostanie przekierowany.
 *
 * @deprecated Użyj zamiast tego `useProtectedRoute()` - bardziej elastyczny hook
 * @example
 * // Stary sposób:
 * const { user, isLoading } = useRequireRole("TEACHER", "NORMAL");
 *
 * // Nowy sposób:
 * const { user, isLoading } = useProtectedRoute({
 *   requiredAccountType: "TEACHER"
 * });
 *
 * @param requiredAccountType - wymagany typ konta ("TEACHER" lub "STUDENT")
 * @param requiredUserType - wymagany typ użytkownika ("NORMAL" lub "ADMIN")
 * @param redirectTo - opcjonalna ścieżka przekierowania (domyślnie /dashboard)
 */
export const useRequireRole = (
  requiredAccountType: User["accountType"],
  requiredUserType: User["userType"],
  redirectTo: string = "/dashboard"
) => {
  const router = useRouter();
  const { user, isLoading } = useCurrentUser();

  useEffect(() => {
    if (isLoading) return;

    if (!user) {
      router.push("/login");
      return;
    }

    if (user.accountType !== requiredAccountType) {
      router.push(redirectTo);
    }
  }, [user, isLoading, requiredAccountType, redirectTo, router]);

  return { user, isLoading };
};
