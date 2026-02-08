import { useQuery } from "@tanstack/react-query";
import { getDeckStatistics } from "../services/deck.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania statystyk talii
 */
export const useDeckStatistics = (deckId: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.DECKS, "statistics", deckId],
    queryFn: () => getDeckStatistics(deckId),
    enabled: !!deckId,
  });
};
