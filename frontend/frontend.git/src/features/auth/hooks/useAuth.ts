"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { login, signup, logout, getCurrentUser } from "../services/auth";
import type { LoginRequest, SignupRequest } from "../types";
import { AxiosError } from "axios";

/**
 * Hook do zarządzania procesami uwierzytelniania użytkownika
 * Obsługuje logowanie, rejestrację i wylogowanie
 * Automatycznie przekierowuje użytkownika do odpowiedniego dashboardu na podstawie roli
 */
export const useAuth = () => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Obsługuje proces logowania użytkownika
   * Po udanym logowaniu pobiera dane użytkownika i przekierowuje do odpowiedniego dashboardu
   * @param data - Dane logowania (username i password)
   */
  const handleLogin = async (data: LoginRequest) => {
    setIsLoading(true);
    setError(null);

    try {
      await login(data);
      const user = await getCurrentUser();
      if (user.accountType === "TEACHER") {
        router.push("/dashboard-teacher");
      } else {
        router.push("/dashboard");
      }
    } catch (err) {
      const message =
        err instanceof AxiosError
          ? err.response?.data?.message || "Login failed"
          : "Login failed";
      setError(message);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Obsługuje proces rejestracji nowego użytkownika
   * Po udanej rejestracji przekierowuje do strony logowania
   * @param data - Dane rejestracyjne (email, password, opcjonalnie name)
   */
  const handleSignup = async (data: SignupRequest) => {
    setIsLoading(true);
    setError(null);

    try {
      await signup(data);
      router.push("/login");
    } catch (err) {
      const message =
        err instanceof AxiosError
          ? err.response?.data?.message || "Signup failed"
          : "Signup failed";
      setError(message);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Obsługuje proces wylogowania użytkownika
   * Usuwa token uwierzytelniający i przekierowuje do strony logowania
   */
  const handleLogout = async () => {
    setIsLoading(true);

    try {
      await logout();
      router.push("/login");
    } catch (err) {
      console.error("Logout error:", err);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    login: handleLogin,
    signup: handleSignup,
    logout: handleLogout,
    isLoading,
    error,
  };
};
