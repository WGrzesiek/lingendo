import { useQuery } from "@tanstack/react-query";
import { getDeckStatistics } from "../services/deck.service";
import { qk } from "@/lib/queryKeys";

/**
 * Hook do pobierania statystyk talii
 */
export const useDeckStatistics = (deckId: string) => {
  return useQuery({
    queryKey: qk.deck.statistics(deckId),
    queryFn: () => getDeckStatistics(deckId),
    enabled: !!deckId,
  });
};
