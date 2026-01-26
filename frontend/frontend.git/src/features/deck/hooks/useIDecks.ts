import { useQuery } from "@tanstack/react-query";
import { getIDecks } from "../services/deck.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania statystyk talii
 */
export const useIDecks = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: [QUERY_KEYS.DECKS, "iDecks", page, size],
    queryFn: () => getIDecks({ page, size }),
  });
};
