import { useQuery } from "@tanstack/react-query";
import { getAllPublicDecks } from "@/features/deck/services/deck.service";
import type { PageResponse } from "@/types/common";
import type { ICreatedDeckListItem } from "@/features/deck/types/created-deck.types";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Pobiera wszystkie publiczne talie od wszystkich użytkowników
 */
export const usePublicDecks = (params?: { page?: number; size?: number }) => {
  return useQuery<PageResponse<ICreatedDeckListItem>>({
    queryKey: [QUERY_KEYS.COMMUNITY, params?.page, params?.size],
    queryFn: () =>
      getAllPublicDecks({
        page: params?.page ?? 0,
        size: params?.size ?? 20,
      }),
    staleTime: 5 * 60 * 1000,
  });
};
