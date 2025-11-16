import { useQuery } from "@tanstack/react-query";
import { getDeckStatistics } from "../services/deck.service";

/**
 * Hook do pobierania statystyk talii
 */
export const useDeckStatistics = (deckId: string) => {
  return useQuery({
    queryKey: ["deck-statistics", deckId],
    queryFn: () => getDeckStatistics(deckId),
    enabled: !!deckId,
  });
};
