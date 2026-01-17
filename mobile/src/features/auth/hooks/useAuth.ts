import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { AxiosError } from 'axios';

import type { LoginRequest, SignupRequest, User, ApiErrorResponse } from '../types';
import { login, signup, logout } from '../services/auth';
import { CURRENT_USER_KEY } from './useCurrentUser';
import { router } from 'expo-router';

/**
 * Hook do obsługi procesów logowania, rejestracji i wylogowania
 * Korzysta z React Query mutations i aktualizuje cache
 */
export const useAuth = () => {
  const queryClient = useQueryClient();

  /**
   * Mutation do logowania
   */
  const loginMutation = useMutation<void, AxiosError<ApiErrorResponse>, LoginRequest>({
    mutationFn: async (data) => {
      await login(data);
    },
    onSuccess: () => {
      console.log('[useAuth] Login success, redirecting to dashboard...');
      queryClient.invalidateQueries({ queryKey: CURRENT_USER_KEY });
      router.replace('/(dashboard)/student');
    },
    onError: (error) => {
      console.error('[useAuth] Błąd logowania:', error.message);
    },
  });

  /**
   * Mutation do rejestracji
   */
  const signupMutation = useMutation<void, AxiosError<ApiErrorResponse>, SignupRequest>({
    mutationFn: signup,
    onSuccess: () => {
      console.log('[useAuth] Signup success, redirecting to login...');
      router.replace('/(auth)/login');
    },
    onError: (error) => {
      console.error('[useAuth] Błąd rejestracji:', error.message);
    },
  });

  /**
   * Mutation do wylogowania
   */
  const logoutMutation = useMutation<void, AxiosError<ApiErrorResponse>, void>({
    mutationFn: logout,
    onSuccess: () => {
      console.log('[useAuth] Logout success, redirecting to login...');
      // Wyczyść cache użytkownika
      queryClient.setQueryData(CURRENT_USER_KEY, null);
      queryClient.clear();
      router.replace('/(auth)/login');
    },
    onError: (error) => {
      console.error('[useAuth] Błąd wylogowania:', error.message);
      router.replace('/(auth)/login');
    },
  });

  return {
    // Funkcje mutacji
    login: loginMutation.mutate,
    loginAsync: loginMutation.mutateAsync,
    signup: signupMutation.mutate,
    signupAsync: signupMutation.mutateAsync,
    logout: logoutMutation.mutate,
    logoutAsync: logoutMutation.mutateAsync,

    // Stan ładowania
    isLoading: loginMutation.isPending || signupMutation.isPending || logoutMutation.isPending,
    isLoginLoading: loginMutation.isPending,
    isSignupLoading: signupMutation.isPending,
    isLogoutLoading: logoutMutation.isPending,

    // Błędy
    loginError: loginMutation.error,
    signupError: signupMutation.error,
    logoutError: logoutMutation.error,

    // Reset błędów
    resetLoginError: loginMutation.reset,
    resetSignupError: signupMutation.reset,
  };
};
