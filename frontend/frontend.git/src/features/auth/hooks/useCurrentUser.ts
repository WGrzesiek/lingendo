"use client";

import { useState, useEffect } from "react";
import { getCurrentUser } from "../services/auth";
import type { User } from "../types";

/**
 * Hook do pobierania danych aktualnie zalogowanego użytkownika z /me
 * Automatycznie pobiera dane przy montowaniu komponentu
 */
export const useCurrentUser = () => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        setIsLoading(true);
        const userData = await getCurrentUser();
        setUser(userData);
        setError(null);
      } catch (err) {
        console.error("Failed to fetch current user:", err);
        setError("Failed to load user data");
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUser();
  }, []);

  return { user, isLoading, error };
};
