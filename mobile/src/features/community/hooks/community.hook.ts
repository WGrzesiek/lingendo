import { useQuery } from '@tanstack/react-query';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { getPublicDecks } from '../services';

/**
 * Hook do pobierania publicznych talii (community)
 */
export function usePublicDecks(params?: { page?: number; size?: number }) {
  return useQuery({
    queryKey: [QUERY_KEYS.COMMUNITY, 'publicDecks', params?.page, params?.size],
    queryFn: async () => await getPublicDecks(params),
    staleTime: 5 * 60 * 1000,
  });
}
