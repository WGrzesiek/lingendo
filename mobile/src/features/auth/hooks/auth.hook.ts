import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { router } from 'expo-router';
import type { AxiosError } from 'axios';

import { QUERY_KEYS, INVALIDATION_GROUPS } from '@/constants';
import { AuthService } from '../services/auth.service';
import type { LoginRequest, SignupRequest, User, ApiErrorResponse } from '../types';

/**
 * Hook autoryzacji - Object Literal Module Pattern
 * Łączy funkcjonalność logowania, rejestracji, wylogowania i pobierania użytkownika
 */
export const useAuth = () => {
  const queryClient = useQueryClient();

  // ========== QUERY: Aktualny użytkownik ==========

  const userQuery = useQuery<User | null>({
    queryKey: QUERY_KEYS.USER,
    queryFn: async () => {
      try {
        return await AuthService.getCurrentUser();
      } catch {
        console.log('[useAuth] Nie udało się pobrać użytkownika');
        return null;
      }
    },
    staleTime: 5 * 60 * 1000,
    gcTime: 30 * 60 * 1000,
    retry: 1,
  });

  // ========== MUTATIONS ==========

  const loginMutation = useMutation<void, AxiosError<ApiErrorResponse>, LoginRequest>({
    mutationFn: AuthService.login,
    onSuccess: () => {
      console.log('[useAuth] Login success, redirecting to dashboard...');
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER });
      router.replace('/(dashboard)/student');
    },
    onError: (error) => {
      console.error('[useAuth] Błąd logowania:', error.message);
    },
  });

  const signupMutation = useMutation<void, AxiosError<ApiErrorResponse>, SignupRequest>({
    mutationFn: AuthService.signup,
    onSuccess: () => {
      console.log('[useAuth] Signup success, redirecting to login...');
      router.replace('/(auth)/login');
    },
    onError: (error) => {
      console.error('[useAuth] Błąd rejestracji:', error.message);
    },
  });

  const logoutMutation = useMutation<void, AxiosError<ApiErrorResponse>, void>({
    mutationFn: AuthService.logout,
    onSuccess: () => {
      console.log('[useAuth] Logout success, clearing cache...');
      INVALIDATION_GROUPS.ON_LOGOUT.forEach((key) => {
        queryClient.removeQueries({ queryKey: key });
      });
      router.replace('/(auth)/login');
    },
    onError: (error) => {
      console.error('[useAuth] Błąd wylogowania:', error.message);
      router.replace('/(auth)/login');
    },
  });

  // ========== RETURN OBJECT ==========

  return {
    // Dane użytkownika
    user: userQuery.data ?? null,
    isAuthenticated: !!userQuery.data,
    isUserLoading: userQuery.isLoading,
    isUserError: userQuery.isError,
    refetchUser: userQuery.refetch,

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
