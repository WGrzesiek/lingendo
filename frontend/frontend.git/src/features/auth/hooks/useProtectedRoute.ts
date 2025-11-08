"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useCurrentUser } from "./useCurrentUser";
import type { User } from "../types";

/**
 * Opcje konfiguracji zabezpieczenia trasy
 */
interface ProtectionOptions {
  requiredAccountType?: User["accountType"];
  requiredUserType?: User["userType"];
  requireEnabled?: boolean;
  redirectTo?: string;
  loginRedirect?: string;
}

/**
 * Uniwersalny hook do zabezpieczania widoków przed nieautoryzowanym dostępem
 * Wykorzystuje endpoint /me do weryfikacji użytkownika i jego uprawnień
 * Backend automatycznie sprawdza access token z cookies
 * @param options - Opcje konfiguracji zabezpieczenia trasy
 * @returns Obiekt zawierający dane użytkownika, status ładowania, błędy i informację o dostępie
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
    if (isLoading) return;

    if (!user || error) {
      console.log(
        "[useProtectedRoute] Brak użytkownika, przekierowanie na login"
      );
      router.push(loginRedirect);
      return;
    }

    if (requireEnabled && !user.isEnabled) {
      console.log("[useProtectedRoute] Konto nieaktywne, przekierowanie");
      router.push("/account-disabled");
      return;
    }

    if (requiredUserType && user.userType !== requiredUserType) {
      console.log(
        `[useProtectedRoute] Nieprawidłowy userType. Wymagany: ${requiredUserType}, obecny: ${user.userType}`
      );
      router.push(redirectTo);
      return;
    }

    if (requiredAccountType && user.accountType !== requiredAccountType) {
      console.log(
        `[useProtectedRoute] Nieprawidłowy accountType. Wymagany: ${requiredAccountType}, obecny: ${user.accountType}`
      );
      router.push(redirectTo);
      return;
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
    hasAccess: !isLoading && !!user,
  };
};
