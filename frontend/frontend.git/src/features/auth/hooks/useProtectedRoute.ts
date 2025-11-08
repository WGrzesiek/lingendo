"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useCurrentUser } from "./useCurrentUser";
import type { User } from "../types";

interface ProtectionOptions {
  /** Wymagany typ konta (opcjonalnie) */
  requiredAccountType?: User["accountType"] | User["accountType"][];
  /** Wymagany typ użytkownika (opcjonalnie) */
  requiredUserType?: User["userType"];
  /** Czy konto musi być aktywne? (domyślnie: true) */
  requireEnabled?: boolean;
  /** Ścieżka przekierowania gdy brak dostępu (domyślnie: /dashboard) */
  redirectTo?: string;
  /** Ścieżka przekierowania gdy użytkownik niezalogowany (domyślnie: /login) */
  loginRedirect?: string;
}

/**
 * Uniwersalny hook do zabezpieczania widoków
 *
 * Wykorzystuje endpoint /me do weryfikacji użytkownika i jego uprawnień.
 * Backend automatycznie sprawdza access token z cookies.
 *
 * @example
 * // Podstawowe zabezpieczenie - tylko zalogowani
 * const { user, isLoading } = useProtectedRoute();
 *
 * @example
 * // Tylko dla nauczycieli
 * const { user, isLoading } = useProtectedRoute({
 *   requiredAccountType: "TEACHER"
 * });
 *
 * @example
 * // Dla nauczycieli lub administratorów
 * const { user, isLoading } = useProtectedRoute({
 *   requiredAccountType: ["TEACHER", "STUDENT"],
 *   requiredUserType: "ADMIN"
 * });
 *
 * @example
 * // Tylko dla aktywnych użytkowników premium
 * const { user, isLoading } = useProtectedRoute({
 *   requiredAccountType: "PREMIUM",
 *   requireEnabled: true
 * });
 */
export const useProtectedRoute = (options: ProtectionOptions = {}) => {
  const {
    requiredAccountType,
    requiredUserType,
    requireEnabled = true,
    redirectTo = "/dashboard",
    loginRedirect = "/login",
  } = options;

  const router = useRouter();
  const { user, isLoading, error } = useCurrentUser();

  useEffect(() => {
    // Czekaj aż dane się załadują
    if (isLoading) return;

    // Jeśli nie ma użytkownika (niezalogowany lub błąd), przekieruj na login
    if (!user || error) {
      console.log(
        "🔒 [useProtectedRoute] Brak użytkownika, przekierowanie na login"
      );
      router.push(loginRedirect);
      return;
    }

    // Sprawdź czy konto jest aktywne
    if (requireEnabled && !user.isEnabled) {
      console.log("🔒 [useProtectedRoute] Konto nieaktywne, przekierowanie");
      router.push("/account-disabled");
      return;
    }

    // Sprawdź typ użytkownika (NORMAL/ADMIN)
    if (requiredUserType && user.userType !== requiredUserType) {
      console.log(
        `🔒 [useProtectedRoute] Nieprawidłowy userType. Wymagany: ${requiredUserType}, obecny: ${user.userType}`
      );
      router.push(redirectTo);
      return;
    }

    // Sprawdź typ konta (BASIC/PREMIUM/STUDENT/TEACHER)
    if (requiredAccountType) {
      const allowedTypes = Array.isArray(requiredAccountType)
        ? requiredAccountType
        : [requiredAccountType];

      if (!allowedTypes.includes(user.accountType)) {
        console.log(
          `🔒 [useProtectedRoute] Nieprawidłowy accountType. Wymagany: ${allowedTypes.join(
            " lub "
          )}, obecny: ${user.accountType}`
        );
        router.push(redirectTo);
        return;
      }
    }

    console.log("✅ [useProtectedRoute] Użytkownik zweryfikowany:", {
      username: user.username,
      accountType: user.accountType,
      userType: user.userType,
      isEnabled: user.isEnabled,
    });
  }, [
    user,
    isLoading,
    error,
    requiredAccountType,
    requiredUserType,
    requireEnabled,
    redirectTo,
    loginRedirect,
    router,
  ]);

  return {
    user,
    isLoading,
    error,
    /** Czy użytkownik ma dostęp (nie jest w trakcie ładowania i user istnieje) */
    hasAccess: !isLoading && !!user,
  };
};
