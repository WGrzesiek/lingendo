"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { login, signup, logout } from "../services/auth";
import type { LoginRequest, SignupRequest } from "../types";
import { AxiosError } from "axios";

export const useAuth = () => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async (data: LoginRequest) => {
    setIsLoading(true);
    setError(null);

    try {
      await login(data);
      router.push("/dashboard");
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

  const handleSignup = async (data: SignupRequest) => {
    setIsLoading(true);
    setError(null);

    try {
      await signup(data);
      router.push("/signin");
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

  const handleLogout = async () => {
    setIsLoading(true);

    try {
      await logout();
      router.push("/signin");
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
