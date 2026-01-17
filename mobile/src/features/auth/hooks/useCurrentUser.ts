import { useQuery } from '@tanstack/react-query';
import { getCurrentUser } from '@/features/auth';
import type { User } from '../types';
import { router } from 'expo-router';

/**
 * Klucz cache dla aktualnego użytkownika
 */
export const CURRENT_USER_KEY = ['currentUser'] as const;

/**
 * Hook do pobierania danych aktualnie zalogowanego użytkownika
 * Najpierw sprawdza local storage, potem odpytuje API
 */
export const useCurrentUser = () => {
  return useQuery<User | null>({
    queryKey: CURRENT_USER_KEY,
    queryFn: async () => {
      try {
        return await getCurrentUser();
      } catch (e) {
        console.log('[useCurrentUser] Nie udało się pobrać użytkownika');
        return null;
      }
    },
    staleTime: 5 * 60 * 1000,
    gcTime: 30 * 60 * 1000,
    retry: 1,
  });
};


/**
 * Hook do sprawdzenia czy użytkownik jest zalogowany
 */
export const useIsAuthenticated = () => {
  const { data: user, isLoading } = useCurrentUser();
  return {
    isAuthenticated: !!user,
    isLoading,
    user,
  };
};
