import { useQuery } from "@tanstack/react-query";
import { getDeckDetails } from "../services/deck.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania szczegółowych informacji o talii
 */
export const useDeckDetails = (deckId: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.DECKS, "details", deckId],
    queryFn: () => getDeckDetails(deckId),
    enabled: !!deckId,
  });
};
