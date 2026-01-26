"use client";

import { useRouter } from "next/navigation";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { AxiosError } from "axios";

import type { LoginRequest, SignupRequest, User } from "../types";
import type { ApiErrorResponse } from "@/types/common";

import { CURRENT_USER_KEY } from "./useCurrentUser";
import { login, signup, logout, getCurrentUser } from "../services/auth";

/**
 * Hook do obsługi procesów logowania, rejestracji i wylogowania
 * Korzysta z React Query mutations, cache’uje usera oraz
 * automatycznie aktualizuje navbar po login/logout
 */
export const useAuth = () => {
  const router = useRouter();
  const queryClient = useQueryClient();

  const loginMutation = useMutation<
    User,
    AxiosError<ApiErrorResponse>,
    LoginRequest
  >({
    mutationFn: async (data) => {
      await login(data);
      return await getCurrentUser();
    },

    onSuccess: (user) => {
      queryClient.setQueryData(CURRENT_USER_KEY, user);

      if (user.accountType === "TEACHER") {
        router.push("/dashboard-teacher");
      } else {
        router.push("/dashboard");
      }
    },
  });

  const signupMutation = useMutation<
    void,
    AxiosError<ApiErrorResponse>,
    SignupRequest
  >({
    mutationFn: signup,
    onSuccess: () => {
      router.push("/login");
    },
  });

  const logoutMutation = useMutation<void, AxiosError<ApiErrorResponse>, void>({
    mutationFn: logout,

    onSuccess: () => {
      queryClient.setQueryData(CURRENT_USER_KEY, null);

      router.push("/login");
    },
  });

  return {
    login: loginMutation.mutate,
    loginAsync: loginMutation.mutateAsync,
    signup: signupMutation.mutate,
    signupAsync: signupMutation.mutateAsync,
    logout: logoutMutation.mutate,
    logoutAsync: logoutMutation.mutateAsync,

    isLoading:
      loginMutation.isPending ||
      signupMutation.isPending ||
      logoutMutation.isPending,

    loginError: loginMutation.error,
    signupError: signupMutation.error,
    logoutError: logoutMutation.error,
  };
};
